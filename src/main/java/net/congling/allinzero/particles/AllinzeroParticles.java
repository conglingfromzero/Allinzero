package net.congling.allinzero.particles;

import net.congling.allinzero.Allinzero;
import net.minecraft.core.particles.SimpleParticleType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.minecraft.core.registries.Registries;

public class AllinzeroParticles {
    public static final DeferredRegister<net.minecraft.core.particles.ParticleType<?>> PARTICLE_TYPES =
            DeferredRegister.create(Registries.PARTICLE_TYPE, Allinzero.MODID);

    public static final DeferredHolder<net.minecraft.core.particles.ParticleType<?>, SimpleParticleType> TOULING_PORTAL =
            PARTICLE_TYPES.register("touling_portal", () -> new SimpleParticleType(false));

    public static final DeferredHolder<net.minecraft.core.particles.ParticleType<?>, SimpleParticleType> TOULING_REVERSE_PORTAL =
            PARTICLE_TYPES.register("touling_reverse_portal", () -> new SimpleParticleType(false));

    public static void register(IEventBus eventBus) {
        PARTICLE_TYPES.register(eventBus);
    }
}
