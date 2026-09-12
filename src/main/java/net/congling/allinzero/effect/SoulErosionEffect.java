package net.congling.allinzero.effect;

import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;

public class SoulErosionEffect extends MobEffect {

    public SoulErosionEffect() {
        super(MobEffectCategory.HARMFUL, 0x4A2D6B);
    }

    @Override
    public boolean applyEffectTick(LivingEntity entity, int amplifier) {
        if (entity.level() instanceof ServerLevel serverLevel && entity.isAlive()) {
            Holder<DamageType> damageType = serverLevel.registryAccess()
                    .lookupOrThrow(net.minecraft.core.registries.Registries.DAMAGE_TYPE)
                    .getOrThrow(AllinzeroDamageTypes.SOUL_EROSION);
            float damage = 2.0F * (amplifier + 1);
            entity.hurt(new DamageSource(damageType), damage);
        }
        return true;
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        return duration % 20 == 0;
    }
}
