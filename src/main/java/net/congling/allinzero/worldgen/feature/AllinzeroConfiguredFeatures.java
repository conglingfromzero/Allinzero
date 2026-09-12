package net.congling.allinzero.worldgen.feature;

import net.congling.allinzero.Allinzero;
import net.congling.allinzero.blocks.AllinzeroBlocks;
import net.congling.allinzero.util.AllinzeroTags;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.valueproviders.ConstantInt;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.OreConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration;
import net.minecraft.world.level.levelgen.feature.featuresize.TwoLayersFeatureSize;
import net.minecraft.world.level.levelgen.feature.foliageplacers.BlobFoliagePlacer;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.minecraft.world.level.levelgen.feature.trunkplacers.ForkingTrunkPlacer;
import net.minecraft.world.level.levelgen.structure.templatesystem.RuleTest;
import net.minecraft.world.level.levelgen.structure.templatesystem.TagMatchTest;

import java.util.List;

public class AllinzeroConfiguredFeatures {
    public static final ResourceKey<ConfiguredFeature<?, ?>> OVERWORLD_GHOST_ORE_KEY = createKey("overworld_ghost_ore");
    public static final ResourceKey<ConfiguredFeature<?, ?>> OVERWORLD_SOUL_ORE_KEY = createKey("overworld_soul_ore");
    public static final ResourceKey<ConfiguredFeature<?, ?>> T0ULING__GHOST_ORE_KEY = createKey("touling_ghost_ore");
    public static final ResourceKey<ConfiguredFeature<?, ?>> T0ULING_SOUL_ORE_KEY = createKey("touling_soul_ore");

    public static final ResourceKey<ConfiguredFeature<?, ?>> GHOST_TREE_KEY = createKey("ghost_tree");
    public static final ResourceKey<ConfiguredFeature<?, ?>> SOUL_TREE_KEY = createKey("soul_tree");

    public static void bootstrap(BootstrapContext<ConfiguredFeature<?, ?>> context) {
        RuleTest stoneReplacable = new TagMatchTest(BlockTags.STONE_ORE_REPLACEABLES);
        RuleTest deepSlateReplacable = new TagMatchTest(BlockTags.DEEPSLATE_ORE_REPLACEABLES);
        RuleTest toulingReplacable = new TagMatchTest(AllinzeroTags.Blocks.TOULING_ORE_REPLACEABLES);
        List<OreConfiguration.TargetBlockState> overworldghostore = List.of(OreConfiguration.target(stoneReplacable,
                ((Block) AllinzeroBlocks.GHOST_ORE.get()).defaultBlockState()), OreConfiguration.target(deepSlateReplacable,
                ((Block)AllinzeroBlocks.DEEPSLATE_GHOST_ORE.get()).defaultBlockState()));
        register(context, OVERWORLD_GHOST_ORE_KEY, Feature.ORE, new OreConfiguration(overworldghostore, 7));
        List<OreConfiguration.TargetBlockState> overworldsoulore = List.of(OreConfiguration.target(stoneReplacable,
                ((Block)AllinzeroBlocks.SOUL_ORE.get()).defaultBlockState()), OreConfiguration.target(deepSlateReplacable,
                ((Block)AllinzeroBlocks.DEEPSLATE_SOUL_ORE.get()).defaultBlockState()));
        register(context, OVERWORLD_SOUL_ORE_KEY, Feature.ORE, new OreConfiguration(overworldsoulore, 3));
        List<OreConfiguration.TargetBlockState> toulingghostore = List.of(OreConfiguration.target(toulingReplacable,
                ((Block)AllinzeroBlocks.TOULING_GHOST_ORE.get()).defaultBlockState()));
        register(context, T0ULING__GHOST_ORE_KEY, Feature.ORE, new OreConfiguration(toulingghostore, 10));
        List<OreConfiguration.TargetBlockState> toulingsoulore = List.of(OreConfiguration.target(toulingReplacable,
                ((Block)AllinzeroBlocks.TOULING_SOUL_ORE.get()).defaultBlockState()));
        register(context, T0ULING_SOUL_ORE_KEY, Feature.ORE, new OreConfiguration(toulingsoulore, 5));

        register(context, GHOST_TREE_KEY, Feature.TREE, new TreeConfiguration.TreeConfigurationBuilder(
                BlockStateProvider.simple(AllinzeroBlocks.GHOST_WOOD.get()),
                new ForkingTrunkPlacer(2, 5, 5),
                BlockStateProvider.simple(AllinzeroBlocks.GHOST_LEAVES.get()),
                new BlobFoliagePlacer(ConstantInt.of(2), ConstantInt.of(3), 3),
                new TwoLayersFeatureSize(1, 0, 2)).build());
        register(context, SOUL_TREE_KEY, Feature.TREE, new TreeConfiguration.TreeConfigurationBuilder(
                BlockStateProvider.simple(AllinzeroBlocks.SOUL_WOOD.get()),
                new ForkingTrunkPlacer(2, 5, 5),
                BlockStateProvider.simple(AllinzeroBlocks.SOUL_LEAVES.get()),
                new BlobFoliagePlacer(ConstantInt.of(2), ConstantInt.of(3), 3),
                new TwoLayersFeatureSize(1, 0, 2)).build());
    }

    private static ResourceKey<ConfiguredFeature<?, ?>> createKey(String name) {
        return ResourceKey.create(Registries.CONFIGURED_FEATURE, ResourceLocation.fromNamespaceAndPath(Allinzero.MODID, name));
    }

    private static <FC extends FeatureConfiguration, F extends Feature<FC>> void register(BootstrapContext<ConfiguredFeature<?, ?>> context, ResourceKey<ConfiguredFeature<?, ?>> key, F feature, FC config) {
        context.register(key, new ConfiguredFeature(feature, config));
    }
}
