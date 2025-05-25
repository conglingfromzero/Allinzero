package net.fromzero.allinzero.worldgen.featre;
import java.util.List;
import java.util.Set;

import net.fromzero.allinzero.Allinzero;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.data.worldgen.placement.OrePlacements;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.placement.BiomeFilter;
import net.minecraft.world.level.levelgen.placement.CountPlacement;
import net.minecraft.world.level.levelgen.placement.HeightRangePlacement;
import net.minecraft.world.level.levelgen.placement.InSquarePlacement;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.levelgen.placement.PlacementModifier;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.world.BiomeModifier;
import net.neoforged.neoforge.common.world.BiomeModifiers;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import static net.fromzero.allinzero.Config.BUILDER;


public class AllinzeroPlacedFeatures {
    public static final ResourceKey<PlacedFeature> SOUL_PLACED_KEY = createKey("soul_placed");
    public static final ResourceKey<PlacedFeature> GHOST_PLACED_KEY = createKey("ghost_placed");

    public static void bootstrap(BootstrapContext<PlacedFeature> context) {
        HolderGetter<ConfiguredFeature<?, ?>> configuredFeatures = context.lookup(Registries.CONFIGURED_FEATURE);
        Holder<ConfiguredFeature<?, ?>> overworldSoulOre = configuredFeatures.getOrThrow(AllinzeroConfiguredFeatures.OVERWORLD_SOUL_ORE_KEY);
        Holder<ConfiguredFeature<?, ?>> overworldGhostOre = configuredFeatures.getOrThrow(AllinzeroConfiguredFeatures.OVERWORLD_GHOST_ORE_KEY);
        register(context, SOUL_PLACED_KEY, overworldSoulOre, commonOrePlacement(9, HeightRangePlacement.uniform(VerticalAnchor.aboveBottom(-80), VerticalAnchor.absolute(80))));
        register(context, GHOST_PLACED_KEY, overworldGhostOre, commonOrePlacement(9, HeightRangePlacement.uniform(VerticalAnchor.aboveBottom(-100), VerticalAnchor.absolute(125))));
    }
    public static void setSoulPlacedKey(BootstrapContext<PlacedFeature> context) {
        HolderGetter<ConfiguredFeature<?, ?>> configuredFeatures = context.lookup(Registries.CONFIGURED_FEATURE);
        Holder<ConfiguredFeature<?, ?>> overworldSoulOre = configuredFeatures.getOrThrow(AllinzeroConfiguredFeatures.TOULING_SOUL_ORE_KEY);
        Holder<ConfiguredFeature<?, ?>> overworldGhostOre = configuredFeatures.getOrThrow(AllinzeroConfiguredFeatures.TOULING_GHOST_ORE_KEY);
        register(context, SOUL_PLACED_KEY, overworldSoulOre, commonOrePlacement(9, HeightRangePlacement.uniform(VerticalAnchor.aboveBottom(-80), VerticalAnchor.absolute(80))));
        register(context, GHOST_PLACED_KEY, overworldGhostOre, commonOrePlacement(9, HeightRangePlacement.uniform(VerticalAnchor.aboveBottom(-100), VerticalAnchor.absolute(125))));
    }
    public static final ResourceKey<BiomeModifier> REMOVE_FEATURES_EXAMPLE = ResourceKey.create(
            NeoForgeRegistries.Keys.BIOME_MODIFIERS,
            ResourceLocation.fromNamespaceAndPath(Allinzero.MODID, "remove_touling")
    );


    public static List<PlacementModifier> orePlacement(PlacementModifier p_195347_, PlacementModifier p_195348_) {
        return List.of(p_195347_, InSquarePlacement.spread(), p_195348_, BiomeFilter.biome());
    }

    public static List<PlacementModifier> commonOrePlacement(int p_195344_, PlacementModifier p_195345_) {
        return orePlacement(CountPlacement.of(p_195344_), p_195345_);
    }

    private static ResourceKey<PlacedFeature> createKey(String name) {
        return ResourceKey.create(Registries.PLACED_FEATURE, ResourceLocation.fromNamespaceAndPath("allinzero_ore", name));
    }

    private static void register(BootstrapContext<PlacedFeature> context, ResourceKey<PlacedFeature> key,
                                 Holder<ConfiguredFeature<?, ?>> feature, List<PlacementModifier> placementModifiers) {
        context.register(key, new PlacedFeature(feature, placementModifiers));
    }
}