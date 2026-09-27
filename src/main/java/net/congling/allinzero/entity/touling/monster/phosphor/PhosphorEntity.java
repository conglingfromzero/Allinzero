package net.congling.allinzero.entity.touling.monster.phosphor;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.congling.allinzero.entity.ai.flyingranged.DodgePlayerAim;
import net.congling.allinzero.entity.ai.flyingranged.HoverWander;
import net.congling.allinzero.entity.ai.hostile.TieredApproachTarget;
import net.congling.allinzero.sound.AllinzeroSounds;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.NeutralMob;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.control.FlyingMoveControl;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.animal.FlyingAnimal;
import net.minecraft.world.entity.monster.RangedAttackMob;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.pathfinder.PathType;
import net.minecraft.world.phys.Vec3;
import net.tslat.smartbrainlib.api.SmartBrainOwner;
import net.tslat.smartbrainlib.api.core.BrainActivityGroup;
import net.tslat.smartbrainlib.api.core.SmartBrainProvider;
import net.tslat.smartbrainlib.api.core.behaviour.FirstApplicableBehaviour;
import net.tslat.smartbrainlib.api.core.behaviour.custom.attack.AnimatableRangedAttack;
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
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.UUID;

/**
 * 磷光体：飞行远程敌对生物，基于 SmartBrainLib 行为树驱动。
 * 行为组成：
 * <ul>
 *     <li>锁定目标后分层接近（{@link TieredApproachTarget}）：≥64 格约 10 格/秒、32~64 格约 5 格/秒、
 *     20~32 格约 3 格/秒锁定逼近，≤20 格原地悬停；全程允许射击（射程 36 格）</li>
 *     <li>被玩家瞄准时经 10~15 tick 反应延迟横向规避（{@link DodgePlayerAim}）</li>
 *     <li>无目标时空中游荡（{@link HoverWander}，约每秒 3 格）</li>
 * </ul>
 */
public class PhosphorEntity extends PathfinderMob implements NeutralMob, FlyingAnimal, RangedAttackMob, SmartBrainOwner<PhosphorEntity> {
    private static final EntityDataAccessor<Integer> DATA_REMAINING_ANGER_TIME =
            SynchedEntityData.defineId(PhosphorEntity.class, EntityDataSerializers.INT);

    @Nullable
    private UUID persistentAngerTarget;

    public PhosphorEntity(EntityType<PhosphorEntity> type, Level level) {
        super(type, level);
        this.xpReward = 6;
        this.moveControl = new FlyingMoveControl(this, 20, true);
        this.setPathfindingMalus(PathType.WATER, -1.0F);
        this.setPathfindingMalus(PathType.LAVA, -1.0F);
        this.setPathfindingMalus(PathType.DANGER_FIRE, -1.0F);
    }

    @Override
    protected void registerGoals() {
        // 移动逻辑全部交由 SmartBrainLib 行为树（见 brainProvider/getCoreTasks 等），不再使用 vanilla Goal
    }

    @Override
    protected Brain.Provider<?> brainProvider() {
        return new SmartBrainProvider<>(this);
    }

    @Override
    public List<? extends ExtendedSensor<? extends PhosphorEntity>> getSensors() {
        return ObjectArrayList.of(
                new NearbyPlayersSensor<>(), // 感知周围玩家（半径默认 = FOLLOW_RANGE，与原目标搜索一致）
                new HurtBySensor<>());       // 记录伤害来源，被激怒时用于锁定攻击者
    }

    @Override
    public BrainActivityGroup<? extends PhosphorEntity> getCoreTasks() {
        return BrainActivityGroup.coreTasks(
                new FloatToSurfaceOfFluid<>(),  // 落水时上浮
                new LookAtTarget<>(),           // 看向 LOOK_TARGET
                new MoveToWalkTarget<>());      // 沿 WALK_TARGET 做飞行导航（游荡与战斗移动共用）
    }

    @Override
    public BrainActivityGroup<? extends PhosphorEntity> getIdleTasks() {
        return BrainActivityGroup.idleTasks(
                new FirstApplicableBehaviour<PhosphorEntity>(
                        new TargetOrRetaliate<PhosphorEntity>()
                                // 优先取附近可见可攻击玩家（发现玩家即锁定）；无则取伤害来源（被激怒反击）
                                .useMemory(MemoryModuleType.NEAREST_VISIBLE_ATTACKABLE_PLAYER),
                        new SetPlayerLookTarget<>(),
                        new SetRandomLookTarget<>()),
                new FirstApplicableBehaviour<PhosphorEntity>(
                        new HoverWander<>(), // 空中随机游荡（低速约 3 格/秒）
                        new Idle<>().runFor(entity -> 20 + entity.getRandom().nextInt(20))));
    }

    @Override
    public BrainActivityGroup<? extends PhosphorEntity> getFightTasks() {
        DodgePlayerAim<PhosphorEntity> dodge = new DodgePlayerAim<>();

        return BrainActivityGroup.fightTasks(
                new InvalidateAttackTarget<>(), // 目标死亡/失效/超出 FOLLOW_RANGE 后清空，回到游荡
                dodge,                          // 躲避玩家准星（带 10~15 tick 反应延迟）
                new TieredApproachTarget<>(dodge), // 分层接近：64/32/20 格档位，10/5/3 格/秒，≤20 悬停
                new AnimatableRangedAttack<PhosphorEntity>(0)      // 发射 PhosphorBolt，节奏沿用原有 40~80 tick 距离插值
                        .attackInterval(this::attackInterval)
                        .attackRadius(36.0F));
    }

    /**
     * 原有 RangedAttackGoal(this, 1.0, 40, 80, 12.0F) 的冷却逻辑：
     * 两次攻击间隔随攻击距离在 40~80 tick 间线性插值（越近越快）。
     * 现攻击射程 36 格（覆盖分层接近的射击区间），节奏公式保持不变。
     */
    private int attackInterval(PhosphorEntity entity) {
        LivingEntity target = entity.getTarget();

        if (target == null)
            return 40;

        float fraction = Mth.clamp(entity.distanceTo(target) / 20.0F, 0.0F, 1.0F);

        return Mth.floor(Mth.lerp(fraction, 40.0F, 80.0F));
    }

    public static net.minecraft.world.entity.ai.attributes.AttributeSupplier.Builder createAttributes() {
        return PathfinderMob.createMobAttributes()
                .add(net.minecraft.world.entity.ai.attributes.Attributes.MAX_HEALTH, 48.0)
                .add(net.minecraft.world.entity.ai.attributes.Attributes.ARMOR, 5.0)
                .add(net.minecraft.world.entity.ai.attributes.Attributes.ARMOR_TOUGHNESS, 3.0)
                .add(net.minecraft.world.entity.ai.attributes.Attributes.MOVEMENT_SPEED, 0.33)
                .add(net.minecraft.world.entity.ai.attributes.Attributes.FLYING_SPEED, 0.33)
                .add(net.minecraft.world.entity.ai.attributes.Attributes.FOLLOW_RANGE, 72.0) // 索敌 72 格（sensor 半径默认取 FOLLOW_RANGE）
                .add(net.minecraft.world.entity.ai.attributes.Attributes.ATTACK_DAMAGE, 6.0);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DATA_REMAINING_ANGER_TIME, 0);
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
            float speedMultiplier = (float) this.getAttributeValue(net.minecraft.world.entity.ai.attributes.Attributes.MOVEMENT_SPEED);
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
    protected SoundEvent getAmbientSound() {
        return SoundEvents.ALLAY_AMBIENT_WITHOUT_ITEM;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return SoundEvents.ALLAY_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.ALLAY_DEATH;
    }

    @Override
    protected void customServerAiStep() {
        this.updatePersistentAnger((ServerLevel) this.level(), true);

        // 无目标时的悬停防下沉（保留原有逻辑）
        if (this.getTarget() == null && this.getDeltaMovement().y < 0.0 && this.random.nextInt(3) == 0) {
            this.setDeltaMovement(this.getDeltaMovement().x, this.getDeltaMovement().y + 0.04, this.getDeltaMovement().z);
        }

        // SmartBrainLib：驱动行为树
        tickBrain(this);

        super.customServerAiStep();
    }

    @Override
    public void performRangedAttack(net.minecraft.world.entity.LivingEntity target, float velocity) {
        Vec3 targetPos = target.position().add(0.0, target.getBbHeight() * 0.5, 0.0);
        PhosphorBoltEntity bolt = new PhosphorBoltEntity(this.level(), this, targetPos);
        this.playSound(AllinzeroSounds.PHOSPHOR_SHOOT.get(), 2.0F,
                1.0F / (this.getRandom().nextFloat() * 0.2F + 0.9F));
        this.level().addFreshEntity(bolt);
    }


    @Override
    public int getRemainingPersistentAngerTime() {
        return this.entityData.get(DATA_REMAINING_ANGER_TIME);
    }

    @Override
    public void setRemainingPersistentAngerTime(int time) {
        this.entityData.set(DATA_REMAINING_ANGER_TIME, time);
    }

    @Override
    @Nullable
    public UUID getPersistentAngerTarget() {
        return this.persistentAngerTarget;
    }

    @Override
    public void setPersistentAngerTarget(@Nullable UUID target) {
        this.persistentAngerTarget = target;
    }

    @Override
    public void startPersistentAngerTimer() {
        this.setRemainingPersistentAngerTime(20 * (20 + this.random.nextInt(20)));
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        this.addPersistentAngerSaveData(tag);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        this.readPersistentAngerSaveData(this.level(), tag);
    }

}
