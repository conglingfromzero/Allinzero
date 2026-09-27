package net.congling.allinzero.entity.ai.hostile;

import com.mojang.datafixers.util.Pair;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.behavior.EntityTracker;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.memory.WalkTarget;
import net.congling.allinzero.entity.ai.flyingranged.DodgePlayerAim;
import net.tslat.smartbrainlib.api.core.behaviour.ExtendedBehaviour;
import net.tslat.smartbrainlib.object.MemoryTest;
import net.tslat.smartbrainlib.util.BrainUtils;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * 飞行远程“分层接近”行为：按与目标的距离档位以不同速度逼近，替代固定距离悬停逻辑。
 * 默认档位（可经 {@link #tierSpeeds} 调整）：
 * <ul>
 *     <li>距离 ≥ 64 格：0.46（≈ 10 格/秒）高速接近</li>
 *     <li>32 ~ 64 格：0.23（≈ 5 格/秒）中速接近</li>
 *     <li>20 ~ 32 格：0.14（≈ 3 格/秒）低速接近并锁定目标准备攻击（悬停姿态，允许射击）</li>
 *     <li>≤ 20 格：停止接近，原地悬停（仍可射击）</li>
 * </ul>
 * <p>
 * 飞行速度校准（已验证结论，来自 vanilla {@code FlyingMoveControl} + travel(moveRelative) 0.91 阻尼链分析）：
 * 终端速度 ≈ {@code 21.6 × speedModifier} 格/秒。因此 0.46 ≈ 10 格/秒、0.23 ≈ 5 格/秒、0.14 ≈ 3 格/秒、
 * 0.046 ≈ 1 格/秒。
 * <p>
 * 通过 {@code WALK_TARGET} 内存驱动核心任务中的 {@code MoveToWalkTarget} 完成飞行导航；
 * 每 tick 按距离重新分档刷新速度。全程面向目标便于瞄准。
 * 规避行为（{@link DodgePlayerAim}）激活期间本行为让出移动权，保证规避机动不被干扰。
 *
 * @param <E> 实体类型
 */
public class TieredApproachTarget<E extends PathfinderMob> extends ExtendedBehaviour<E> {
	private static final MemoryTest MEMORY_REQUIREMENTS = MemoryTest.builder(1).hasMemory(MemoryModuleType.ATTACK_TARGET);

	/** 档位边界（格）：≥ far 高速、≥ mid 中速、≥ near 低速锁定、否则悬停 */
	protected float farDistance = 64.0f;
	protected float midDistance = 32.0f;
	protected float nearDistance = 20.0f;
	/** 远档速度修正：0.46 ≈ 10 格/秒 */
	protected float farSpeedModifier = 0.46f;
	/** 中档速度修正：0.23 ≈ 5 格/秒 */
	protected float midSpeedModifier = 0.23f;
	/** 近档速度修正：0.14 ≈ 3 格/秒（锁定悬停前最后一次逼近） */
	protected float nearSpeedModifier = 0.14f;
	@Nullable
	private final DodgePlayerAim<E> dodge;

	public TieredApproachTarget() {
		this(null);
	}

	/**
	 * @param dodge 配套的规避行为，用于在规避机动期间让出移动权（可为 null）
	 */
	public TieredApproachTarget(@Nullable DodgePlayerAim<E> dodge) {
		this.dodge = dodge;

		noTimeout();
	}

	/**
	 * 设置三档速度修正（远 / 中 / 近，0.046 ≈ 1 格/秒，每 +0.046 约多 1 格/秒）。
	 */
	public TieredApproachTarget<E> tierSpeeds(float far, float mid, float near) {
		this.farSpeedModifier = far;
		this.midSpeedModifier = mid;
		this.nearSpeedModifier = near;

		return this;
	}

	/**
	 * 设置档位边界（格）：远 / 中 / 近。
	 */
	public TieredApproachTarget<E> tierDistances(float far, float mid, float near) {
		this.farDistance = far;
		this.midDistance = mid;
		this.nearDistance = near;

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

		// 规避机动期间让出移动权：清掉移动目标，避免干扰规避直线机动
		if (this.dodge != null && this.dodge.isDodging()) {
			BrainUtils.clearMemory(entity, MemoryModuleType.WALK_TARGET);

			return;
		}

		// 全程面向目标，便于瞄准射击
		BrainUtils.setMemory(entity, MemoryModuleType.LOOK_TARGET, new EntityTracker(target, true));

		double dist = entity.distanceTo(target);

		if (dist >= this.farDistance) {
			// 远档：10 格/秒高速接近
			BrainUtils.setMemory(entity, MemoryModuleType.WALK_TARGET,
					new WalkTarget(new EntityTracker(target, true), this.farSpeedModifier, 1));
		}
		else if (dist >= this.midDistance) {
			// 中档：5 格/秒接近
			BrainUtils.setMemory(entity, MemoryModuleType.WALK_TARGET,
					new WalkTarget(new EntityTracker(target, true), this.midSpeedModifier, 1));
		}
		else if (dist >= this.nearDistance) {
			// 近档：3 格/秒低速逼近，锁定目标准备攻击（允许射击）
			BrainUtils.setMemory(entity, MemoryModuleType.WALK_TARGET,
					new WalkTarget(new EntityTracker(target, true), this.nearSpeedModifier, 1));
		}
		else {
			// 进入最内圈：停止接近，原地悬停（仍可射击）
			BrainUtils.clearMemory(entity, MemoryModuleType.WALK_TARGET);
		}
	}

	@Override
	protected void stop(E entity) {
		BrainUtils.clearMemory(entity, MemoryModuleType.WALK_TARGET);
	}
}
