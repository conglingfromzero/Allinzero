package net.congling.allinzero.effect;

import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;

/**
 * 灵魂附体：被磷光体的光弹命中后获得，持续 30s；
 * 效果自然到期的一瞬间，无视血量/护甲/创造模式直接杀死目标；
 * 只有饮用灵魂牛奶可以解除。
 */
public class SoulPossessionEffect extends MobEffect {

    public SoulPossessionEffect() {
        super(MobEffectCategory.HARMFUL, 0x3B1E5E);
    }

    @Override
    public boolean applyEffectTick(LivingEntity entity, int amplifier) {
        if (entity.level() instanceof ServerLevel serverLevel && entity.isAlive()) {
            MobEffectInstance instance = entity.getEffect(AllinzeroEffects.SOUL_POSSESSION);
            // applyEffectTick 在剩余时长递减之前调用，剩余 1 tick 即自然到期瞬间
            if (instance != null && instance.getDuration() <= 1) {
                Holder<DamageType> damageType = serverLevel.registryAccess()
                        .lookupOrThrow(Registries.DAMAGE_TYPE)
                        .getOrThrow(AllinzeroDamageTypes.SOUL_POSSESSION);
                entity.hurt(new DamageSource(damageType), Float.MAX_VALUE);
            }
        }
        return true;
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        // 每 tick 检测，确保精确捕捉到到期的最后一刻
        return true;
    }
}
