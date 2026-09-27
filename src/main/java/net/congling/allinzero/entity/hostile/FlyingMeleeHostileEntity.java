package net.congling.allinzero.entity.hostile;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.core.BlockPos;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.FlyingMoveControl;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.animal.FlyingAnimal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.pathfinder.PathType;
import net.minecraft.world.phys.Vec3;
import net.congling.allinzero.entity.ai.flyingranged.HoverWander;
import net.congling.allinzero.entity.ai.hostile.OrbitThenChargeTarget;
import net.tslat.smartbrainlib.api.SmartBrainOwner;
import net.tslat.smartbrainlib.api.core.BrainActivityGroup;
import net.tslat.smartbrainlib.api.core.SmartBrainProvider;
import net.tslat.smartbrainlib.api.core.behaviour.FirstApplicableBehaviour;
import net.tslat.smartbrainlib.api.core.behaviour.custom.look.LookAtTarget;
import net.tslat.smartbrainlib.api.core.behaviour.custom.misc.Idle;
import net.tslat.smartbrainlib.api.core.behaviour.custom.move.FloatToSurfaceOfFluid;
import net.tslat.smartbrainlib.api.core.behaviour.custom.move.MoveToWalkTarget;
import net.tslat.smartbrainlib.api.core.behaviour.custom.target.InvalidateAttackTarget;
import net.tslat.smartbrainlib.api.core.behaviour.custom.target.SetPlayerLookTarget;
import net.tslat.smartbrainlib.api.core.behaviour.custom.target.SetRandomLookTarget;
import net.tslat.smartbrainlib.api.core.behaviour.custom.target.TargetOrRetaliate;
import net.tslat.smartbrainlib.api.core.sensor.ExtendedSensor;
import net.tslat.smartbrainlib.api.core.sensor.vanilla.HurtBySensor;
import net.tslat.smartbrainlib.api.core.sensor.vanilla.NearbyPlayersSensor;

import java.util.List;

/**
 * 飞行敌对近战生物抽象基类（可复用原型，参照 {@code PhosphorEntity} 的 SmartBrainLib 接线方式）。
 * <p>
 * 构成：{@code FlyingMoveControl} + {@code FlyingPathNavigation} + travel 阻尼 0.91（飞行速度校准：
 * 终端速度 ≈ {@code 21.6 × speedModifier} 格/秒，0.7 ≈ 15 格/秒）。
 * <p>
 * 索敌范围 48 格（{@code FOLLOW_RANGE} 属性 + {@link NearbyPlayersSensor} 默认半径即 FOLLOW_RANGE）；
 * 可攻击范围 24 格（超出不发起突进）。战斗循环由 {@link OrbitThenChargeTarget} 驱动：
 * 与目标保持 10~15 格距离环绕，首轮环绕 10 秒（200 tick）、之后每轮 15 秒（300 tick）后突进，
 * 突进约 15 格/秒直线扑向目标，命中造成 10 点固定伤害（mobAttack 伤害源，非 ATTACK_DAMAGE 属性结算），
 * 未命中（超时约 2 秒/冲过头）立刻拉回并循环。
 * <p>
 * 基类为 abstract，不注册实体类型，子类注册后可直接复用整套行为树；子类如需扩展战斗行为可覆写
 * {@link #getFightTasks()} 并在末尾追加行为。
 */
public abstract class FlyingMeleeHostileEntity extends Monster implements FlyingAnimal, SmartBrainOwner<FlyingMeleeHostileEntity> {

	public FlyingMeleeHostileEntity(EntityType<? extends FlyingMeleeHostileEntity> type, Level level) {
		super(type, level);
		this.moveControl = new FlyingMoveControl(this, 20, true);
		this.setPathfindingMalus(PathType.WATER, -1.0F);
		this.setPathfindingMalus(PathType.LAVA, -1.0F);
		this.setPathfindingMalus(PathType.DANGER_FIRE, -1.0F);
	}

	@Override
	protected void registerGoals() {
		// 移动与战斗逻辑全部交由 SmartBrainLib 行为树，不使用 vanilla Goal
	}

	@Override
	protected Brain.Provider<?> brainProvider() {
		return new SmartBrainProvider<>(this);
	}

	@Override
	public List<? extends ExtendedSensor<? extends FlyingMeleeHostileEntity>> getSensors() {
		return ObjectArrayList.of(
				new NearbyPlayersSensor<>(), // 感知周围玩家（半径默认 = FOLLOW_RANGE = 48 格索敌）
				new HurtBySensor<>());       // 记录伤害来源，被攻击时反击
	}

	@Override
	public BrainActivityGroup<? extends FlyingMeleeHostileEntity> getCoreTasks() {
		return BrainActivityGroup.coreTasks(
				new FloatToSurfaceOfFluid<>(),  // 落水时上浮
				new LookAtTarget<>(),           // 看向 LOOK_TARGET
				new MoveToWalkTarget<>());      // 沿 WALK_TARGET 做飞行导航（环绕/追近共用）
	}

	@Override
	public BrainActivityGroup<? extends FlyingMeleeHostileEntity> getIdleTasks() {
		return BrainActivityGroup.idleTasks(
				new FirstApplicableBehaviour<FlyingMeleeHostileEntity>(
						new TargetOrRetaliate<FlyingMeleeHostileEntity>()
								// 优先取附近可见可攻击玩家（48 格索敌）；无则取伤害来源（被激怒反击）
								.useMemory(MemoryModuleType.NEAREST_VISIBLE_ATTACKABLE_PLAYER),
						new SetPlayerLookTarget<>(),
						new SetRandomLookTarget<>()),
				new FirstApplicableBehaviour<FlyingMeleeHostileEntity>(
						new HoverWander<>(), // 无目标时空中低速游荡（约 3 格/秒）
						new Idle<>().runFor(entity -> 20 + entity.getRandom().nextInt(20))));
	}

	@Override
	public BrainActivityGroup<? extends FlyingMeleeHostileEntity> getFightTasks() {
		return BrainActivityGroup.fightTasks(
				new InvalidateAttackTarget<>(),          // 目标死亡/失效/超出 FOLLOW_RANGE 后清空，回到游荡
				new OrbitThenChargeTarget<FlyingMeleeHostileEntity>()); // 环绕 10~15 格 + 定时突进（10 点固定伤害）
	}

	/**
	 * 基类属性（abstract 不注册实体，仅供子类静态注册时调用或链式追加）：
	 * 飞行速度 MOVEMENT_SPEED = FLYING_SPEED = 0.33（校准基准，见 {@link OrbitThenChargeTarget} 注释）、
	 * 索敌 FOLLOW_RANGE = 48 格、攻击属性 ATTACK_DAMAGE = 10（与突进 10 点固定伤害一致的基线）。
	 */
	public static AttributeSupplier.Builder createAttributes() {
		return PathfinderMob.createMobAttributes()
				.add(Attributes.MAX_HEALTH, 20.0) // 基线生命值，子类可追加覆盖
				.add(Attributes.MOVEMENT_SPEED, 0.33)
				.add(Attributes.FLYING_SPEED, 0.33)
				.add(Attributes.FOLLOW_RANGE, 48.0)
				.add(Attributes.ATTACK_DAMAGE, 10.0);
	}

	@Override
	protected PathNavigation createNavigation(Level level) {
		FlyingPathNavigation navigation = new FlyingPathNavigation(this, level);
		navigation.setCanOpenDoors(false);
		navigation.setCanFloat(true);
		navigation.setCanPassDoors(true);
		return navigation;
	}

	@Override
	public void travel(Vec3 travelVector) {
		if (this.isControlledByLocalInstance() || this.isEffectiveAi()) {
			float speedMultiplier = (float) this.getAttributeValue(Attributes.MOVEMENT_SPEED);
			this.moveRelative(speedMultiplier, travelVector);
			this.move(MoverType.SELF, this.getDeltaMovement());
			this.setDeltaMovement(this.getDeltaMovement().scale(0.91));
		} else {
			super.travel(travelVector);
		}
	}

	@Override
	public boolean causeFallDamage(float distance, float multiplier, DamageSource source) {
		return false;
	}

	@Override
	protected void checkFallDamage(double y, boolean onGround, net.minecraft.world.level.block.state.BlockState state, BlockPos pos) {
	}

	@Override
	public boolean isFlying() {
		return !this.onGround();
	}

	@Override
	protected void customServerAiStep() {
		// 无目标时的悬停防下沉（与 PhosphorEntity 一致）
		if (this.getTarget() == null && this.getDeltaMovement().y < 0.0 && this.random.nextInt(3) == 0) {
			this.setDeltaMovement(this.getDeltaMovement().x, this.getDeltaMovement().y + 0.04, this.getDeltaMovement().z);
		}

		// SmartBrainLib：驱动行为树
		tickBrain(this);

		super.customServerAiStep();
	}
}
