package net.congling.allinzero.entity.touling.monster.phosphor;

import net.congling.allinzero.effect.AllinzeroDamageTypes;
import net.congling.allinzero.effect.AllinzeroEffects;
import net.congling.allinzero.entity.AllinzeroEntity;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractHurtingProjectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public class PhosphorBoltEntity extends AbstractHurtingProjectile {
    public static final float DAMAGE = 10.0F;
    public static final int POSSESSION_DURATION = 20 * 30;
    private static final double SPEED = 0.8;
    private static final int MAX_LIFE = 20 * 10;

    private int life;

    public PhosphorBoltEntity(EntityType<? extends PhosphorBoltEntity> type, Level level) {
        super(type, level);
        this.accelerationPower = 0.0;
    }

    public PhosphorBoltEntity(Level level, LivingEntity owner, Vec3 targetPos) {
        this(AllinzeroEntity.PHOSPHOR_BOLT.get(), level);
        this.setOwner(owner);
        Vec3 start = owner.getEyePosition();
        this.moveTo(start.x, start.y, start.z, this.getYRot(), this.getXRot());
        this.reapplyPosition();

        Vec3 dir = targetPos.subtract(start).normalize();
        this.setDeltaMovement(dir.scale(SPEED));
        this.hasImpulse = true;

        double horizontal = dir.horizontalDistance();
        this.setYRot((float) (Mth.atan2(dir.z, dir.x) * (180.0 / Math.PI)) - 90.0F);
        this.setXRot((float) (-(Mth.atan2(dir.y, horizontal) * (180.0 / Math.PI))));
        this.yRotO = this.getYRot();
        this.xRotO = this.getXRot();
    }

    @Override
    public void tick() {
        super.tick();
        if (this.level().isClientSide) {
            this.level().addParticle(ParticleTypes.WAX_ON,
                    this.getX(), this.getY() + 0.1, this.getZ(), 0.0, 0.0, 0.0);
        } else if (++this.life > MAX_LIFE) {
            this.discard();
        }
    }

    @Override
    protected void onHit(HitResult result) {
        super.onHit(result);
        if (!this.level().isClientSide) {
            this.level().playSound(null, this.getX(), this.getY(), this.getZ(),
                    SoundEvents.GUARDIAN_ATTACK, SoundSource.HOSTILE, 1.0F, 0.7F);
            this.discard();
        }
    }

    @Override
    protected void onHitEntity(EntityHitResult result) {
        super.onHitEntity(result);
        Entity hit = result.getEntity();
        if (!this.level().isClientSide && hit instanceof LivingEntity living && hit != this.getOwner()) {
            living.hurt(this.getDamageSource(), DAMAGE);
            if (hit instanceof Player player) {
                player.addEffect(new MobEffectInstance(AllinzeroEffects.SOUL_POSSESSION, POSSESSION_DURATION, 0));
            }
        }
    }

    private DamageSource getDamageSource() {
        return this.damageSources().source(AllinzeroDamageTypes.PHOSPHOR_BOLT,
                this.getOwner() != null ? this.getOwner() : this);
    }

    @Override
    protected boolean shouldBurn() {
        return false;
    }

    @Override
    protected net.minecraft.core.particles.ParticleOptions getTrailParticle() {
        return null;
    }

    @Override
    protected float getInertia() {
        return 1.0F;
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putInt("Life", this.life);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        this.life = tag.getInt("Life");
    }
}
