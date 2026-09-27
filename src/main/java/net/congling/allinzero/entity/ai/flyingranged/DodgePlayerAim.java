package net.congling.allinzero.entity.ai.flyingranged;

import com.mojang.datafixers.util.Pair;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.tslat.smartbrainlib.api.core.behaviour.ExtendedBehaviour;
import net.tslat.smartbrainlib.object.MemoryTest;
import net.tslat.smartbrainlib.util.BrainUtils;

import java.util.List;

/**
 * 规避玩家瞄准行为：攻击目标为玩家、且本体处于其视线锥内（前方且垂直距离小于约 3 格）时，
 * 以垂直于玩家视线的水平方向横向规避。
 * <p>
 * 反应延迟机制：首次检测到被瞄准后，先倒计时 {@code 10~15} tick 才真正开始规避；
 * 延迟期间威胁解除则计数清零。延迟计数与规避方向均使用持久化字段（非 brain memory）。
 * 规避方向（左侧/右侧）在连续规避间保持不变，仅在所选方向被方块阻挡时才换向，
 * 使玩家可以预判提前量。两次规避之间另有 30~50 tick 冷却。
 * <p>
 * 规避期间本行为直接驱动 {@code moveControl}（不经过 WALK_TARGET），
 * {@link KeepDistanceFromTarget} 会在此期间让出移动权。
 *
 * @param <E> 实体类型
 */
public class DodgePlayerAim<E extends PathfinderMob> extends ExtendedBehaviour<E> {
	private static final MemoryTest MEMORY_REQUIREMENTS = MemoryTest.builder(1).hasMemory(MemoryModuleType.ATTACK_TARGET);

	/** 玩家视线锥的垂直半径（格）：本体到玩家视线射线的垂直距离小于该值即视为被瞄准 */
	private static final double AIM_CONE_RADIUS_SQR = 3.0 * 3.0;
	/** 反应延迟（tick）：检测到威胁后延迟响应，使玩家可预判 */
	private static final int MIN_REACTION_DELAY = 10;
	private static final int MAX_REACTION_DELAY = 15;
	/** 单次规避的横向位移（格） */
	private static final float DODGE_DISTANCE = 4.0f;
	/** 单次规避机动的持续时间（tick） */
	private static final int MIN_DODGE_TICKS = 12;
	private static final int MAX_DODGE_TICKS = 18;

	protected float speedModifier = 1.0f;

	/** 威胁反应延迟计数，-1 表示当前未处于威胁计数状态 */
	private int reactionDelay = -1;
	/** 是否正在执行规避机动 */
	private boolean dodging = false;
	/** 规避机动剩余 tick 数 */
	private int dodgeTicks = 0;
	/** 规避侧向（+1/-1），持久化字段，连续规避间不换向 */
	private int dodgeSide = 1;
	/** 本次规避的横向方向（世界坐标，规避开始时确定，期间不变） */
	private Vec3 dodgeDirection = Vec3.ZERO;

	public DodgePlayerAim() {
		// 两次规避之间的最小间隔
		cooldownFor(entity -> 30 + entity.getRandom().nextInt(20));
	}

	/**
	 * 设置规避机动时的速度修正（默认 1.0，与战斗全速一致）。
	 */
	public DodgePlayerAim<E> speedModifier(float modifier) {
		this.speedModifier = modifier;

		return this;
	}

	/**
	 * 供 {@link KeepDistanceFromTarget} 查询：规避机动期间本行为独占移动权。
	 */
	public boolean isDodging() {
		return this.dodging;
	}

	@Override
	protected List<Pair<MemoryModuleType<?>, MemoryStatus>> getMemoryRequirements() {
		return MEMORY_REQUIREMENTS;
	}

	/**
	 * 每 tick（本行为未运行时）调用：检测威胁并执行延迟倒计时，
	 * 倒计时结束时才真正开始规避（返回 true 启动本行为）。
	 */
	@Override
	protected boolean checkExtraStartConditions(ServerLevel level, E entity) {
		if (this.dodging)
			return true;

		LivingEntity target = BrainUtils.getTargetOfEntity(entity);

		if (target instanceof Player player && isAimedByPlayer(entity, player)) {
			// 首次检测到威胁：掷出 10~15 tick 的反应延迟
			if (this.reactionDelay < 0)
				this.reactionDelay = MIN_REACTION_DELAY + entity.getRandom().nextInt(MAX_REACTION_DELAY - MIN_REACTION_DELAY + 1);

			if (--this.reactionDelay <= 0) {
				this.reactionDelay = -1;

				return prepareDodge(entity, player);
			}

			// 延迟倒计时中，暂不启动规避
			return false;
		}

		// 威胁解除，重置计数（避免陈旧计数误触发）
		this.reactionDelay = -1;

		return false;
	}

	@Override
	protected boolean shouldKeepRunning(E entity) {
		return this.dodging && this.dodgeTicks > 0 && BrainUtils.hasMemory(entity, MemoryModuleType.ATTACK_TARGET);
	}

	@Override
	protected void tick(E entity) {
		this.dodgeTicks--;

		// 沿既定横向方向持续横移（方向在规避开始时确定，期间不变，不频繁换向）
		Vec3 wanted = entity.position().add(this.dodgeDirection.scale(DODGE_DISTANCE));

		entity.getMoveControl().setWantedPosition(wanted.x, wanted.y, wanted.z, this.speedModifier);
	}

	@Override
	protected void stop(E entity) {
		this.dodging = false;
		this.dodgeTicks = 0;
		this.reactionDelay = -1;
	}

	/**
	 * 判定本体是否处于玩家的瞄准视线锥内：位于玩家视线前方、且到视线射线的垂直距离小于锥半径。
	 */
	private boolean isAimedByPlayer(E entity, Player player) {
		Vec3 look = player.getLookAngle();
		Vec3 toEntity = entity.getEyePosition().subtract(player.getEyePosition());
		double along = toEntity.dot(look);

		// 位于玩家身后，不可能被瞄准
		if (along <= 0)
			return false;

		Vec3 perpOffset = toEntity.subtract(look.scale(along));

		return perpOffset.lengthSqr() < AIM_CONE_RADIUS_SQR;
	}

	/**
	 * 准备一次规避：取玩家视线的水平法向作为规避方向（先当前侧向，被阻挡才换向），
	 * 校验目标点为空气后启动规避机动。
	 */
	private boolean prepareDodge(E entity, Player player) {
		Vec3 look = player.getLookAngle();
		Vec3 horizontalPerp = new Vec3(-look.z, 0, look.x);

		if (horizontalPerp.lengthSqr() < 1.0E-4)
			horizontalPerp = new Vec3(1, 0, 0);
		else
			horizontalPerp = horizontalPerp.normalize();

		for (int i = 0; i < 2; i++) {
			Vec3 dir = horizontalPerp.scale(this.dodgeSide);
			Vec3 candidate = entity.position().add(dir.scale(DODGE_DISTANCE));

			if (entity.level().getBlockState(BlockPos.containing(candidate)).isAir()) {
				this.dodgeDirection = dir;
				this.dodging = true;
				this.dodgeTicks = MIN_DODGE_TICKS + entity.getRandom().nextInt(MAX_DODGE_TICKS - MIN_DODGE_TICKS + 1);

				return true;
			}

			// 当前侧向被阻挡，仅在此时才换向
			this.dodgeSide = -this.dodgeSide;
		}

		return false;
	}
}
