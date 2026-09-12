package net.congling.allinzero.effect;

import net.congling.allinzero.Allinzero;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.effect.MobEffect;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class AllinzeroEffects {
    public static final DeferredRegister<MobEffect> MOB_EFFECTS =
            DeferredRegister.create(Registries.MOB_EFFECT, Allinzero.MODID);

    public static final DeferredHolder<MobEffect, SoulErosionEffect> SOUL_EROSION =
            MOB_EFFECTS.register("soul_erosion", SoulErosionEffect::new);

    public static void register(IEventBus eventBus) {
        MOB_EFFECTS.register(eventBus);
    }
}
