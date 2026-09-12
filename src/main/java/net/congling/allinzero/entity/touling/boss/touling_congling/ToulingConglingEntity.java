package net.congling.allinzero.entity.touling.boss.touling_congling;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.BossEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ThrownPotion;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.NeoForgeMod;
import net.neoforged.neoforge.event.entity.RegisterSpawnPlacementsEvent;

public class ToulingConglingEntity extends Monster {
    public final AnimationState animationState0 = new AnimationState();
    private final ServerBossEvent bossInfo;

    public ToulingConglingEntity(EntityType<ToulingConglingEntity> type, Level world) {
        super(type, world);
        this.bossInfo = new ServerBossEvent(this.getDisplayName(), BossEvent.BossBarColor.BLUE, BossEvent.BossBarOverlay.PROGRESS);
        this.xpReward = 0;
        this.setNoAi(false);
        this.setPersistenceRequired();
    }

    protected void registerGoals() {
        super.registerGoals();
        this.targetSelector.addGoal(1, new NearestAttackableTargetGoal(this, Player.class, true, true));
        this.goalSelector.addGoal(2, new MeleeAttackGoal(this, 1.2, true) {
            protected boolean canPerformAttack(LivingEntity entity) {
                return this.isTimeToAttack() && this.mob.distanceToSqr(entity) < (double)(this.mob.getBbWidth() * this.mob.getBbWidth() + entity.getBbWidth()) && this.mob.getSensing().hasLineOfSight(entity);
            }
        });
        this.goalSelector.addGoal(3, new RandomStrollGoal(this, (double)1.0F));
    }

    public boolean removeWhenFarAway(double distanceToClosestPlayer) {
        return false;
    }

    public Vec3 getPassengerRidingPosition(Entity entity) {
        return super.getPassengerRidingPosition(entity).add((double)0.0F, (double)-0.35F, (double)0.0F);
    }

    public SoundEvent getHurtSound(DamageSource ds) {
        return (SoundEvent) BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("entity.generic.hurt"));
    }

    public SoundEvent getDeathSound() {
        return (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("entity.generic.death"));
    }

    public boolean hurt(DamageSource damagesource, float amount) {
        if (!(damagesource.getDirectEntity() instanceof ThrownPotion) && !(damagesource.getDirectEntity() instanceof AreaEffectCloud) && !damagesource.typeHolder().is(NeoForgeMod.POISON_DAMAGE)) {
            if (damagesource.is(DamageTypes.FALL)) {
                return false;
            } else if (damagesource.is(DamageTypes.DROWN)) {
                return false;
            } else if (!damagesource.is(DamageTypes.EXPLOSION) && !damagesource.is(DamageTypes.PLAYER_EXPLOSION)) {
                if (damagesource.is(DamageTypes.FALLING_ANVIL)) {
                    return false;
                } else {
                    return damagesource.is(DamageTypes.DRAGON_BREATH) ? false : super.hurt(damagesource, amount);
                }
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    public boolean ignoreExplosion(Explosion explosion) {
        return true;
    }

    public void tick() {
        super.tick();
        if (this.level().isClientSide()) {
            this.animationState0.animateWhen(true, this.tickCount);
        }

    }

    public void startSeenByPlayer(ServerPlayer player) {
        super.startSeenByPlayer(player);
        this.bossInfo.addPlayer(player);
    }

    public void stopSeenByPlayer(ServerPlayer player) {
        super.stopSeenByPlayer(player);
        this.bossInfo.removePlayer(player);
    }

    public void customServerAiStep() {
        super.customServerAiStep();
        this.bossInfo.setProgress(this.getHealth() / this.getMaxHealth());
    }

    public static void init(RegisterSpawnPlacementsEvent event) {
    }

    public static AttributeSupplier.Builder createAttributes() {
        AttributeSupplier.Builder builder = Mob.createMobAttributes();
        builder = builder.add(Attributes.MOVEMENT_SPEED, 0.5);
        builder = builder.add(Attributes.MAX_HEALTH, (double)800.0F);
        builder = builder.add(Attributes.ARMOR, (double)20.0F);
        builder = builder.add(Attributes.ATTACK_DAMAGE, (double)20.0F);
        builder = builder.add(Attributes.ARMOR_TOUGHNESS, (double)15.0F);
        return builder;
    }
}