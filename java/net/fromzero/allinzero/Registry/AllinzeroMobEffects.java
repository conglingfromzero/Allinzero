package net.fromzero.allinzero.Registry;

import net.fromzero.allinzero.effects.SoulErosionEffect;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;



public class AllinzeroMobEffects {
    public static final DeferredRegister<MobEffect> EFFECTS;
    public static final DeferredHolder<MobEffect, MobEffect> SOUL_EROSION;

    static {
        EFFECTS = DeferredRegister.create(BuiltInRegistries.MOB_EFFECT, "allinzero");
        SOUL_EROSION = EFFECTS.register("soul_erosion", () -> new SoulErosionEffect(MobEffectCategory.HARMFUL, 7561558));

    }
}