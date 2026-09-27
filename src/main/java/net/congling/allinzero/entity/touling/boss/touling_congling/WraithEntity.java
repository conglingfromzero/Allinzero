package net.congling.allinzero.entity.touling.boss.touling_congling;

import net.congling.allinzero.entity.hostile.FlyingMeleeHostileEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

/**
 * 怨灵：飞行敌对近战生物，复用 {@link FlyingMeleeHostileEntity} 的 SmartBrainLib 行为树
 * （10~15 格环绕 → 突进 10 点固定伤害 → 未命中拉回循环，索敌 48 格、攻击范围 24 格）。
 * <p>
 * 保留原 Vex 系接口（{@link #setOwner}/{@link #getOwner}/{@link #setBoundOrigin}/{@link #getBoundOrigin}）
 * 以兼容透灵从零与游荡怨灵的召唤/清理逻辑。
 */
public class WraithEntity extends FlyingMeleeHostileEntity implements Enemy {

    @Nullable
    private Mob owner;
    @Nullable
    private BlockPos boundOrigin;

    public WraithEntity(EntityType<? extends WraithEntity> type, Level level) {
        super(type, level);
        this.xpReward = 0;
    }

    @Override
    protected void registerGoals() {
        // 移动与战斗全部交由 SmartBrainLib 行为树（见 FlyingMeleeHostileEntity）
    }

    /**
     * 怨灵属性：复用飞行近战基类的速度/索敌校准，仅覆写生命、护甲与攻击。
     * MOVEMENT_SPEED/FLYING_SPEED 保持基类 0.33 以匹配突进/环绕速度校准。
     */
    public static AttributeSupplier.Builder createAttributes() {
        return FlyingMeleeHostileEntity.createAttributes()
                .add(Attributes.MAX_HEALTH, 50.0)
                .add(Attributes.ATTACK_DAMAGE, 30.0)
                .add(Attributes.ARMOR, 10.0)
                .add(Attributes.ARMOR_TOUGHNESS, 5.0);
    }

    @Nullable
    public Mob getOwner() {
        return this.owner;
    }

    public void setOwner(@Nullable Mob owner) {
        this.owner = owner;
    }

    @Nullable
    public BlockPos getBoundOrigin() {
        return this.boundOrigin;
    }

    public void setBoundOrigin(@Nullable BlockPos origin) {
        this.boundOrigin = origin;
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.VEX_AMBIENT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.VEX_DEATH;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSource) {
        return SoundEvents.VEX_HURT;
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        if (this.owner != null) {
            tag.putUUID("Owner", this.owner.getUUID());
        }
        if (this.boundOrigin != null) {
            tag.putLong("BoundOrigin", this.boundOrigin.asLong());
        }
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        // Owner 在加载时不解析（召唤者可能未加载），由召唤方调用 setOwner 重新绑定
        if (tag.contains("BoundOrigin")) {
            this.boundOrigin = BlockPos.of(tag.getLong("BoundOrigin"));
        }
    }
}
