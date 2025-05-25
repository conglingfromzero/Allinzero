package net.fromzero.allinzero.worldgen.dimension;

import net.minecraft.client.renderer.DimensionSpecialEffects;
import net.minecraft.client.renderer.DimensionSpecialEffects.SkyType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.EventBusSubscriber.Bus;
import net.neoforged.neoforge.client.event.RegisterDimensionSpecialEffectsEvent;

public class ToulingDimension {
    @EventBusSubscriber(
            bus = Bus.MOD,
            value = {Dist.CLIENT}
    )
    public static class ToulingSpecialEffectsHandler {
        @SubscribeEvent
        public static void registerDimensionSpecialEffects(RegisterDimensionSpecialEffectsEvent event) {
            DimensionSpecialEffects customEffect = new DimensionSpecialEffects(192.0F, true, SkyType.OVERWORLD, true, true) {
                public Vec3 getBrightnessDependentFogColor(Vec3 color, float sunHeight) {
                    return color;
                }

                public boolean isFoggyAt(int x, int y) {
                    return false;
                }
            };
            event.register(ResourceLocation.parse("allinzero:touling"), customEffect);
        }
    }
}