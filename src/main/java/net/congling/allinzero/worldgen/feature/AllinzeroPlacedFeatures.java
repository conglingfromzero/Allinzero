package net.congling.allinzero.worldgen.feature;

import net.congling.allinzero.Allinzero;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.placement.*;

import java.util.List;

public class AllinzeroPlacedFeatures {
    public static final ResourceKey<PlacedFeature> GHOST_PLACED_KEY = createKey("ghost_placed");
    public static final ResourceKey<PlacedFeature> SOUL_PLACED_KEY = createKey("soul_placed");
    public static final ResourceKey<PlacedFeature> TOULING_GHOST_PLACED_KEY = createKey("touling_ghost_placed");
    public static final ResourceKey<PlacedFeature> TOULING_SOUL_PLACED_KEY = createKey("touling_soul_placed");

    public static void bootstrap(BootstrapContext<PlacedFeature> context) {
        HolderGetter<ConfiguredFeature<?, ?>> configuredFeatures = context.lookup(Registries.CONFIGURED_FEATURE);
        Holder<ConfiguredFeature<?, ?>> overworldghostore = configuredFeatures.getOrThrow(AllinzeroConfiguredFeatures.OVERWORLD_GHOST_ORE_KEY);
        Holder<ConfiguredFeature<?, ?>> overworldsoulore = configuredFeatures.getOrThrow(AllinzeroConfiguredFeatures.OVERWORLD_SOUL_ORE_KEY);
        Holder<ConfiguredFeature<?, ?>> toulingghostore = configuredFeatures.getOrThrow(AllinzeroConfiguredFeatures.T0ULING__GHOST_ORE_KEY);
        Holder<ConfiguredFeature<?, ?>> toulingsoulore = configuredFeatures.getOrThrow(AllinzeroConfiguredFeatures.T0ULING_SOUL_ORE_KEY);
        register(context, GHOST_PLACED_KEY, overworldghostore, commonOrePlacement(7, HeightRangePlacement.uniform(VerticalAnchor.aboveBottom(-30), VerticalAnchor.absolute(40))));
        register(context, SOUL_PLACED_KEY, overworldsoulore, commonOrePlacement(3, HeightRangePlacement.uniform(VerticalAnchor.aboveBottom(-64), VerticalAnchor.absolute(0))));
        register(context, TOULING_GHOST_PLACED_KEY, toulingghostore, commonOrePlacement(10, HeightRangePlacement.uniform(VerticalAnchor.aboveBottom(-60), VerticalAnchor.absolute(50))));
        register(context, TOULING_SOUL_PLACED_KEY, toulingsoulore, commonOrePlacement(5, HeightRangePlacement.uniform(VerticalAnchor.aboveBottom(-64), VerticalAnchor.absolute(0))));
    }

    public static List<PlacementModifier> orePlacement(PlacementModifier placementModifier, PlacementModifier p_195348_) {
        return List.of(placementModifier, InSquarePlacement.spread(), p_195348_, BiomeFilter.biome());
    }

    public static List<PlacementModifier> commonOrePlacement(int p_195344_, PlacementModifier p_195345_) {
        return orePlacement(CountPlacement.of(p_195344_), p_195345_);
    }

    private static ResourceKey<PlacedFeature> createKey(String name) {
        return ResourceKey.create(Registries.PLACED_FEATURE, ResourceLocation.fromNamespaceAndPath(Allinzero.MODID, name));
    }

    private static void register(BootstrapContext<PlacedFeature> context, ResourceKey<PlacedFeature> key, Holder<ConfiguredFeature<?, ?>> feature, List<PlacementModifier> placementModifiers) {
        context.register(key, new PlacedFeature(feature, placementModifiers));
    }
}
