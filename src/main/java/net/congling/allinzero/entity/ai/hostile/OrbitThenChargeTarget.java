package net.congling.allinzero.entity.ai.hostile;

import com.mojang.datafixers.util.Pair;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.behavior.EntityTracker;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.memory.WalkTarget;
import net.minecraft.world.entity.ai.util.DefaultRandomPos;
import net.minecraft.world.phys.Vec3;
import net.tslat.smartbrainlib.api.core.behaviour.ExtendedBehaviour;
import net.tslat.smartbrainlib.object.MemoryTest;
import net.tslat.smartbrainlib.util.BrainUtils;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * 飞行近战“环绕 + 突进”行为（配合 {@code FlyingMeleeHostileEntity} 使用）。
 * <p>
 * 行为循环（状态机）：
 * <ol>
 *     <li><b>环绕阶段</b>：与目标保持 {@code [minOrbitDistance, maxOrbitDistance]}（默认 10~15 格）距离，
 *     区间内沿切向同向环绕（间歇下发环绕点），区间外追近/后撤；环绕持续 {@code firstOrbitTicks}（首轮 200 tick = 10 秒），
 *     之后每轮 {@code reorbitTicks}（300 tick = 15 秒）；环绕计时结束时若已进入可攻击范围（默认 24 格）则转入突进，
 *     超出 24 格则不发起突进、继续追近直至重新进入范围</li>
 *     <li><b>突进阶段</b>：直接驱动 {@code moveControl}（不经 WALK_TARGET）以
 *     {@code chargeSpeedModifier}（默认 0.7）直线扑向目标当前位置；命中判定 = 进入本体攻击触及距离
 *     （{@code Mob#isWithinMeleeAttackRange}），命中即造成 {@code chargeDamage}（默认 10 点，固定值，
 *     走 {@code damageSources().mobAttack(this)}，不经 ATTACK_DAMAGE 属性结算），随后拉回环绕阶段</li>
 *     <li><b>未命中</b>：突进超时（默认 40 tick ≈ 2 秒）或冲过头（越过最近点后又拉开 {@code OVERSHOOT_DISTANCE} 格）
 *     即视为未命中，立刻拉回 10~15 格距离并环绕 15 秒后再次突进</li>
 * </ol>
 * <p>
 * 飞行速度校准（已验证结论，来自 vanilla {@code FlyingMoveControl} + travel(moveRelative) 0.91 阻尼链分析）：
 * 终端速度 ≈ {@code 21.6 × speedModifier} 格/秒。因此 0.7 ≈ 15 格/秒（突进）、
 * 0.46 ≈ 10 格/秒、0.23 ≈ 5 格/秒、0.14 ≈ 3 格/秒、0.046 ≈ 1 格/秒。
 * <p>
 * 环绕/追近阶段通过 {@code WALK_TARGET} 内存驱动核心任务中的 {@code MoveToWalkTarget} 完成导航；
 * 突进阶段独占 {@code moveControl}，开始时清空 WALK_TARGET 并停掉导航以避免两者争抢。
 *
 * @param <E> 实体类型
 */
public class OrbitThenChargeTarget<E extends PathfinderMob> extends ExtendedBehaviour<E> {
	private static final MemoryTest MEMORY_REQUIREMENTS = MemoryTest.builder(1).hasMemory(MemoryModuleType.ATTACK_TARGET);

	/** 冲过头判定：距目标最近点之后又拉开该距离（格）即视为冲过头 */
	private static final float OVERSHOOT_DISTANCE = 4.0f;

	protected float minOrbitDistance = 10.0f;
	protected float maxOrbitDistance = 15.0f;
	/** 突进发起上限（格）：超出该距离不发起突进 */
	protected float maxAttackDistance = 24.0f;
	/** 突进速度修正：0.7 ≈ 21.6 × 0.7 ≈ 15 格/秒 */
	protected float chargeSpeedModifier = 0.7f;
	/** 追近速度修正（环绕阶段目标跑出外圈时使用）：≈ 15 格/秒 */
	protected float pursueSpeedModifier = 0.7f;
	/** 环绕切向速度修正：0.23 ≈ 5 格/秒 */
	protected float orbitSpeedModifier = 0.23f;
	/** 后撤速度修正：≈ 6.5 格/秒 */
	protected float retreatSpeedModifier = 0.3f;
	/** 突进命中伤害（固定值，非 ATTACK_DAMAGE 属性） */
	protected float chargeDamage = 10.0f;
	/** 突进超时（tick）：约 2 秒 */
	protected int chargeTimeoutTicks = 40;
	/** 首轮环绕时长（tick）：10 秒 */
	protected int firstOrbitTicks = 200;
	/** 后续每轮环绕时长（tick）：15 秒 */
	protected int reorbitTicks = 300;

	/** 是否处于突进阶段 */
	protected boolean charging = false;
	/** 环绕阶段剩余 tick 数 */
	private int orbitCountdown = 0;
	/** 突进剩余 tick 数 */
	private int chargeTicksLeft = 0;
	/** 本次突进中与目标的最近距离（用于冲过头判定） */
	private float minChargeDistance = Float.MAX_VALUE;
	/** 环绕侧向（+1/-1），同一目标交战期间保持同向环绕 */
	private int orbitSide = 1;
	/** 下一次环绕点下发的倒计时 */
	private int orbitDelay = 0;

	public OrbitThenChargeTarget() {
		// 行为依赖 ATTACK_TARGET 内存持续运行，交由 InvalidateAttackTarget 决定停止，不设运行时长上限
		noTimeout();
	}

	/**
	 * 设置环绕距离带与突进发起上限（格）。
	 */
	public OrbitThenChargeTarget<E> orbitDistance(float min, float max, float attackRange) {
		this.minOrbitDistance = min;
		this.maxOrbitDistance = max;
		this.maxAttackDistance = attackRange;

		return this;
	}

	/**
	 * 设置突进速度修正与命中伤害（固定值）。
	 */
	public OrbitThenChargeTarget<E> chargeStats(float speedModifier, float damage) {
		this.chargeSpeedModifier = speedModifier;
		this.chargeDamage = damage;

		return this;
	}

	/**
	 * 设置环绕时长：首轮与后续每轮（tick）。
	 */
	public OrbitThenChargeTarget<E> orbitDurations(int firstTicks, int reorbitTicks) {
		this.firstOrbitTicks = firstTicks;
		this.reorbitTicks = reorbitTicks;

		return this;
	}

	@Override
	protected List<Pair<MemoryModuleType<?>, MemoryStatus>> getMemoryRequirements() {
		return MEMORY_REQUIREMENTS;
	}

	@Override
	protected void start(E entity) {
		// 新一轮交战：先环绕 10 秒（200 tick），此后每轮 15 秒
		this.charging = false;
		this.orbitCountdown = this.firstOrbitTicks;
		this.orbitDelay = 0;
		this.orbitSide = entity.getRandom().nextBoolean() ? 1 : -1;
	}

	@Override
	protected boolean shouldKeepRunning(E entity) {
		return BrainUtils.hasMemory(entity, MemoryModuleType.ATTACK_TARGET);
	}

	@Override
	protected void tick(E entity) {
		LivingEntity target = BrainUtils.getTargetOfEntity(entity);

		if (target == null)
			return;

		if (this.charging) {
			tickCharge(entity, target);

			return;
		}

		tickOrbit(entity, target);
	}

	/**
	 * 环绕阶段：保持 10~15 格距离带并沿切向环绕；计时结束后进入突进（需在 24 格可攻击范围内）。
	 */
	private void tickOrbit(E entity, LivingEntity target) {
		this.orbitCountdown--;

		// 全程面向目标，便于观察与后续突进
		BrainUtils.setMemory(entity, MemoryModuleType.LOOK_TARGET, new EntityTracker(target, true));

		double dist = entity.distanceTo(target);

		if (this.orbitCountdown <= 0) {
			if (dist <= this.maxAttackDistance) {
				// 环绕计时结束且进入可攻击范围：发起突进
				beginCharge(entity);

				return;
			}

			// 超出 24 格不发起突进：全速追近，重新进入范围后再突进
			BrainUtils.setMemory(entity, MemoryModuleType.WALK_TARGET,
					new WalkTarget(new EntityTracker(target, true), this.pursueSpeedModifier, 1));

			return;
		}

		if (dist > this.maxOrbitDistance) {
			// 目标跑出外圈：追近（速度随接近程度衰减，避免惯性冲进区间）
			float speed = (float) (0.3f + 0.7f * Math.min(1.0, (dist - this.maxOrbitDistance) / 10.0));

			BrainUtils.setMemory(entity, MemoryModuleType.WALK_TARGET,
					new WalkTarget(new EntityTracker(target, true), Math.min(speed, this.pursueSpeedModifier), 1));
		}
		else if (dist < this.minOrbitDistance) {
			// 目标贴脸：后撤拉开距离
			Vec3 awayPos = getRetreatPos(entity, target);

			if (awayPos != null)
				BrainUtils.setMemory(entity, MemoryModuleType.WALK_TARGET,
						new WalkTarget(awayPos, this.retreatSpeedModifier, 0));
			else
				BrainUtils.clearMemory(entity, MemoryModuleType.WALK_TARGET);
		}
		else {
			// 距离带内：间歇沿切向环绕（同向，保持可预判）
			if (this.orbitDelay > 0)
				this.orbitDelay--;

			if (this.orbitDelay <= 0) {
				Vec3 orbitPos = getOrbitPos(entity, target);

				if (orbitPos != null) {
					BrainUtils.setMemory(entity, MemoryModuleType.WALK_TARGET,
							new WalkTarget(orbitPos, this.orbitSpeedModifier, 1));
					this.orbitDelay = 20 + entity.getRandom().nextInt(20);
				}
				else {
					// 当前环绕方向被阻挡，换向重试
					this.orbitSide = -this.orbitSide;
					this.orbitDelay = 10;
				}
			}
		}
	}

	/**
	 * 突进阶段：每 tick 直接驱动 moveControl 高速直线扑向目标当前位置；
	 * 命中（进入攻击触及距离）即结算固定伤害并拉回，超时或冲过头视为未命中。
	 */
	private void tickCharge(E entity, LivingEntity target) {
		BrainUtils.setMemory(entity, MemoryModuleType.LOOK_TARGET, new EntityTracker(target, true));

		double dist = entity.distanceTo(target);

		if (dist < this.minChargeDistance)
			this.minChargeDistance = (float) dist;

		// 直接驱动 moveControl 直线扑向目标当前位置（约 15 格/秒）
		entity.getMoveControl().setWantedPosition(
				target.getX(), target.getY(target.getBbHeight() * 0.5), target.getZ(), this.chargeSpeedModifier);

		// 命中判定：与目标距离小于本体攻击触及距离（攻击包围盒相交）
		if (entity.isWithinMeleeAttackRange(target)) {
			target.hurt(entity.damageSources().mobAttack(entity), this.chargeDamage);
			entity.swing(InteractionHand.MAIN_HAND);
			endCharge(entity);

			return;
		}

		this.chargeTicksLeft--;

		// 未命中判定：超时（约 2 秒）或冲过头（越过最近点后又拉开 OVERSHOOT_DISTANCE 格）
		if (this.chargeTicksLeft <= 0 || dist > this.minChargeDistance + OVERSHOOT_DISTANCE)
			endCharge(entity);
	}

	/**
	 * 开始突进：独占 moveControl，清空 WALK_TARGET 并停掉导航，避免与 MoveToWalkTarget 争抢。
	 */
	private void beginCharge(E entity) {
		this.charging = true;
		this.chargeTicksLeft = this.chargeTimeoutTicks;
		this.minChargeDistance = Float.MAX_VALUE;

		entity.getNavigation().stop();
		BrainUtils.clearMemory(entity, MemoryModuleType.WALK_TARGET);
		entity.swing(InteractionHand.MAIN_HAND);
	}

	/**
	 * 结束突进（无论命中与否）：立刻拉回 10~15 格距离并环绕 15 秒（300 tick）后再次突进。
	 */
	private void endCharge(E entity) {
		this.charging = false;
		this.orbitCountdown = this.reorbitTicks;
		this.orbitDelay = 0;

		entity.getNavigation().stop();
		BrainUtils.clearMemory(entity, MemoryModuleType.WALK_TARGET);
	}

	@Override
	protected void stop(E entity) {
		this.charging = false;
		this.chargeTicksLeft = 0;
		this.orbitCountdown = 0;

		BrainUtils.clearMemory(entity, MemoryModuleType.WALK_TARGET);
	}

	/**
	 * 计算后撤点：优先用 DefaultRandomPos 取远离目标的点，失败则沿“实体 - 目标”水平反方向取空气点。
	 */
	@Nullable
	protected Vec3 getRetreatPos(E entity, LivingEntity target) {
		Vec3 awayPos = DefaultRandomPos.getPosAway(entity, 6, 4, target.position());

		if (awayPos != null && entity.level().getBlockState(BlockPos.containing(awayPos)).isAir())
			return awayPos;

		Vec3 dir = entity.position().subtract(target.position());
		Vec3 horizontal = new Vec3(dir.x, 0, dir.z);

		if (horizontal.lengthSqr() < 1.0E-4)
			horizontal = new Vec3(0, 0, 1);
		else
			horizontal = horizontal.normalize();

		Vec3 candidate = entity.position().add(horizontal.scale(5));

		return entity.level().getBlockState(BlockPos.containing(candidate)).isAir() ? candidate : null;
	}

	/**
	 * 计算环绕点：保持当前到目标的水平方位，沿切向同向漂移 3~5 格（半径基本不变，垂直方向微调）。
	 */
	@Nullable
	protected Vec3 getOrbitPos(E entity, LivingEntity target) {
		Vec3 radial = entity.position().subtract(target.position());
		Vec3 tangent;

		if (radial.horizontalDistanceSqr() < 1.0E-4)
			tangent = new Vec3(1, 0, 0);
		else
			tangent = new Vec3(-radial.z, 0, radial.x).normalize();

		tangent = tangent.scale(this.orbitSide);

		Vec3 candidate = entity.position()
				.add(tangent.scale(3 + entity.getRandom().nextInt(3)))
				.add(0, entity.getRandom().nextInt(3) - 1, 0);

		return entity.level().getBlockState(BlockPos.containing(candidate)).isAir() ? candidate : null;
	}
}
