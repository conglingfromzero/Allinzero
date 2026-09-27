package net.congling.allinzero.entity.touling.boss.touling_congling;

import net.congling.allinzero.effect.AllinzeroEffects;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.BossEvent;
import net.minecraft.world.Difficulty;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.FlyingMoveControl;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.animal.FlyingAnimal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.ThrownPotion;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.NeoForgeMod;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animatable.instance.InstancedAnimatableInstanceCache;
import software.bernie.geckolib.animation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * 透灵从零：透灵维度终极 BOSS，基于 GeckoLib 驱动，使用
 * animations/touling/boss 下的动画与 geo/touling/boss 下的模型。
 * <p>
 * 基础属性：生命 5000，攻击 10/30/50（简单/普通/困难），护甲 20，盔甲韧性 15。
 * <p>
 * 行动方式：漂浮在距目标 3.5~7.5 格的正侧方位（x 或 z 轴）、y 轴 3 格以内；
 * 距离过远时执行 fly 动画飞近；目标处于面朝方向左/右侧时执行 fly_to_left/fly_to_right
 * 并转身面向目标。
 * <p>
 * 被动：灵魂代偿（火焰/岩浆 ×2，火焰附加/火矢 ×3）；灵魂吸取（血量 <30% 触发一次，
 * 无敌并召唤 4 怨灵，20s 内全灭 → 受伤 ×4；否则每只存活怨灵回复 75 生命）。
 * <p>
 * 技能：灵魂镇压（出场，30 半径强制拉入）；灵魂召唤（y 轴高度差，1.25s 召唤 5 怨灵，
 * 1.5s 发起攻击）；灵魂之力（1.25s 获得力量 V/抗性 III/速度 II，15s，45s 冷却）；
 * 灵魂轰击（5 颗光弹身后上浮至 y+5 静止，1.25s 后 0.5s 内依次追踪打出，15 伤害 + 力量加成）。
 */
public class ToulingConglingEntity extends Monster implements FlyingAnimal, GeoEntity {

    // ===== 动画 ID（与动画文件中的名称一一对应） =====
    public static final byte ANIM_NONE = 0;
    /** 技能一：灵魂镇压（2.67s，54t），出场时使用 */
    public static final byte ANIM_SUPPRESSION = 1;
    /** 技能二：灵魂召唤（2s，40t） */
    public static final byte ANIM_CALL = 2;
    /** 技能三：灵魂之力（2s，40t） */
    public static final byte ANIM_POWER = 3;
    /** 技能四：灵魂轰击（2s，40t） */
    public static final byte ANIM_BLAST = 4;
    /** 被动：灵魂吸取（21s，420t；机制 20s = 400t） */
    public static final byte ANIM_ABSORPTION = 5;
    /** 远距离飞近（循环） */
    public static final byte ANIM_FLY = 6;
    /** 目标在左侧：左转身（1.25s） */
    public static final byte ANIM_FLY_LEFT = 7;
    /** 目标在右侧：右转身（1.25s） */
    public static final byte ANIM_FLY_RIGHT = 8;

    private static final EntityDataAccessor<Byte> DATA_ANIM_ID =
            SynchedEntityData.defineId(ToulingConglingEntity.class, EntityDataSerializers.BYTE);
    private static final EntityDataAccessor<Integer> DATA_ANIM_TICKS =
            SynchedEntityData.defineId(ToulingConglingEntity.class, EntityDataSerializers.INT);

    private enum Phase { NORMAL, DRAIN, WEAKENED }

    // ===== 定位参数 =====
    private static final float MIN_HOVER = 3.5F;
    private static final float MAX_HOVER = 7.5F;
    private static final float Y_BAND = 3.0F;
    /** 转向动画触发阈值（度） */
    private static final float TURN_THRESHOLD = 30.0F;
    /** 每 tick 最大转身角度（度） */
    private static final float TURN_SPEED = 6.0F;

    // ===== 灵魂吸取 =====
    private static final float DRAIN_HP_THRESHOLD = 0.30F;
    private static final int DRAIN_RESOLVE_TICKS = 20 * 20;
    private static final float DRAIN_HEAL_PER_WRAITH = 75.0F;
    private static final int DRAIN_WRAITH_COUNT = 4;

    // ===== 灵魂召唤 =====
    private static final int SUMMON_WRAITH_COUNT = 5;
    private static final int SUMMON_Y_DIFF = 1;
    private static final double SUMMON_RANGE = 64.0;

    // ===== 灵魂之力 =====
    private static final int POWER_DURATION = 15 * 20;
    private static final int POWER_COOLDOWN = 45 * 20;

    // ===== 灵魂轰击 =====
    private static final int BOMB_COUNT = 5;
    private static final int BOMB_COOLDOWN = 13 * 20;
    private static final double BOMB_RANGE = 48.0;
    private static final float BOMB_BASE_DAMAGE = 15.0F;

    // ===== 灵魂镇压 =====
    private static final int SUPPRESS_RADIUS = 30;
    private static final int SUPPRESS_HEIGHT = 10;

    // ===== 强化版 =====
    private static final float ENHANCED_MAX_HEALTH = 8000.0F;
    private static final double ENHANCED_ARMOR = 50.0;
    private static final double ENHANCED_ARMOR_TOUGHNESS = 35.0;
    /** 强化版灵魂吸取：每只存活凋零回血 500 */
    private static final float ENHANCED_DRAIN_HEAL_PER_WITHER = 500.0F;
    /** 强化版近战附带的灵魂侵蚀时长（tick）与等级（V） */
    private static final int EROSION_DURATION = 200;
    private static final int EROSION_AMPLIFIER = 4;
    /** 每隔 10 秒（200 tick）检测一次范围内玩家数量，动态切换强化/普通模式 */
    private static final int ENHANCED_CHECK_INTERVAL = 200;
    /** 动态检测玩家数量的半径（与祭坛召唤时一致） */
    private static final double ENHANCED_CHECK_RADIUS = 64.0;

    private Phase phase = Phase.NORMAL;
    private boolean drainUsed;
    /** 是否为灵魂晶体召唤的强化版 */
    private boolean enhancedMode;
    private final List<java.util.UUID> drainWraiths = new ArrayList<>();

    private boolean suppressionDone;
    private boolean spawnAnimStarted;
    /** 距离下一次玩家数量检测还剩多少 tick（0 表示立刻检测） */
    private int enhancedCheckTicks;
    private int powerCooldown = 10 * 20;
    private int bombCooldown = 8 * 20;

    /** 技能动画事件点追踪 */
    private int skillPrevElapsed = -1;
    /** 灵魂轰击已打出的光弹序号 */
    private int lastBombFired = -1;
    /** 当前灵魂轰击的光弹实体 ID（仅服务端，瞬态） */
    private final List<Integer> activeBombs = new ArrayList<>();
    /** 灵魂召唤已生成但尚未出击的怨灵/凋零（1.25s~1.5s 之间） */
    private final List<Mob> pendingCallWraiths = new ArrayList<>();

    /** 悬停点参数（锁定，避免抖动） */
    private int hoverAxis; // 0=x, 1=z
    private int hoverSign = 1;
    private float hoverDist = 5.0F;

    private final ServerBossEvent bossEvent = new ServerBossEvent(
            this.getDisplayName(), BossEvent.BossBarColor.BLUE, BossEvent.BossBarOverlay.PROGRESS);

    private final AnimatableInstanceCache geoCache = new InstancedAnimatableInstanceCache(this);

    public ToulingConglingEntity(EntityType<ToulingConglingEntity> type, Level level) {
        super(type, level);
        this.xpReward = 0;
        this.setNoAi(false);
        this.setPersistenceRequired();
        // 漂浮移动
        this.moveControl = new FlyingMoveControl(this, 20, true);
    }

    @Override
    protected void registerGoals() {
        this.targetSelector.addGoal(1, new NearestAttackableTargetGoal(this, Player.class, true, true));
        this.goalSelector.addGoal(4, new LookAtPlayerGoal(this, Player.class, 8.0F));
    }

    /**
     * 透灵从零属性：生命 5000，攻击 10/30/50（随难度动态同步，基础 10），
     * 护甲 20，盔甲韧性 15，抗击退；飞行单位。
     */
    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 5000.0)
                .add(Attributes.MOVEMENT_SPEED, 0.3)
                .add(Attributes.FLYING_SPEED, 0.6)
                .add(Attributes.ATTACK_DAMAGE, 10.0)
                .add(Attributes.ARMOR, 20.0)
                .add(Attributes.ARMOR_TOUGHNESS, 15.0)
                .add(Attributes.FOLLOW_RANGE, 64.0)
                .add(Attributes.KNOCKBACK_RESISTANCE, 1.0);
    }

    public boolean isEnhanced() {
        return this.enhancedMode;
    }

    /**
     * 设置强化模式与血量档位。
     * @param enhanced 是否为强化版（决定护甲 50/韧性 35、攻击 30/50/80、凋零召唤物、500 回血、灵魂侵蚀 V）
     * @param manyChallengers 是否有 2 名及以上挑战者（决定最大生命 8000，否则 5000）
     * 祭坛召唤时调用一次用于初始锁定；出生后由 customServerAiStep 每 10 秒按玩家数动态重算。
     * 切换时血量按比例同步，避免满血切换后血条不满，并清理已召唤的怨灵/凋零（类型不同）。
     */
    public void setEnhancedMode(boolean enhanced, boolean manyChallengers) {
        float targetMax = manyChallengers ? ENHANCED_MAX_HEALTH : 5000.0F;
        boolean alreadyMatched = this.enhancedMode == enhanced
                && Math.abs(this.getMaxHealth() - targetMax) < 0.5F;
        if (alreadyMatched) {
            return;
        }

        float oldMax = this.getMaxHealth();
        this.enhancedMode = enhanced;

        AttributeInstance armor = this.getAttribute(Attributes.ARMOR);
        AttributeInstance toughness = this.getAttribute(Attributes.ARMOR_TOUGHNESS);
        AttributeInstance health = this.getAttribute(Attributes.MAX_HEALTH);
        if (armor != null) {
            armor.setBaseValue(enhanced ? ENHANCED_ARMOR : 20.0);
        }
        if (toughness != null) {
            toughness.setBaseValue(enhanced ? ENHANCED_ARMOR_TOUGHNESS : 15.0);
        }
        if (health != null) {
            health.setBaseValue(targetMax);
        }
        // 血量按比例同步（满血 → 新上限；残血 → 同比例残血）
        float ratio = oldMax > 0.0F ? this.getHealth() / oldMax : 1.0F;
        this.setHealth(this.getMaxHealth() * ratio);

        this.syncDifficultyAttack();
        // 召唤物类型随模式变化，切换时统一清理
        this.clearSummons();
    }

    /** 清理所有归属本 BOSS 的怨灵与削弱凋零 */
    private void clearSummons() {
        if (!(this.level() instanceof ServerLevel level)) {
            return;
        }
        for (WraithEntity wraith : level.getEntitiesOfClass(WraithEntity.class, AABB.INFINITE,
                w -> w.getOwner() == this)) {
            wraith.discard();
        }
        for (SoulDrainWitherEntity wither : level.getEntitiesOfClass(
                SoulDrainWitherEntity.class, AABB.INFINITE, w -> w.isOwnedBy(this.getUUID()))) {
            wither.discard();
        }
        this.drainWraiths.clear();
        this.pendingCallWraiths.clear();
    }

    /** 强化版近战命中：附加 5 级灵魂侵蚀（10s） */
    @Override
    public boolean doHurtTarget(Entity target) {
        boolean hit = super.doHurtTarget(target);
        if (hit && this.enhancedMode && target instanceof LivingEntity living) {
            living.addEffect(new MobEffectInstance(
                    AllinzeroEffects.SOUL_EROSION, EROSION_DURATION, EROSION_AMPLIFIER, false, true));
        }
        return hit;
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
        if (this.isEffectiveAi() || this.isControlledByLocalInstance()) {
            float speedMultiplier = (float) this.getAttributeValue(Attributes.MOVEMENT_SPEED);
            this.moveRelative(speedMultiplier, travelVector);
            this.move(MoverType.SELF, this.getDeltaMovement());
            this.setDeltaMovement(this.getDeltaMovement().scale(0.91));
        } else {
            super.travel(travelVector);
        }
    }

    @Override
    public boolean isFlying() {
        return !this.onGround();
    }

    @Override
    public boolean causeFallDamage(float distance, float multiplier, DamageSource source) {
        return false;
    }

    /**
     * 定身条件：灵魂吸取阶段，或正在播放会定身的技能动画。
     * fly / fly_to_left / fly_to_right 属于移动动画，不阻止移动。
     */
    @Override
    public boolean isImmobile() {
        if (this.phase == Phase.DRAIN) {
            return true;
        }
        byte id = this.getAnimationId();
        return (id == ANIM_SUPPRESSION || id == ANIM_CALL || id == ANIM_POWER
                || id == ANIM_BLAST || id == ANIM_ABSORPTION)
                && this.getAnimationTicks() > 0;
    }

    @Override
    public SoundEvent getHurtSound(DamageSource source) {
        return BuiltInRegistries.SOUND_EVENT.get(net.minecraft.resources.ResourceLocation.parse("entity.generic.hurt"));
    }

    @Override
    public SoundEvent getDeathSound() {
        return BuiltInRegistries.SOUND_EVENT.get(net.minecraft.resources.ResourceLocation.parse("entity.generic.death"));
    }

    // ===== 受伤与无敌 =====

    @Override
    public boolean hurt(DamageSource source, float amount) {
        // 灵魂吸取期间无敌
        if (this.phase == Phase.DRAIN) {
            return false;
        }
        Entity direct = source.getDirectEntity();
        if (direct instanceof ThrownPotion || direct instanceof AreaEffectCloud
                || source.typeHolder().is(NeoForgeMod.POISON_DAMAGE)
                || source.is(DamageTypes.FALL)
                || source.is(DamageTypes.DROWN)
                || source.is(DamageTypes.EXPLOSION)
                || source.is(DamageTypes.PLAYER_EXPLOSION)
                || source.is(DamageTypes.FALLING_ANVIL)
                || source.is(DamageTypes.DRAGON_BREATH)) {
            return false;
        }

        float multiplier = this.phase == Phase.WEAKENED ? 4.0F : 1.0F;
        // 灵魂代偿第一重（护甲减免前，与灵魂生物类型口径相同）：
        // 火焰附加 II → +350%（×4.5），火焰附加 I → +200%（×3），火矢 → ×3，火焰/岩浆 → ×2。
        // SoulDamageHandler 在护甲减免后会再放大一次（双重放大，设计保留）。
        multiplier *= this.resolveFireHitMultiplier(source);
        return super.hurt(source, amount * multiplier);
    }

    /** 解析火焰类命中的独立倍率：FA2 ×4.5、FA1 ×3、火矢 ×3、火焰/岩浆 ×2，其余 ×1 */
    private float resolveFireHitMultiplier(DamageSource source) {
        var registry = this.level().registryAccess().lookupOrThrow(Registries.ENCHANTMENT);
        Holder<Enchantment> flame = registry.getOrThrow(Enchantments.FLAME);
        Holder<Enchantment> fireAspect = registry.getOrThrow(Enchantments.FIRE_ASPECT);
        Entity direct = source.getDirectEntity();

        // 火矢/燃烧的弓箭
        if (direct instanceof AbstractArrow arrow) {
            if (arrow.isOnFire()
                    || EnchantmentHelper.getItemEnchantmentLevel(flame, arrow.getWeaponItem()) > 0) {
                return 3.0F;
            }
        } else if (source.getEntity() instanceof LivingEntity living) {
            // 近战火焰附加分级
            int level = EnchantmentHelper.getItemEnchantmentLevel(fireAspect, living.getMainHandItem());
            if (level >= 2) {
                return 4.5F;
            } else if (level == 1) {
                return 3.0F;
            }
        }
        // 普通火焰/岩浆
        if (source.is(DamageTypeTags.IS_FIRE) || source.is(DamageTypes.HOT_FLOOR)) {
            return 2.0F;
        }
        return 1.0F;
    }

    @Override
    public boolean ignoreExplosion(Explosion explosion) {
        return true;
    }

    // ===== 动画数据 =====

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DATA_ANIM_ID, ANIM_NONE);
        builder.define(DATA_ANIM_TICKS, 0);
    }

    public static int getAnimationDurationTicks(byte animId) {
        return switch (animId) {
            case ANIM_SUPPRESSION -> 54;
            case ANIM_CALL, ANIM_POWER, ANIM_BLAST -> 40;
            case ANIM_ABSORPTION -> 420;
            case ANIM_FLY_LEFT, ANIM_FLY_RIGHT -> 25;
            case ANIM_FLY -> 20;
            default -> 0;
        };
    }

    /** 播放一次性技能动画 */
    private void playSkillAnimation(byte animId) {
        this.entityData.set(DATA_ANIM_ID, animId);
        this.entityData.set(DATA_ANIM_TICKS, getAnimationDurationTicks(animId));
        this.skillPrevElapsed = -1;
        this.lastBombFired = -1;
    }

    public byte getAnimationId() {
        return this.entityData.get(DATA_ANIM_ID);
    }

    public int getAnimationTicks() {
        return this.entityData.get(DATA_ANIM_TICKS);
    }

    @Override
    public void aiStep() {
        super.aiStep();
        int ticksLeft = this.entityData.get(DATA_ANIM_TICKS);
        if (ticksLeft > 0) {
            int next = ticksLeft - 1;
            this.entityData.set(DATA_ANIM_TICKS, next);
            if (next == 0) {
                this.entityData.set(DATA_ANIM_ID, ANIM_NONE);
            }
        }
    }

    // ===== 服务端 AI =====

    @Override
    protected void customServerAiStep() {
        super.customServerAiStep();
        if (!(this.level() instanceof ServerLevel level)) {
            return;
        }

        // 出场：先播放灵魂镇压，动画播完时释放
        if (!this.spawnAnimStarted) {
            this.spawnAnimStarted = true;
            this.playSkillAnimation(ANIM_SUPPRESSION);
        }

        this.bossEvent.setProgress(this.getHealth() / this.getMaxHealth());
        this.syncDifficultyAttack();

        // 每 10 秒按范围内玩家数量动态切换强化/普通模式；吸取进行中不切换，避免打断结算
        if (this.phase != Phase.DRAIN) {
            if (--this.enhancedCheckTicks <= 0) {
                this.enhancedCheckTicks = ENHANCED_CHECK_INTERVAL;
                int challengers = level.getEntitiesOfClass(Player.class,
                        AABB.ofSize(this.position(),
                                ENHANCED_CHECK_RADIUS * 2, ENHANCED_CHECK_RADIUS * 2, ENHANCED_CHECK_RADIUS * 2),
                        p -> p.isAlive() && !p.isSpectator()).size();
                this.setEnhancedMode(challengers >= 2, challengers >= 2);
            }
        }

        // 正在播放技能动画：按动画时间点触发事件；不做常规行动
        byte animId = this.getAnimationId();
        if (animId != ANIM_NONE && animId != ANIM_FLY
                && animId != ANIM_FLY_LEFT && animId != ANIM_FLY_RIGHT) {
            this.tickSkillTimeline(level, animId);
            return;
        }

        if (this.phase == Phase.DRAIN) {
            return;
        }

        LivingEntity target = this.getTarget();

        // 灵魂吸取：血量 <30% 且从未触发
        if (!this.drainUsed && this.getHealth() > 0.0F
                && this.getHealth() <= this.getMaxHealth() * DRAIN_HP_THRESHOLD) {
            this.beginSoulDrain(level);
            return;
        }

        // 技能二：灵魂召唤（与目标 y 轴高度不同）
        if (target != null && target.isAlive() && this.distanceToSqr(target) <= SUMMON_RANGE * SUMMON_RANGE
                && Math.abs(target.getY() - this.getY()) >= SUMMON_Y_DIFF) {
            this.playSkillAnimation(ANIM_CALL);
            this.playSoulSound(SoundEvents.EVOKER_CAST_SPELL, 1.2F, 0.7F);
            return;
        }

        // 技能三：灵魂之力（冷却结束）
        if (--this.powerCooldown <= 0) {
            this.powerCooldown = POWER_COOLDOWN;
            this.playSkillAnimation(ANIM_POWER);
            this.playSoulSound(SoundEvents.ILLUSIONER_CAST_SPELL, 1.2F, 0.8F);
            return;
        }

        // 技能四：灵魂轰击（冷却结束，目标在范围内）
        if (--this.bombCooldown <= 0) {
            if (target != null && target.isAlive() && this.distanceToSqr(target) <= BOMB_RANGE * BOMB_RANGE) {
                this.beginSoulBlast(level);
                return;
            }
            this.bombCooldown = 20;
        }

        // 常规漂浮定位
        this.tickHoverMovement(target);
    }

    /**
     * 技能动画时间线：elapsed = 已播放 tick，在跨越 25（1.25s）/30（1.5s）/54 等阈值时触发事件。
     */
    private void tickSkillTimeline(ServerLevel level, byte animId) {
        int duration = getAnimationDurationTicks(animId);
        int elapsed = duration - this.getAnimationTicks();

        switch (animId) {
            case ANIM_SUPPRESSION -> {
                if (!this.suppressionDone && elapsed >= 54) {
                    this.suppressionDone = true;
                    this.castSoulSuppression(level);
                }
            }
            case ANIM_CALL -> {
                // 第 1.25 秒：召唤 5 怨灵（尚未出击）
                if (this.skillPrevElapsed < 25 && elapsed >= 25) {
                    this.summonCallWraiths(level);
                }
                // 第 1.5 秒：怨灵向玩家发起攻击（穿墙，类似恼鬼）
                if (this.skillPrevElapsed < 30 && elapsed >= 30) {
                    this.releaseCallWraiths();
                }
            }
            case ANIM_POWER -> {
                // 第 1.25 秒：赋予自身增益
                if (this.skillPrevElapsed < 25 && elapsed >= 25) {
                    this.castSoulPower();
                }
            }
            case ANIM_BLAST -> {
                // 第 1.25 秒起 0.5s 内依次打出（间隔 2t）
                if (elapsed >= 25) {
                    int due = Math.min((elapsed - 25) / 2, BOMB_COUNT - 1);
                    while (this.lastBombFired < due) {
                        this.fireNextBomb(level, ++this.lastBombFired);
                    }
                }
            }
            case ANIM_ABSORPTION -> {
                if (this.skillPrevElapsed < DRAIN_RESOLVE_TICKS && elapsed >= DRAIN_RESOLVE_TICKS) {
                    this.resolveSoulDrain(level);
                }
            }
            default -> { }
        }

        this.skillPrevElapsed = elapsed;
    }

    /**
     * 常规漂浮：将自身定位在距目标正侧方位 3.5~7.5 格、y 轴 3 格以内。
     * 距离过远 → fly 动画飞近；目标在左/右侧 → 转向动画并转身。
     */
    private void tickHoverMovement(LivingEntity target) {
        if (target == null) {
            // 无目标：悬停防下沉
            if (this.getDeltaMovement().y < 0.0 && this.random.nextInt(3) == 0) {
                this.setDeltaMovement(this.getDeltaMovement().x,
                        this.getDeltaMovement().y + 0.04, this.getDeltaMovement().z);
            }
            return;
        }

        // 目标朝向角与当前朝向的差（度）：>0 需向右转，<0 需向左转
        double desiredYaw = Math.toDegrees(Math.atan2(
                target.getX() - this.getX(), target.getZ() - this.getZ()));
        float yawDelta = (float) Mth.wrapDegrees(desiredYaw - this.getYRot());

        // 悬停点参数：目标变化或明显偏离时重新锁定
        Vec3 desiredPos = this.resolveHoverPoint(target);
        double err = this.position().distanceTo(desiredPos);

        if (err > 1.5) {
            // 距离过远：fly 动画飞近
            this.getMoveControl().setWantedPosition(
                    desiredPos.x, desiredPos.y, desiredPos.z, 0.5);
            if (this.getAnimationId() != ANIM_FLY) {
                this.entityData.set(DATA_ANIM_ID, ANIM_FLY);
            }
            this.entityData.set(DATA_ANIM_TICKS, 20);
        } else {
            // 到位：停止飞近，清除 fly 动画
            if (this.getAnimationId() == ANIM_FLY) {
                this.entityData.set(DATA_ANIM_ID, ANIM_NONE);
                this.entityData.set(DATA_ANIM_TICKS, 0);
            }
            // 目标在左/右侧且当前无动画：播放对应转向动画
            if (Math.abs(yawDelta) > TURN_THRESHOLD && this.getAnimationId() == ANIM_NONE) {
                byte turnAnim = yawDelta > 0 ? ANIM_FLY_RIGHT : ANIM_FLY_LEFT;
                this.playSkillAnimation(turnAnim);
            }
        }

        // 平滑转身面向目标
        float step = Mth.clamp(yawDelta, -TURN_SPEED, TURN_SPEED);
        float newYaw = this.getYRot() + step;
        this.setYRot(newYaw);
        this.setYBodyRot(newYaw);
        this.setYHeadRot(newYaw);
    }

    /** 计算悬停点：目标正侧方位（x 或 z 轴）3.5~7.5 格，y 轴保持在 3 格以内 */
    private Vec3 resolveHoverPoint(LivingEntity target) {
        // 参数锁定：若当前实际位置与锁定参数严重不符（目标移动等），保持参数直到失位
        double px = target.getX();
        double py = target.getY();
        double pz = target.getZ();

        double x = this.hoverAxis == 0 ? px + this.hoverSign * this.hoverDist : px;
        double z = this.hoverAxis == 1 ? pz + this.hoverSign * this.hoverDist : pz;

        // y：当前在带内则保持自身 y，否则贴到目标 y
        double y = Math.abs(this.getY() - py) <= Y_BAND ? this.getY() : py;

        // 若当前位置偏离锁定点 4 格以上，重新随机一组参数并据此取点
        if (this.position().distanceTo(new Vec3(x, y, z)) > 4.0) {
            this.hoverAxis = this.random.nextBoolean() ? 0 : 1;
            this.hoverSign = this.random.nextBoolean() ? 1 : -1;
            this.hoverDist = Mth.randomBetween(this.random, MIN_HOVER, MAX_HOVER);
            x = this.hoverAxis == 0 ? px + this.hoverSign * this.hoverDist : px;
            z = this.hoverAxis == 1 ? pz + this.hoverSign * this.hoverDist : pz;
            y = Math.abs(this.getY() - py) <= Y_BAND ? this.getY() : py;
        }
        return new Vec3(x, y, z);
    }

    // ===== 技能一：灵魂镇压 =====

    private void castSoulSuppression(ServerLevel level) {
        this.playSoulSound(SoundEvents.ENDER_DRAGON_GROWL, 1.4F, 0.6F);
        Vec3 bossPos = this.position();
        Vec3 look = this.getLookAngle();
        Vec3 horizontalLook = (Math.abs(look.x) < 1.0E-4 && Math.abs(look.z) < 1.0E-4)
                ? new Vec3(0.0, 0.0, 1.0)
                : new Vec3(look.x, 0.0, look.z).normalize();
        // 面前一格位置
        Vec3 front = bossPos.add(horizontalLook.x, 0.0, horizontalLook.z);

        for (ServerPlayer player : level.players()) {
            if (player.isSpectator()) {
                continue;
            }
            double dx = player.getX() - bossPos.x;
            double dz = player.getZ() - bossPos.z;
            double horizontalDist = Math.sqrt(dx * dx + dz * dz);
            boolean inside = horizontalDist <= SUPPRESS_RADIUS
                    && player.getY() >= bossPos.y - 0.5
                    && player.getY() <= bossPos.y + SUPPRESS_HEIGHT;
            if (!inside) {
                // 超出范围：直接拉到面前一格
                player.teleportTo(level, front.x, this.getY(), front.z,
                        java.util.Set.<RelativeMovement>of(), player.getYRot(), player.getXRot());
                player.setDeltaMovement(Vec3.ZERO);
                player.resetFallDistance();
                this.playSoulSound(SoundEvents.BEACON_ACTIVATE, 0.8F, 0.5F);
            } else {
                player.setDeltaMovement(player.getDeltaMovement().scale(0.3));
            }
        }
    }

    // ===== 技能二：灵魂召唤 =====

    /** 第 1.25 秒：在身边环绕召唤 5 怨灵（强化版为削弱凋零），暂不设目标 */
    private void summonCallWraiths(ServerLevel level) {
        this.pendingCallWraiths.clear();
        for (int i = 0; i < SUMMON_WRAITH_COUNT; i++) {
            float angle = (float) Math.toRadians(
                    (360.0F / SUMMON_WRAITH_COUNT) * i + this.random.nextFloat() * 30.0F);
            double x = this.getX() + Math.cos(angle) * (1.5 + this.random.nextDouble());
            double y = this.getY() + 0.5 + this.random.nextInt(2);
            double z = this.getZ() + Math.sin(angle) * (1.5 + this.random.nextDouble());
            // 暂不设目标：原地停留，等待 1.5s 出击
            Mob summoned;
            if (this.enhancedMode) {
                summoned = this.spawnDrainWither(level, x, y, z, null);
            } else {
                WraithEntity wraith = new WraithEntity(
                        net.congling.allinzero.entity.AllinzeroEntity.WRAITH.get(), level);
                wraith.moveTo(x, y, z, this.random.nextFloat() * 360.0F, 0.0F);
                wraith.setOwner(this);
                wraith.setBoundOrigin(BlockPos.containing(x, y, z));
                level.addFreshEntity(wraith);
                level.sendParticles(net.minecraft.core.particles.ParticleTypes.SCULK_SOUL,
                        x, y, z, 20, 0.3, 0.4, 0.3, 0.01);
                summoned = wraith;
            }
            this.pendingCallWraiths.add(summoned);
        }
    }

    /** 第 1.5 秒：怨灵/凋零锁定玩家并发起攻击（无视方块阻挡） */
    private void releaseCallWraiths() {
        LivingEntity prey = this.getTarget();
        for (Mob summoned : this.pendingCallWraiths) {
            if (summoned.isAlive() && prey != null && prey.isAlive()) {
                summoned.setTarget(prey);
            }
        }
        this.pendingCallWraiths.clear();
    }

    // ===== 技能三：灵魂之力 =====

    private void castSoulPower() {
        this.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, POWER_DURATION, 4, false, true));
        this.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, POWER_DURATION, 2, false, true));
        this.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, POWER_DURATION, 1, false, true));
        this.playSoulSound(SoundEvents.BEACON_POWER_SELECT, 1.2F, 0.8F);
    }

    // ===== 技能四：灵魂轰击 =====

    /** 施法开始：5 颗光弹从身后生成，上浮至 y+5 并沿垂直面朝方向的轴分散静止 */
    private void beginSoulBlast(ServerLevel level) {
        this.bombCooldown = BOMB_COOLDOWN;
        this.activeBombs.clear();

        Vec3 look = this.getLookAngle();
        Vec3 lookH = (new Vec3(look.x, 0.0, look.z)).normalize();
        // 面朝 x 轴 → 沿 z 分散；面朝 z 轴 → 沿 x 分散
        boolean facingX = Math.abs(look.x) >= Math.abs(look.z);
        Vec3 behind = this.position().subtract(lookH.scale(2.0));
        double highY = this.getY() + 5.0;

        for (int i = 0; i < BOMB_COUNT; i++) {
            double perp = (i - (BOMB_COUNT - 1) / 2.0) * 1.5;
            double hx = behind.x + (facingX ? 0.0 : perp);
            double hz = behind.z + (facingX ? perp : 0.0);
            Vec3 spawnPos = new Vec3(hx, this.getY() + 0.5, hz);
            Vec3 holdPos = new Vec3(hx, highY, hz);
            SoulBombEntity bomb = new SoulBombEntity(level, this, spawnPos, holdPos);
            level.addFreshEntity(bomb);
            this.activeBombs.add(bomb.getId());
        }

        this.playSkillAnimation(ANIM_BLAST);
        this.playSoulSound(SoundEvents.EVOKER_PREPARE_ATTACK, 1.0F, 0.6F);
    }

    /** 按 0.5s 内的节奏依次打出指定序号的光弹 */
    private void fireNextBomb(ServerLevel level, int index) {
        if (index >= this.activeBombs.size()) {
            return;
        }
        Entity entity = level.getEntity(this.activeBombs.get(index));
        if (entity instanceof SoulBombEntity bomb && !bomb.isFired()) {
            LivingEntity prey = this.getTarget();
            if (prey == null || !prey.isAlive()) {
                prey = level.getNearestPlayer(this, BOMB_RANGE);
            }
            if (prey != null) {
                bomb.fire(prey, this.computeBombDamage());
            }
        }
    }

    /** 光弹伤害：15 点基础 + 自身力量效果 50% 的加成（力量每级 +3，按 50% 计入） */
    private float computeBombDamage() {
        float damage = BOMB_BASE_DAMAGE;
        MobEffectInstance strength = this.getEffect(MobEffects.DAMAGE_BOOST);
        if (strength != null) {
            damage += 0.5F * 3.0F * (strength.getAmplifier() + 1);
        }
        return damage;
    }

    // ===== 被动：灵魂吸取 =====

    private void beginSoulDrain(ServerLevel level) {
        this.phase = Phase.DRAIN;
        this.drainUsed = true;
        this.drainWraiths.clear();
        this.playSkillAnimation(ANIM_ABSORPTION);
        this.playSoulSound(SoundEvents.WITHER_AMBIENT, 1.6F, 0.5F);

        LivingEntity prey = this.getTarget();
        for (int i = 0; i < DRAIN_WRAITH_COUNT; i++) {
            float angle = (float) Math.toRadians(i * 90.0F + this.random.nextFloat() * 30.0F);
            double x = this.getX() + Math.cos(angle) * (2.0 + this.random.nextDouble());
            double y = this.getY() + 0.5 + this.random.nextInt(2);
            double z = this.getZ() + Math.sin(angle) * (2.0 + this.random.nextDouble());
            Mob summoned;
            if (this.enhancedMode) {
                // 强化版：削弱凋零
                summoned = this.spawnDrainWither(level, x, y, z, prey);
            } else {
                summoned = this.spawnWraith(level, x, y, z, prey);
            }
            this.drainWraiths.add(summoned.getUUID());
        }
    }

    private void resolveSoulDrain(ServerLevel level) {
        int alive = 0;
        for (java.util.UUID id : this.drainWraiths) {
            Entity entity = level.getEntity(id);
            if (entity instanceof LivingEntity living && living.isAlive()) {
                alive++;
            }
        }
        this.drainWraiths.clear();
        if (alive == 0) {
            // 全部击杀：虚弱状态，受到全部伤害 +300%（×4）
            this.phase = Phase.WEAKENED;
            this.playSoulSound(SoundEvents.BEACON_DEACTIVATE, 1.2F, 0.6F);
            level.sendParticles(net.minecraft.core.particles.ParticleTypes.SOUL_FIRE_FLAME,
                    this.getX(), this.getY() + 1.0, this.getZ(), 80, 1.0, 1.2, 1.0, 0.0);
        } else {
            // 有幸存：普通版每只回复 75，强化版每只回复 500
            float perWither = this.enhancedMode
                    ? ENHANCED_DRAIN_HEAL_PER_WITHER : DRAIN_HEAL_PER_WRAITH;
            this.heal(perWither * alive);
            this.phase = Phase.NORMAL;
            this.playSoulSound(SoundEvents.WITHER_SPAWN, 1.0F, 0.7F);
        }
    }

    /** 强化版：召唤一只削弱凋零并绑定 owner（死亡时由 BOSS 统一清理）；target 可为 null */
    private SoulDrainWitherEntity spawnDrainWither(ServerLevel level, double x, double y, double z,
                                                     LivingEntity target) {
        SoulDrainWitherEntity wither = new SoulDrainWitherEntity(
                net.congling.allinzero.entity.AllinzeroEntity.SOUL_DRAIN_WITHER.get(), level);
        wither.moveTo(x, y, z, this.random.nextFloat() * 360.0F, 0.0F);
        wither.bindOwner(this.getUUID());
        if (target != null && target.isAlive()) {
            wither.setTarget(target);
        } else {
            Player nearest = level.getNearestPlayer(this, 48.0);
            if (nearest != null) {
                wither.setTarget(nearest);
            }
        }
        level.addFreshEntity(wither);
        level.sendParticles(net.minecraft.core.particles.ParticleTypes.SOUL_FIRE_FLAME,
                x, y, z, 20, 0.4, 0.5, 0.4, 0.01);
        return wither;
    }

    /** 召唤怨灵并绑定 owner（死亡时由 BOSS 统一清理） */
    private WraithEntity spawnWraith(ServerLevel level, double x, double y, double z,
                                     LivingEntity target) {
        WraithEntity wraith = new WraithEntity(
                net.congling.allinzero.entity.AllinzeroEntity.WRAITH.get(), level);
        wraith.moveTo(x, y, z, this.random.nextFloat() * 360.0F, 0.0F);
        wraith.setOwner(this);
        wraith.setBoundOrigin(BlockPos.containing(x, y, z));
        if (target != null && target.isAlive()) {
            wraith.setTarget(target);
        } else {
            Player nearest = level.getNearestPlayer(this, 48.0);
            if (nearest != null) {
                wraith.setTarget(nearest);
            }
        }
        level.addFreshEntity(wraith);
        level.sendParticles(net.minecraft.core.particles.ParticleTypes.SCULK_SOUL,
                x, y, z, 20, 0.3, 0.4, 0.3, 0.01);
        return wraith;
    }

    @Override
    public void die(DamageSource source) {
        this.bossEvent.setProgress(0.0F);
        // 死亡：召唤出的怨灵与凋零全部消失
        this.clearSummons();
        super.die(source);
    }

    @Override
    public void startSeenByPlayer(ServerPlayer player) {
        super.startSeenByPlayer(player);
        this.bossEvent.addPlayer(player);
    }

    @Override
    public void stopSeenByPlayer(ServerPlayer player) {
        super.stopSeenByPlayer(player);
        this.bossEvent.removePlayer(player);
    }

    @Override
    public boolean removeWhenFarAway(double distanceToClosestPlayer) {
        return false;
    }

    private void playSoulSound(SoundEvent sound, float volume, float pitch) {
        if (this.level() instanceof ServerLevel level) {
            level.playSound(null, this.getX(), this.getY(), this.getZ(),
                    sound, SoundSource.HOSTILE, volume, pitch);
        }
    }

    private void syncDifficultyAttack() {
        AttributeInstance attack = this.getAttribute(Attributes.ATTACK_DAMAGE);
        if (attack == null) {
            return;
        }
        float damage;
        if (this.enhancedMode) {
            // 强化版：30/50/80
            damage = switch (this.level().getDifficulty()) {
                case HARD -> 80.0F;
                case NORMAL -> 50.0F;
                default -> 30.0F;
            };
        } else {
            damage = switch (this.level().getDifficulty()) {
                case HARD -> 50.0F;
                case NORMAL -> 30.0F;
                default -> 10.0F;
            };
        }
        attack.setBaseValue(damage);
    }

    // ===== NBT =====

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putBoolean("DrainUsed", this.drainUsed);
        tag.putBoolean("Enhanced", this.enhancedMode);
        tag.putInt("Phase", this.phase.ordinal());
        tag.putInt("PowerCooldown", this.powerCooldown);
        tag.putInt("BombCooldown", this.bombCooldown);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        this.drainUsed = tag.getBoolean("DrainUsed");
        this.enhancedMode = tag.getBoolean("Enhanced");
        int phaseOrd = tag.getInt("Phase");
        this.phase = phaseOrd >= 0 && phaseOrd < Phase.values().length
                ? Phase.values()[phaseOrd] : Phase.NORMAL;
        this.powerCooldown = tag.getInt("PowerCooldown");
        this.bombCooldown = tag.getInt("BombCooldown");
    }

    // ===== GeckoLib =====

    private static final RawAnimation ANIM_SUPPRESSION_DEF =
            RawAnimation.begin().thenPlay("animation.touling_congling.soul_suppression_prepare");
    private static final RawAnimation ANIM_CALL_DEF =
            RawAnimation.begin().thenPlay("animation.touling_congling.soul_call");
    private static final RawAnimation ANIM_POWER_DEF =
            RawAnimation.begin().thenPlay("animation.touling_congling.soul_power");
    private static final RawAnimation ANIM_BLAST_DEF =
            RawAnimation.begin().thenPlay("animation.touling_congling.soul_blast");
    private static final RawAnimation ANIM_ABSORPTION_DEF =
            RawAnimation.begin().thenPlay("animation.touling_congling.soul_absorption");
    private static final RawAnimation ANIM_FLY_DEF =
            RawAnimation.begin().thenLoop("animation.touling_congling.fly");
    private static final RawAnimation ANIM_FLY_LEFT_DEF =
            RawAnimation.begin().thenPlay("animation.touling_congling.fly_to_left");
    private static final RawAnimation ANIM_FLY_RIGHT_DEF =
            RawAnimation.begin().thenPlay("animation.touling_congling.fly_to_right");

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "main", 5, this::mainController));
    }

    private PlayState mainController(software.bernie.geckolib.animation.AnimationState<ToulingConglingEntity> state) {
        return switch (this.getAnimationId()) {
            case ANIM_SUPPRESSION -> state.setAndContinue(ANIM_SUPPRESSION_DEF);
            case ANIM_CALL -> state.setAndContinue(ANIM_CALL_DEF);
            case ANIM_POWER -> state.setAndContinue(ANIM_POWER_DEF);
            case ANIM_BLAST -> state.setAndContinue(ANIM_BLAST_DEF);
            case ANIM_ABSORPTION -> state.setAndContinue(ANIM_ABSORPTION_DEF);
            case ANIM_FLY -> state.setAndContinue(ANIM_FLY_DEF);
            case ANIM_FLY_LEFT -> state.setAndContinue(ANIM_FLY_LEFT_DEF);
            case ANIM_FLY_RIGHT -> state.setAndContinue(ANIM_FLY_RIGHT_DEF);
            default -> PlayState.STOP;
        };
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.geoCache;
    }
}
