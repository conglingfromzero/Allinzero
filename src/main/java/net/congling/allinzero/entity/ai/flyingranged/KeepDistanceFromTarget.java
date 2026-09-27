package net.congling.allinzero.entity.ai.flyingranged;

import com.mojang.datafixers.util.Pair;
import net.minecraft.core.BlockPos;
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
 * 与攻击目标保持距离的飞行行为：持续与目标保持 {@code [minDistance, maxDistance]} 格距离。
 * <ul>
 *     <li>距离 &gt; max：追近（速度随接近程度衰减，避免惯性冲进区间内）</li>
 *     <li>距离 &lt; min：后撤（远离目标的空中点）</li>
 *     <li>区间内：悬停为主，间歇沿切向小幅环绕</li>
 * </ul>
 * 通过 {@code WALK_TARGET} 内存驱动核心任务中的 {@code MoveToWalkTarget}，避免多个行为直接争抢导航。
 * 战斗追击/后撤速度修正最高为 {@code 1.0}（与原 RangedAttackGoal 一致，不降低机动性），
 * 环绕用低速修正 {@code 0.15}（约每秒 3 格）。
 * <p>
 * 规避行为（{@link DodgePlayerAim}）激活期间本行为让出移动权，保证规避机动不被干扰。
 *
 * @param <E> 实体类型
 */
public class KeepDistanceFromTarget<E extends PathfinderMob> extends ExtendedBehaviour<E> {
	private static final MemoryTest MEMORY_REQUIREMENTS = MemoryTest.builder(1).hasMemory(MemoryModuleType.ATTACK_TARGET);

	private final float minDistance;
	private final float maxDistance;
	@Nullable
	private final DodgePlayerAim<E> dodge;

	protected float orbitSpeedModifier = 0.15f;

	private int orbitDelay = 0;

	public KeepDistanceFromTarget(float minDistance, float maxDistance) {
		this(minDistance, maxDistance, null);
	}

	/**
	 * @param minDistance 允许的最小距离（格），更近则后撤
	 * @param maxDistance 允许的最大距离（格），更远则追近
	 * @param dodge       配套的规避行为，用于在规避机动期间让出移动权（可为 null）
	 */
	public KeepDistanceFromTarget(float minDistance, float maxDistance, @Nullable DodgePlayerAim<E> dodge) {
		this.minDistance = minDistance;
		this.maxDistance = maxDistance;
		this.dodge = dodge;
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

		// 规避机动期间让出移动权：清掉游荡遗留的移动目标，避免干扰规避直线机动
		if (this.dodge != null && this.dodge.isDodging()) {
			BrainUtils.clearMemory(entity, MemoryModuleType.WALK_TARGET);

			return;
		}

		// 全程面向目标，便于瞄准射击
		BrainUtils.setMemory(entity, MemoryModuleType.LOOK_TARGET, new EntityTracker(target, true));

		double dist = entity.distanceTo(target);

		if (dist > this.maxDistance) {
			// 追近：越接近区间上界速度越低，平滑进入悬停区间
			BrainUtils.setMemory(entity, MemoryModuleType.WALK_TARGET,
					new WalkTarget(new EntityTracker(target, true), approachSpeed(dist), 1));
		}
		else if (dist < this.minDistance) {
			// 后撤：远离目标的空中点
			Vec3 awayPos = getRetreatPos(entity, target);

			if (awayPos != null)
				BrainUtils.setMemory(entity, MemoryModuleType.WALK_TARGET,
						new WalkTarget(awayPos, retreatSpeed(dist), 0));
			else
				BrainUtils.clearMemory(entity, MemoryModuleType.WALK_TARGET);
		}
		else {
			// 区间内：悬停为主，间歇沿切向小幅环绕
			if (this.orbitDelay > 0)
				this.orbitDelay--;

			if (this.orbitDelay <= 0) {
				Vec3 orbitPos = getOrbitPos(entity);

				if (orbitPos != null) {
					BrainUtils.setMemory(entity, MemoryModuleType.WALK_TARGET,
							new WalkTarget(orbitPos, this.orbitSpeedModifier, 1));
					this.orbitDelay = 60 + entity.getRandom().nextInt(40);
				}
				else {
					this.orbitDelay = 20;
				}
			}
		}
	}

	/**
	 * 追近速度：远离上界时全速（1.0），接近上界时逐步降到低速。
	 */
	protected float approachSpeed(double dist) {
		return (float) (0.3f + 0.7f * Math.min(1.0, (dist - this.maxDistance) / 10.0));
	}

	/**
	 * 后撤速度：越贴近下界越快拉开距离。
	 */
	protected float retreatSpeed(double dist) {
		return (float) (0.3f + 0.7f * Math.min(1.0, (this.minDistance - dist) / 8.0));
	}

	/**
	 * 计算后撤点：优先用 DefaultRandomPos 取远离目标的点，失败则沿目标反方向手动取空气点。
	 */
	@Nullable
	protected Vec3 getRetreatPos(E entity, LivingEntity target) {
		Vec3 awayPos = DefaultRandomPos.getPosAway(entity, 6, 4, target.position());

		if (awayPos != null && entity.level().getBlockState(BlockPos.containing(awayPos)).isAir())
			return awayPos;

		// 兜底：沿“实体 - 目标”的水平反方向取 5 格外的点
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
	 * 计算小幅环绕点：保持当前到目标的水平方位，沿切向漂移 3~5 格（半径基本不变）。
	 */
	@Nullable
	protected Vec3 getOrbitPos(E entity) {
		LivingEntity target = BrainUtils.getTargetOfEntity(entity);

		if (target == null)
			return null;

		Vec3 radial = entity.position().subtract(target.position());
		Vec3 tangent;

		if (radial.horizontalDistanceSqr() < 1.0E-4)
			tangent = new Vec3(1, 0, 0);
		else
			tangent = new Vec3(-radial.z, 0, radial.x).normalize();

		tangent = tangent.scale(entity.getRandom().nextBoolean() ? 1 : -1);

		Vec3 candidate = entity.position()
				.add(tangent.scale(3 + entity.getRandom().nextInt(3)))
				.add(0, entity.getRandom().nextInt(3) - 1, 0);

		return entity.level().getBlockState(BlockPos.containing(candidate)).isAir() ? candidate : null;
	}
}
