package net.congling.allinzero.entity.touling.boss.touling_congling;

import net.congling.allinzero.entity.AllinzeroEntity;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractHurtingProjectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;

/**
 * 灵魂光弹：灵魂轰击技能的弹射物，具有两个阶段。
 * <ul>
 *     <li>悬停阶段：从生成点上浮到 holdPos（BOSS 上方 y+5）后静止，无伤害、无碰撞；</li>
 *     <li>发射阶段：{@link #fire} 后追踪目标，命中造成传入的伤害，可被方块或盾牌阻挡。</li>
 * </ul>
 */
public class SoulBombEntity extends AbstractHurtingProjectile {
    public static final float DEFAULT_DAMAGE = 15.0F;
    private static final double HOVER_SPEED = 0.35;
    private static final double SPEED = 0.55;
    private static final double HOMING = 0.22;
    private static final int MAX_FIRED_LIFE = 20 * 6;

    private int targetId = -1;
    private int firedLife;
    private boolean fired;
    private float damage = DEFAULT_DAMAGE;
    private double holdX;
    private double holdY;
    private double holdZ;

    public SoulBombEntity(EntityType<? extends SoulBombEntity> type, Level level) {
        super(type, level);
        this.accelerationPower = 0.0;
    }

    /**
     * @param owner    发射者（透灵从零）
     * @param spawnPos 生成点（BOSS 身后）
     * @param holdPos  悬停点（BOSS y+5、沿垂直面朝方向的轴分散）
     */
    public SoulBombEntity(Level level, LivingEntity owner, Vec3 spawnPos, Vec3 holdPos) {
        this(AllinzeroEntity.SOUL_BOMB.get(), level);
        this.setOwner(owner);
        this.moveTo(spawnPos.x, spawnPos.y, spawnPos.z, this.getYRot(), this.getXRot());
        this.reapplyPosition();
        this.setDeltaMovement(Vec3.ZERO);
        this.holdX = holdPos.x;
        this.holdY = holdPos.y;
        this.holdZ = holdPos.z;
        this.noPhysics = true;
    }

    public boolean isFired() {
        return this.fired;
    }

    /** 从悬停转为追踪打出：锁定猎物与本颗光弹的最终伤害 */
    public void fire(LivingEntity prey, float finalDamage) {
        this.fired = true;
        this.targetId = prey.getId();
        this.damage = finalDamage;
        this.noPhysics = false;
        Vec3 dir = prey.getEyePosition().subtract(this.position());
        if (dir.lengthSqr() > 1.0E-6) {
            this.setDeltaMovement(dir.normalize().scale(SPEED));
        } else {
            this.setDeltaMovement(Vec3.ZERO);
        }
        this.hasImpulse = true;
    }

    @Override
    public void tick() {
        if (!this.fired) {
            // ===== 悬停阶段：自行上浮到 holdPos 静止，不走弹射物碰撞逻辑 =====
            this.tickHover();
            this.spawnHoverParticles();
            return;
        }

        // ===== 发射阶段：追踪目标 =====
        if (!this.level().isClientSide) {
            if (++this.firedLife > MAX_FIRED_LIFE) {
                this.discard();
                return;
            }
            LivingEntity target = this.resolveTarget();
            if (target != null) {
                Vec3 current = this.getDeltaMovement();
                Vec3 desired = target.getEyePosition().subtract(this.position()).normalize().scale(SPEED);
                Vec3 steered = current.add(desired.subtract(current).scale(HOMING));
                double len = steered.length();
                if (len > 1.0E-4) {
                    this.setDeltaMovement(steered.scale(SPEED / len));
                    this.hasImpulse = true;
                }
            }
        }
        super.tick();
        if (this.level().isClientSide) {
            this.level().addParticle(ParticleTypes.SCULK_SOUL,
                    this.getX(), this.getY() + 0.1, this.getZ(), 0.0, 0.0, 0.0);
        }
    }

    /** 悬停阶段：朝 holdPos 上浮，抵达后静止 */
    private void tickHover() {
        Vec3 hold = new Vec3(this.holdX, this.holdY, this.holdZ);
        Vec3 delta = hold.subtract(this.position());
        double dist = delta.length();
        if (dist <= 0.08) {
            this.setDeltaMovement(Vec3.ZERO);
            this.setPos(hold.x, hold.y, hold.z);
            return;
        }
        double stepLen = Math.min(HOVER_SPEED, dist);
        Vec3 step = delta.scale(stepLen / dist);
        this.setDeltaMovement(step);
        this.move(MoverType.SELF, step);
    }

    private void spawnHoverParticles() {
        if (this.level().isClientSide) {
            this.level().addParticle(ParticleTypes.SCULK_SOUL,
                    this.getX(), this.getY() + 0.1, this.getZ(), 0.0, 0.02, 0.0);
            if (this.random.nextInt(3) == 0) {
                this.level().addParticle(ParticleTypes.SOUL_FIRE_FLAME,
                        this.getX(), this.getY(), this.getZ(), 0.0, 0.02, 0.0);
            }
        }
    }

    @Nullable
    private LivingEntity resolveTarget() {
        if (this.level() instanceof ServerLevel serverLevel) {
            Entity entity = serverLevel.getEntity(this.targetId);
            if (entity instanceof LivingEntity living && living.isAlive()) {
                return living;
            }
        }
        return null;
    }

    @Override
    protected void onHit(HitResult result) {
        super.onHit(result);
        if (!this.level().isClientSide && result.getType() != HitResult.Type.MISS) {
            this.level().playSound(null, this.getX(), this.getY(), this.getZ(),
                    SoundEvents.GUARDIAN_ATTACK, SoundSource.HOSTILE, 1.0F, 0.6F);
            this.discard();
        }
    }

    @Override
    protected void onHitEntity(EntityHitResult result) {
        super.onHitEntity(result);
        Entity hit = result.getEntity();
        if (this.level().isClientSide || hit == this.getOwner() || !(hit instanceof LivingEntity living)) {
            return;
        }
        DamageSource source = this.getDamageSource();
        if (living instanceof Player player && player.isBlocking() && player.isDamageSourceBlocked(source)) {
            // 盾牌阻挡
            this.level().playSound(null, player.getX(), player.getY(), player.getZ(),
                    SoundEvents.SHIELD_BLOCK, SoundSource.PLAYERS, 1.0F, 0.8F + this.random.nextFloat() * 0.4F);
            this.discard();
            return;
        }
        living.hurt(source, this.damage);
    }

    private DamageSource getDamageSource() {
        Entity owner = this.getOwner();
        if (owner instanceof LivingEntity living) {
            return this.damageSources().mobAttack(living);
        }
        return this.damageSources().thrown(this, null);
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
    public boolean isPickable() {
        return true;
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putBoolean("Fired", this.fired);
        tag.putInt("TargetId", this.targetId);
        tag.putInt("FiredLife", this.firedLife);
        tag.putFloat("Damage", this.damage);
        tag.putDouble("HoldX", this.holdX);
        tag.putDouble("HoldY", this.holdY);
        tag.putDouble("HoldZ", this.holdZ);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        this.fired = tag.getBoolean("Fired");
        this.targetId = tag.getInt("TargetId");
        this.firedLife = tag.getInt("FiredLife");
        this.damage = tag.contains("Damage") ? tag.getFloat("Damage") : DEFAULT_DAMAGE;
        this.holdX = tag.getDouble("HoldX");
        this.holdY = tag.getDouble("HoldY");
        this.holdZ = tag.getDouble("HoldZ");
    }
}
