package net.congling.allinzero.worldgen;

import net.congling.allinzero.Allinzero;
import net.minecraft.client.renderer.DimensionSpecialEffects;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterDimensionSpecialEffectsEvent;

public class AllinzeroDimensions {
    public static final ResourceKey<Level> TOULING_LEVEL = ResourceKey.create(
            Registries.DIMENSION, ResourceLocation.fromNamespaceAndPath(Allinzero.MODID, "touling"));

    @EventBusSubscriber(
            modid = Allinzero.MODID,
            value = {Dist.CLIENT}
    )
    public static class ToulingSpecialEffectsHandler {
        @SubscribeEvent
        public static void registerDimensionSpecialEffects(RegisterDimensionSpecialEffectsEvent event) {
            DimensionSpecialEffects customEffect = new DimensionSpecialEffects(Float.NaN, true, DimensionSpecialEffects.SkyType.END, false, false) {
                public Vec3 getBrightnessDependentFogColor(Vec3 color, float sunHeight) {
                    return new Vec3(0.8, 0.8, (double)1.0F);
                }

                public boolean isFoggyAt(int x, int y) {
                    return true;
                }
            };
            event.register(ResourceLocation.parse("allinzero:touling"), customEffect);
        }
    }
}
