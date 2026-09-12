package net.congling.allinzero.util;

import net.congling.allinzero.Allinzero;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;

public class AllinzeroTags {

    public static class Blocks {
        public static final TagKey<Block> INCORRECT_FOR_GHOST_TOOL = createTag("incorrect_for_ghost_tool");

        public static final TagKey<Block> INCORRECT_FOR_SOUL_TOOL = createTag("incorrect_for_soul_tool");

            public static final TagKey<Block> TOULING_ORE_REPLACEABLES = createTag("touling_ore_replaceables");

        private static TagKey<Block> createTag(String name) {
            return BlockTags.create(ResourceLocation.fromNamespaceAndPath(Allinzero.MODID, name));
        }


        public static class Biomes {
            public static final TagKey<Biome> IS_TOULING = tag("is_touling");

            private static TagKey<Biome> tag(String name) {
                return TagKey.create(Registries.BIOME, ResourceLocation.fromNamespaceAndPath(Allinzero.MODID, name));
            }
        }

    }

}
