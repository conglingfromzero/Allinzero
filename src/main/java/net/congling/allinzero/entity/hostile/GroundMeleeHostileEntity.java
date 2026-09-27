package net.congling.allinzero.entity.hostile;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;
import net.congling.allinzero.entity.ai.hostile.ChargeApproach;
import net.tslat.smartbrainlib.api.SmartBrainOwner;
import net.tslat.smartbrainlib.api.core.BrainActivityGroup;
import net.tslat.smartbrainlib.api.core.SmartBrainProvider;
import net.tslat.smartbrainlib.api.core.behaviour.FirstApplicableBehaviour;
import net.tslat.smartbrainlib.api.core.behaviour.custom.attack.AnimatableMeleeAttack;
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
 * 地面敌对近战生物抽象基类（可复用原型，参照 {@code PhosphorEntity} 的 SmartBrainLib 接线方式）。
 * <p>
 * 索敌范围 64 格（{@code FOLLOW_RANGE} 属性 + {@link NearbyPlayersSensor} 默认半径即 FOLLOW_RANGE）。
 * 锁定目标后的战斗节奏由 {@link ChargeApproach} 驱动：
 * <ul>
 *     <li>距离 &gt; 36 格：约 5 格/秒冲向目标，冲撞接触造成 8 点固定伤害（mobAttack 伤害源，
 *     命中后 1 秒内不重复结算）</li>
 *     <li>距离 ≤ 36 格：约 1 格/秒正常接近</li>
 *     <li>贴近后由 {@link AnimatableMeleeAttack} 完成近战攻击，伤害走 ATTACK_DAMAGE 属性（8 点）</li>
 * </ul>
 * <p>
 * 地面速度校准公式（自 vanilla 1.21.1 travel/摩擦链推导，详见 {@link ChargeApproach} 注释）：
 * {@code 格/秒 ≈ 43.2 × speedModifier² × MOVEMENT_SPEED²}（普通地面摩擦 0.6）。
 * 本实体 MOVEMENT_SPEED = 0.25：speedModifier = 1 ≈ 2.70 格/秒；5 格/秒 → 1.36；1 格/秒 → 0.61。
 * <p>
 * 基类为 abstract，不注册实体类型，子类注册后可直接复用整套行为树。
 */
public abstract class GroundMeleeHostileEntity extends Monster implements SmartBrainOwner<GroundMeleeHostileEntity> {

	public GroundMeleeHostileEntity(EntityType<? extends GroundMeleeHostileEntity> type, Level level) {
		super(type, level);
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
	public List<? extends ExtendedSensor<? extends GroundMeleeHostileEntity>> getSensors() {
		return ObjectArrayList.of(
				new NearbyPlayersSensor<>(), // 感知周围玩家（半径默认 = FOLLOW_RANGE = 64 格索敌）
				new HurtBySensor<>());       // 记录伤害来源，被攻击时反击
	}

	@Override
	public BrainActivityGroup<? extends GroundMeleeHostileEntity> getCoreTasks() {
		return BrainActivityGroup.coreTasks(
				new FloatToSurfaceOfFluid<>(),  // 落水时上浮
				new LookAtTarget<>(),           // 看向 LOOK_TARGET
				new MoveToWalkTarget<>());      // 沿 WALK_TARGET 做地面导航（冲撞/接近共用）
	}

	@Override
	public BrainActivityGroup<? extends GroundMeleeHostileEntity> getIdleTasks() {
		return BrainActivityGroup.idleTasks(
				new FirstApplicableBehaviour<GroundMeleeHostileEntity>(
						new TargetOrRetaliate<GroundMeleeHostileEntity>()
								// 优先取附近可见可攻击玩家（64 格索敌）；无则取伤害来源（被激怒反击）
								.useMemory(MemoryModuleType.NEAREST_VISIBLE_ATTACKABLE_PLAYER),
						new SetPlayerLookTarget<>(),
						new SetRandomLookTarget<>()),
				new Idle<>().runFor(entity -> 20 + entity.getRandom().nextInt(20)));
	}

	@Override
	public BrainActivityGroup<? extends GroundMeleeHostileEntity> getFightTasks() {
		return BrainActivityGroup.fightTasks(
				new InvalidateAttackTarget<>(),      // 目标死亡/失效/超出 FOLLOW_RANGE 后清空，回到游荡
				new ChargeApproach<GroundMeleeHostileEntity>(),          // 分段接近：>36 格冲撞（5 格/秒 + 8 点接触伤害）
				new AnimatableMeleeAttack<GroundMeleeHostileEntity>(0)   // 贴近近战，伤害走 ATTACK_DAMAGE（8 点）
						.attackInterval(entity -> 20));
	}

	/**
	 * 基类属性（abstract 不注册实体，仅供子类静态注册时调用或链式追加）：
	 * 地面速度 MOVEMENT_SPEED = 0.25（校准基准：speedModifier = 1 ≈ 2.70 格/秒，见类注释公式）、
	 * 索敌 FOLLOW_RANGE = 64 格、攻击属性 ATTACK_DAMAGE = 8（AnimatableMeleeAttack 使用）。
	 */
	public static AttributeSupplier.Builder createAttributes() {
		return PathfinderMob.createMobAttributes()
				.add(Attributes.MAX_HEALTH, 20.0) // 基线生命值，子类可追加覆盖
				.add(Attributes.MOVEMENT_SPEED, 0.25)
				.add(Attributes.FOLLOW_RANGE, 64.0)
				.add(Attributes.ATTACK_DAMAGE, 8.0);
	}

	@Override
	protected void customServerAiStep() {
		// SmartBrainLib：驱动行为树
		tickBrain(this);

		super.customServerAiStep();
	}
}
