package net.congling.allinzero.entity.touling.monster.wandering_wsip;

import net.congling.allinzero.entity.AllinzeroEntity;
import net.congling.allinzero.entity.hostile.GroundMeleeHostileEntity;
import net.congling.allinzero.entity.touling.boss.touling_congling.WraithEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animatable.instance.InstancedAnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.animation.RawAnimation;

/**
 * 游荡怨灵（wandering_wsip）：地面敌对近战生物，复用 {@link GroundMeleeHostileEntity} 的
 * SmartBrainLib 行为树（>36 格 5 格/秒冲撞 8 点伤害、≤36 格 1 格/秒接近、贴近近战）。
 * <p>
 * 保留特性：
 * <ul>
 *     <li>周期性双臂张开召唤（summon），动画播完后生成 5 只怨灵；施法期间定身沉默</li>
 *     <li>近战命中附加 2 级灵魂侵蚀（全套幽冥/灵魂盔甲可降低等级）</li>
 *     <li>死亡掉落 100 点经验</li>
 * </ul>
 */
public class WanderingWsipEntity extends GroundMeleeHostileEntity implements GeoEntity {
    public static final byte ANIM_NONE = 0;
    public static final byte ANIM_ATTACK = 1;
    public static final byte ANIM_SUMMON = 2;

    private static final EntityDataAccessor<Byte> DATA_ANIM_ID =
            SynchedEntityData.defineId(WanderingWsipEntity.class, EntityDataSerializers.BYTE);
    private static final EntityDataAccessor<Integer> DATA_ANIM_TICKS =
            SynchedEntityData.defineId(WanderingWsipEntity.class, EntityDataSerializers.INT);

    private static final RawAnimation WALK_TWISTED = RawAnimation.begin().thenLoop("animation.walk_twisted");
    private static final RawAnimation ATTACK = RawAnimation.begin().thenPlay("animation.attack");
    private static final RawAnimation SUMMON = RawAnimation.begin().thenPlay("animation.summon");

    /** attack 动画 1.4s */
    private static final int ATTACK_TICKS = 28;
    /** summon 动画 2.8s，播完时生成怨灵 */
    private static final int SUMMON_TICKS = 56;

    private static final int SUMMON_WRAITH_COUNT = 5;
    private static final int SUMMON_COOLDOWN = 20 * 20;
    private static final double SUMMON_RANGE_SQR = 12.0 * 12.0;

    private final AnimatableInstanceCache cache = new InstancedAnimatableInstanceCache(this);

    private int summonCooldown = 8 * 20;

    public WanderingWsipEntity(EntityType<? extends WanderingWsipEntity> type, Level level) {
        super(type, level);
        this.xpReward = 100;
    }

    @Override
    protected void registerGoals() {
        // 移动与战斗全部交由 SmartBrainLib 行为树（见 GroundMeleeHostileEntity）
    }

    /**
     * 属性：复用地面近战基类的速度/索敌校准（MOVEMENT_SPEED 0.25、FOLLOW_RANGE 64），
     * 仅覆写生命与攻击。
     */
    public static AttributeSupplier.Builder createAttributes() {
        return GroundMeleeHostileEntity.createAttributes()
                .add(Attributes.MAX_HEALTH, 100.0)
                .add(Attributes.ATTACK_DAMAGE, 10.0);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DATA_ANIM_ID, ANIM_NONE);
        builder.define(DATA_ANIM_TICKS, 0);
    }

    public byte getAnimId() {
        return this.entityData.get(DATA_ANIM_ID);
    }

    public int getAnimTicks() {
        return this.entityData.get(DATA_ANIM_TICKS);
    }

    /** 召唤施法期间定身沉默：禁止移动与近战攻击 */
    @Override
    public boolean isImmobile() {
        return this.getAnimId() == ANIM_SUMMON || super.isImmobile();
    }

    /** 召唤期间不允许近战攻击命中（AnimatableMeleeAttack 通过此方法判定射程） */
    @Override
    public boolean isWithinMeleeAttackRange(LivingEntity entity) {
        return this.getAnimId() != ANIM_SUMMON && super.isWithinMeleeAttackRange(entity);
    }

    /** 近战命中前的挥击会触发 attack 动画 */
    @Override
    public void swing(InteractionHand hand) {
        super.swing(hand);
        if (!this.level().isClientSide && this.getAnimId() == ANIM_NONE) {
            this.beginAttack();
        }
    }

    private void beginAttack() {
        this.entityData.set(DATA_ANIM_ID, ANIM_ATTACK);
        this.entityData.set(DATA_ANIM_TICKS, ATTACK_TICKS);
    }

    private void beginSummon() {
        this.entityData.set(DATA_ANIM_ID, ANIM_SUMMON);
        this.entityData.set(DATA_ANIM_TICKS, SUMMON_TICKS);
        this.summonCooldown = SUMMON_COOLDOWN;
        this.playCasterSound(SoundEvents.EVOKER_PREPARE_ATTACK, 1.0F, 0.7F);
    }

    @Override
    public void aiStep() {
        super.aiStep();
        int ticksLeft = this.entityData.get(DATA_ANIM_TICKS);
        if (ticksLeft > 0) {
            int next = ticksLeft - 1;
            this.entityData.set(DATA_ANIM_TICKS, next);
            if (next == 0) {
                byte completed = this.entityData.get(DATA_ANIM_ID);
                this.entityData.set(DATA_ANIM_ID, ANIM_NONE);
                if (!this.level().isClientSide && completed == ANIM_SUMMON) {
                    this.spawnWraiths();
                }
            }
        }
    }

    @Override
    protected void customServerAiStep() {
        super.customServerAiStep(); // SmartBrainLib：驱动行为树
        if (this.summonCooldown > 0) {
            this.summonCooldown--;
        }
        // 冷却结束、非施法状态且目标在 12 格内时，开始召唤施法
        if (this.getAnimId() == ANIM_NONE && this.summonCooldown <= 0) {
            LivingEntity target = this.getTarget();
            if (target != null && target.isAlive()
                    && this.distanceToSqr(target) <= SUMMON_RANGE_SQR) {
                this.beginSummon();
            }
        }
    }

    /** summon 动画播完后：在身边环绕生成五只怨灵 */
    private void spawnWraiths() {
        ServerLevel level = (ServerLevel) this.level();
        LivingEntity target = this.getTarget();
        for (int i = 0; i < SUMMON_WRAITH_COUNT; i++) {
            WraithEntity wraith = new WraithEntity(AllinzeroEntity.WRAITH.get(), level);
            float angle = (float) Math.toRadians((360.0F / SUMMON_WRAITH_COUNT) * i
                    + this.random.nextFloat() * 30.0F);
            float radius = 2.0F + this.random.nextFloat() * 1.5F;
            double x = this.getX() + Math.cos(angle) * radius;
            double y = this.getY() + 0.5 + this.random.nextInt(2);
            double z = this.getZ() + Math.sin(angle) * radius;
            wraith.moveTo(x, y, z, this.random.nextFloat() * 360.0F, 0.0F);
            wraith.setOwner(this);
            wraith.setBoundOrigin(BlockPos.containing(x, y, z));
            if (target != null && target.isAlive()) {
                wraith.setTarget(target);
            }
            level.addFreshEntity(wraith);
            level.sendParticles(ParticleTypes.SCULK_SOUL, x, y, z, 20, 0.3, 0.4, 0.3, 0.01);
        }
        this.playCasterSound(SoundEvents.EVOKER_CAST_SPELL, 1.2F, 0.8F);
    }

    private void playCasterSound(net.minecraft.sounds.SoundEvent sound, float volume, float pitch) {
        if (this.level() instanceof ServerLevel level) {
            level.playSound(null, this.getX(), this.getY(), this.getZ(),
                    sound, SoundSource.HOSTILE, volume, pitch);
        }
    }

    /** 近战命中附加 2 级灵魂侵蚀（全套幽冥/灵魂盔甲可降低等级） */
    @Override
    public boolean doHurtTarget(Entity target) {
        boolean hurt = super.doHurtTarget(target);
        if (hurt && target instanceof LivingEntity living && !living.level().isClientSide) {
            int reduction = living instanceof Player player
                    ? net.congling.allinzero.event.ToulingEnvironmentEvents.getErosionReduction(player) : 0;
            int level = 2 - reduction;
            if (level > 0) {
                living.addEffect(new MobEffectInstance(
                        net.congling.allinzero.effect.AllinzeroEffects.SOUL_EROSION,
                        200, level - 1, false, true), this);
            }
        }
        return hurt;
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putInt("SummonCooldown", this.summonCooldown);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        this.summonCooldown = tag.getInt("SummonCooldown");
    }

    // ===== GeckoLib =====

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "main", 5, this::mainController));
    }

    private PlayState mainController(AnimationState<WanderingWsipEntity> state) {
        byte animId = this.getAnimId();
        if (animId == ANIM_ATTACK) {
            return state.setAndContinue(ATTACK);
        }
        if (animId == ANIM_SUMMON) {
            return state.setAndContinue(SUMMON);
        }
        if (state.isMoving()) {
            return state.setAndContinue(WALK_TWISTED);
        }
        state.resetCurrentAnimation();
        return PlayState.STOP;
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }
}
