package net.fromzero.allinzero.effects;


import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;

public class SoulErosionEffect extends AllinzeroEffects {

    public SoulErosionEffect(MobEffectCategory p_295600_, int p_294141_) {
        super(p_295600_, p_294141_);
    }

    public boolean applyEffectTick(ServerLevel p_376400_, LivingEntity p_296279_, int p_294798_) {
        p_296279_.hurtServer(p_376400_, p_296279_.damageSources().wither(), 5.0F);
        return true;
    }

    public boolean shouldApplyEffectTickThisTick(int p_295629_, int p_295734_) {
        int i = 40 >> p_295734_;
        return i > 0 ? p_295629_ % i == 0 : true;
    }
}

