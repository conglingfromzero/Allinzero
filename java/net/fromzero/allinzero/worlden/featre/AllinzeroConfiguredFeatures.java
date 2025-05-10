package net.fromzero.allinzero.worlden.featre;

import net.fromzero.allinzero.block.AllinzeroBlocks;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.biome.BiomeSpecialEffects;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.SeagrassFeature;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.OreConfiguration;
import net.minecraft.world.level.levelgen.structure.templatesystem.RuleTest;
import net.minecraft.world.level.levelgen.structure.templatesystem.TagMatchTest;

import java.util.List;

import static net.minecraft.data.worldgen.features.FeatureUtils.register;

public class AllinzeroConfiguredFeatures {
    public static final ResourceKey<ConfiguredFeature<?, ?>> OVERWORLD_SOUL_ORE_KEY = cteateKey("overworld_soul_ore_key");
    public static final ResourceKey<ConfiguredFeature<?, ?>> OVERWORLD_GHOST_ORE_KEY = cteateKey("overworld_ghost_ore_key");


    public static void bootstrap(BootstrapContext<ConfiguredFeature<?, ?>> context) {
        RuleTest stoneReplacable = new TagMatchTest(BlockTags.STONE_ORE_REPLACEABLES);
        RuleTest deepSlateReplacable = new TagMatchTest(BlockTags.DEEPSLATE_ORE_REPLACEABLES);
        List<OreConfiguration.TargetBlockState> overworldSoulOre = List.of(OreConfiguration.target(stoneReplacable,
                ((Block) AllinzeroBlocks.SOUL_ORE.get()).defaultBlockState()), OreConfiguration.target(deepSlateReplacable,
                ((Block) AllinzeroBlocks.DEEPSLATE_SOUL_ORE.get()).defaultBlockState()));
        register(context, OVERWORLD_SOUL_ORE_KEY, Feature.ORE, new OreConfiguration(overworldSoulOre, 7));
    }

    public static void setOverworldGhostOreKey(BootstrapContext<ConfiguredFeature<?, ?>> event) {
        RuleTest stoneReplacable = new TagMatchTest(BlockTags.STONE_ORE_REPLACEABLES);
        RuleTest deepSlateReplacable = new TagMatchTest(BlockTags.DEEPSLATE_ORE_REPLACEABLES);
        List<OreConfiguration.TargetBlockState> overworldGhostOre = List.of(OreConfiguration.target(stoneReplacable,
                ((Block) AllinzeroBlocks.GHOST_ORE.get()).defaultBlockState()), OreConfiguration.target(deepSlateReplacable,
                ((Block) AllinzeroBlocks.DEEPSLATE_GHOST_ORE.get()).defaultBlockState()));
        register(event, OVERWORLD_GHOST_ORE_KEY, Feature.ORE, new OreConfiguration(overworldGhostOre, 7));
    }


    private static ResourceKey<ConfiguredFeature<?, ?>> cteateKey(String name) {
        return ResourceKey.create(Registries.CONFIGURED_FEATURE, ResourceLocation.fromNamespaceAndPath("allinzero_ore", name));
    }

    private static <FC extends FeatureConfiguration, F extends Feature<FC>> void
    register(BootstrapContext<ConfiguredFeature<?, ?>> context, ResourceKey<ConfiguredFeature<?, ?>> key, F feature, FC config) {
        context.register(key, new ConfiguredFeature(feature, config));
    }
}
