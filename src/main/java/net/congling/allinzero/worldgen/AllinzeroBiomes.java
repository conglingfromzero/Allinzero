package net.congling.allinzero.worldgen;

import net.congling.allinzero.Allinzero;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.Biome;

public class AllinzeroBiomes {
    public static final ResourceKey<Biome> TOULING_PLAINS = makeBiomeKey("touling_plains");
    public static final ResourceKey<Biome> TOULING_CAVES = makeBiomeKey("touling_caves");

    private static ResourceKey<Biome> makeBiomeKey(String name) {
        return ResourceKey.create(Registries.BIOME, ResourceLocation.fromNamespaceAndPath(Allinzero.MODID, name));
    }
}
