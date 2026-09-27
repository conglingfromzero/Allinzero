package net.congling.allinzero.entity.ai.hostile;

import com.mojang.datafixers.util.Pair;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.behavior.EntityTracker;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.memory.WalkTarget;
import net.tslat.smartbrainlib.api.core.behaviour.ExtendedBehaviour;
import net.tslat.smartbrainlib.object.MemoryTest;
import net.tslat.smartbrainlib.util.BrainUtils;

import java.util.List;

/**
 * 地面近战“冲撞接近”行为（配合 {@code GroundMeleeHostileEntity} 使用）。
 * <ul>
 *     <li>距离 &gt; {@code chargeDistance}（默认 36 格）：以 {@code chargeSpeedModifier}（默认 1.36 ≈ 5 格/秒）
 *     冲向目标，冲撞接触（进入本体攻击触及距离）时造成 {@code chargeDamage}（默认 8 点，固定值，
 *     走 {@code damageSources().mobAttack(this)}，不经 ATTACK_DAMAGE 属性结算），
 *     命中后 {@code damageCooldownTicks}（默认 20 tick = 1 秒）内不重复结算</li>
 *     <li>距离 ≤ 36 格：以 {@code walkSpeedModifier}（默认 0.61 ≈ 1 格/秒）正常接近，
 *     贴近后的实际攻击交由战斗组中的 {@code AnimatableMeleeAttack}（伤害走 ATTACK_DAMAGE 属性）</li>
 * </ul>
 * <p>
 * <b>地面速度校准公式</b>（自 vanilla 1.21.1 源码 travel/摩擦链推导）：
 * {@code Mob.setSpeed(k×M)} 同时写入 speed 字段与 zza（Mob.java）；aiStep 中 {@code zza *= 0.98}；
 * travel → handleRelativeFrictionAndCalculateMovement → moveRelative(getFrictionInfluencedSpeed(0.6), input)，
 * 普通地面摩擦系数 0.6 时地面加速系数 = speed × (0.216 / 0.6³) = speed，且输入向量长度 0.98kM &lt; 1 不会被归一化，
 * 故每 tick 加速度 a = 0.98 × k² × M²；普通地面每 tick 水平阻尼 = 0.6 × 0.91 = 0.546。
 * 实际位移速度（move 时）v = a × (0.546/(1-0.546) + 1) = a × 2.2026，即：
 * <blockquote>{@code 格/秒 ≈ 43.2 × speedModifier² × MOVEMENT_SPEED²}</blockquote>
 * 验证：僵尸 M=0.23、k=1 → ≈2.3 格/秒（与实测 ≈2.4 一致）；玩家输入=1、M=0.1 → 4.32 格/秒 = 官方步行速度 4.317。
 * 本实体 M=0.25 时：k=1 → 2.70 格/秒；5 格/秒 → k=1.36；1 格/秒 → k=0.61。
 * <p>
 * 移动全程通过 {@code WALK_TARGET} 内存驱动核心任务中的 {@code MoveToWalkTarget} 完成导航
 * （每 tick 刷新，目标移动时自动重新寻路）。
 *
 * @param <E> 实体类型
 */
public class ChargeApproach<E extends PathfinderMob> extends ExtendedBehaviour<E> {
	private static final MemoryTest MEMORY_REQUIREMENTS = MemoryTest.builder(1).hasMemory(MemoryModuleType.ATTACK_TARGET);

	/** 冲撞距离阈值（格）：超过该距离进入冲撞接近 */
	protected float chargeDistance = 36.0f;
	/** 冲撞速度修正：M=0.25 时 1.36 ≈ 5 格/秒 */
	protected float chargeSpeedModifier = 1.36f;
	/** 正常接近速度修正：M=0.25 时 0.61 ≈ 1 格/秒 */
	protected float walkSpeedModifier = 0.61f;
	/** 冲撞接触伤害（固定值，非 ATTACK_DAMAGE 属性） */
	protected float chargeDamage = 8.0f;
	/** 冲撞伤害结算冷却（tick）：1 秒内不重复结算 */
	protected int damageCooldownTicks = 20;

	/** 当前是否处于冲撞接近阶段 */
	protected boolean charging = false;
	/** 冲撞伤害冷却剩余 tick 数 */
	private int damageCooldown = 0;

	public ChargeApproach() {
		noTimeout();
	}

	/**
	 * 设置冲撞距离阈值、冲撞/正常接近速度修正（格/秒见类注释公式）。
	 */
	public ChargeApproach<E> approachStats(float chargeDistance, float chargeSpeed, float walkSpeed) {
		this.chargeDistance = chargeDistance;
		this.chargeSpeedModifier = chargeSpeed;
		this.walkSpeedModifier = walkSpeed;

		return this;
	}

	/**
	 * 设置冲撞接触伤害与结算冷却（tick）。
	 */
	public ChargeApproach<E> chargeDamage(float damage, int cooldownTicks) {
		this.chargeDamage = damage;
		this.damageCooldownTicks = cooldownTicks;

		return this;
	}

	@Override
	protected List<Pair<MemoryModuleType<?>, MemoryStatus>> getMemoryRequirements() {
		return MEMORY_REQUIREMENTS;
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

		if (this.damageCooldown > 0)
			this.damageCooldown--;

		// 全程面向目标
		BrainUtils.setMemory(entity, MemoryModuleType.LOOK_TARGET, new EntityTracker(target, true));

		double dist = entity.distanceTo(target);

		if (dist > this.chargeDistance) {
			// 冲撞接近阶段：5 格/秒冲向目标
			this.charging = true;
			BrainUtils.setMemory(entity, MemoryModuleType.WALK_TARGET,
					new WalkTarget(new EntityTracker(target, true), this.chargeSpeedModifier, 1));

			// 冲撞接触结算：进入攻击触及距离且不在冷却中
			if (this.damageCooldown <= 0 && entity.isWithinMeleeAttackRange(target)) {
				target.hurt(entity.damageSources().mobAttack(entity), this.chargeDamage);
				entity.swing(InteractionHand.MAIN_HAND);
				this.damageCooldown = this.damageCooldownTicks;
			}
		}
		else {
			// 正常接近阶段：1 格/秒贴近，攻击交由 AnimatableMeleeAttack
			this.charging = false;
			BrainUtils.setMemory(entity, MemoryModuleType.WALK_TARGET,
					new WalkTarget(new EntityTracker(target, true), this.walkSpeedModifier, 1));
		}
	}

	@Override
	protected void stop(E entity) {
		this.charging = false;
		this.damageCooldown = 0;

		BrainUtils.clearMemory(entity, MemoryModuleType.WALK_TARGET);
	}
}
