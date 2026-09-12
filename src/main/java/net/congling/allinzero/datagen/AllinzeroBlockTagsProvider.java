package net.congling.allinzero.datagen;

import net.congling.allinzero.Allinzero;
import net.congling.allinzero.blocks.AllinzeroBlocks;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.BlockTags;
import net.neoforged.neoforge.common.data.BlockTagsProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class AllinzeroBlockTagsProvider extends BlockTagsProvider {
    public AllinzeroBlockTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, @Nullable ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, Allinzero.MODID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        tag(BlockTags.MINEABLE_WITH_PICKAXE)
                .add(AllinzeroBlocks.SOUL_ORE.get())
                .add(AllinzeroBlocks.DEEPSLATE_SOUL_ORE.get())
                .add(AllinzeroBlocks.TOULING_SOUL_ORE.get())
                .add(AllinzeroBlocks.GHOST_ORE.get())
                .add(AllinzeroBlocks.DEEPSLATE_GHOST_ORE.get())
                .add(AllinzeroBlocks.TOULING_GHOST_ORE.get())
                .add(AllinzeroBlocks.SOUL_STONE.get());

        tag(BlockTags.MINEABLE_WITH_AXE)
                .add(AllinzeroBlocks.SOUL_WOOD.get())
                .add(AllinzeroBlocks.GHOST_WOOD.get());

        tag(BlockTags.MINEABLE_WITH_SHOVEL)
                .add(AllinzeroBlocks.SOUL_GRASS_BLOCK.get());

        this.tag(BlockTags.LOGS_THAT_BURN)
                .add(AllinzeroBlocks.GHOST_WOOD.get())
                .add(AllinzeroBlocks.GHOST_WOOD_PLANKS.get())
                .add(AllinzeroBlocks.STRIPPED_GHOST_WOOD.get())
                .add(AllinzeroBlocks.SOUL_WOOD.get())
                .add(AllinzeroBlocks.STRIPPED_SOUL_WOOD.get())
                .add(AllinzeroBlocks.SOUL_WOOD_PLANKS.get());

    }
}
