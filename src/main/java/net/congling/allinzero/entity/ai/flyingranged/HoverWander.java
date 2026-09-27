package net.congling.allinzero.entity.ai.flyingranged;

import com.mojang.datafixers.util.Pair;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.memory.WalkTarget;
import net.minecraft.world.phys.Vec3;
import net.tslat.smartbrainlib.api.core.behaviour.ExtendedBehaviour;
import net.tslat.smartbrainlib.object.MemoryTest;
import net.tslat.smartbrainlib.util.BrainUtils;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.BiFunction;

/**
 * 空中游荡行为：无攻击目标时在周围随机选取一个空中点写入 {@code WALK_TARGET} 内存，
 * 由核心任务中的 {@code MoveToWalkTarget} 完成飞行导航。
 * <p>
 * 默认速度修正 {@code 0.15} 的校准依据：Phosphor 的 {@code travel} 使用
 * {@code moveRelative(MOVEMENT_SPEED, travelVector)}，而 {@code FlyingMoveControl} 在空中
 * 设 {@code zza = speedModifier * FLYING_SPEED}，每 tick 速度增量 ≈ {@code speedModifier * 0.33 * 0.33}，
 * 配合 {@code 0.91} 阻尼，终端速度 ≈ {@code 21.6 * speedModifier} 格/秒。
 * 取 {@code 0.15} 约等于每秒 3 格，不会影响战斗时（speedModifier 1.0）的机动性。
 *
 * @param <E> 实体类型
 */
public class HoverWander<E extends PathfinderMob> extends ExtendedBehaviour<E> {
	private static final MemoryTest MEMORY_REQUIREMENTS = MemoryTest.builder(1).noMemory(MemoryModuleType.WALK_TARGET);

	protected BiFunction<E, Vec3, Float> speedModifier = (entity, targetPos) -> 0.15f;
	protected int horizontalRadius = 5;
	protected int verticalRadius = 3;

	public HoverWander() {
		// 两次游荡之间留少量间隔，让实体有空悬停观察
		cooldownFor(entity -> 10 + entity.getRandom().nextInt(20));
	}

	/**
	 * 设置游荡时的飞行速度修正（0.15 ≈ 每秒 3 格，1.0 为战斗全速）。
	 */
	public HoverWander<E> speedModifier(BiFunction<E, Vec3, Float> function) {
		this.speedModifier = function;

		return this;
	}

	/**
	 * 设置随机游荡点的选取半径（水平 / 垂直，单位：格）。
	 */
	public HoverWander<E> setRadius(int horizontal, int vertical) {
		this.horizontalRadius = horizontal;
		this.verticalRadius = vertical;

		return this;
	}

	@Override
	protected List<Pair<MemoryModuleType<?>, MemoryStatus>> getMemoryRequirements() {
		return MEMORY_REQUIREMENTS;
	}

	@Override
	protected void start(E entity) {
		Vec3 targetPos = pickRandomAirPos(entity);

		if (targetPos != null)
			BrainUtils.setMemory(entity, MemoryModuleType.WALK_TARGET,
					new WalkTarget(targetPos, this.speedModifier.apply(entity, targetPos), 1));
	}

	/**
	 * 在实体周围随机挑选一个空气方块点作为游荡目标，尝试若干次直到找到有效点。
	 */
	@Nullable
	protected Vec3 pickRandomAirPos(E entity) {
		BlockPos center = entity.blockPosition();

		for (int i = 0; i < 10; i++) {
			BlockPos candidate = center.offset(
					entity.getRandom().nextInt(this.horizontalRadius * 2 + 1) - this.horizontalRadius,
					entity.getRandom().nextInt(this.verticalRadius * 2 + 1) - this.verticalRadius,
					entity.getRandom().nextInt(this.horizontalRadius * 2 + 1) - this.horizontalRadius);

			// 距离过近的点没有移动意义
			if (candidate.distManhattan(center) < 2)
				continue;

			if (entity.level().getBlockState(candidate).isAir())
				return Vec3.atCenterOf(candidate);
		}

		return null;
	}
}
