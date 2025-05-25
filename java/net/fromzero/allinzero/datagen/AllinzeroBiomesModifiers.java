package net.fromzero.allinzero.datagen;

import net.fromzero.allinzero.worldgen.featre.AllinzeroPlacedFeatures;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BiomeTags;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.neoforged.neoforge.common.world.BiomeModifier;
import net.neoforged.neoforge.common.world.BiomeModifiers;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

public class AllinzeroBiomesModifiers {
    protected static ResourceKey<BiomeModifier> ADD_SOUL_ORE = createKey("add_soul_ore");
    protected static ResourceKey<BiomeModifier> ADD_GHOST_ORE = createKey("add_ghost_ore");

    public static void bootstrap(BootstrapContext<BiomeModifier> context) {
        HolderGetter<PlacedFeature> placedFeatures = context.lookup(Registries.PLACED_FEATURE);
        HolderGetter<Biome> biomes = context.lookup(Registries.BIOME);
        context.register(ADD_SOUL_ORE, new BiomeModifiers.AddFeaturesBiomeModifier(biomes.getOrThrow(BiomeTags.IS_OVERWORLD), HolderSet.direct(new Holder[]{placedFeatures.getOrThrow(AllinzeroPlacedFeatures.SOUL_PLACED_KEY)}), GenerationStep.Decoration.UNDERGROUND_ORES));
        context.register(ADD_GHOST_ORE, new BiomeModifiers.AddFeaturesBiomeModifier(biomes.getOrThrow(BiomeTags.IS_OVERWORLD), HolderSet.direct(new Holder[]{placedFeatures.getOrThrow(AllinzeroPlacedFeatures.GHOST_PLACED_KEY)}), GenerationStep.Decoration.UNDERGROUND_ORES));
    }

    private static ResourceKey<BiomeModifier> createKey(String name) {
        return ResourceKey.create(NeoForgeRegistries.Keys.BIOME_MODIFIERS, ResourceLocation.fromNamespaceAndPath("allinzero_ore", name));
    }
}
