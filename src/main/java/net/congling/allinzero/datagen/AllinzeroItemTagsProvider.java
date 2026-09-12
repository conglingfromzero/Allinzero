package net.congling.allinzero.datagen;

import net.congling.allinzero.Allinzero;
import net.congling.allinzero.blocks.AllinzeroBlocks;
import net.congling.allinzero.items.AllinzeroItems;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class AllinzeroItemTagsProvider extends ItemTagsProvider {

    public AllinzeroItemTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, CompletableFuture<TagLookup<Block>> blockTags, @Nullable ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, blockTags, Allinzero.MODID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        tag(ItemTags.SWORDS)
                .add(AllinzeroItems.GHOST_SWORD.get())
                .add(AllinzeroItems.SOUL_SWORD.get());

        tag(ItemTags.PICKAXES)
                .add(AllinzeroItems.GHOST_PICKAXE.get())
                .add(AllinzeroItems.SOUL_PICKAXE.get());

        tag(ItemTags.SHOVELS)
                .add(AllinzeroItems.GHOST_SHOVEL.get())
                .add(AllinzeroItems.SOUL_SHOVEL.get());

        tag(ItemTags.AXES)
                .add(AllinzeroItems.GHOST_AXE.get())
                .add(AllinzeroItems.SOUL_AXE.get());

        tag(ItemTags.HOES)
                .add(AllinzeroItems.GHOST_HOE.get())
                .add(AllinzeroItems.SOUL_HOE.get());

        this.tag(ItemTags.LOGS_THAT_BURN)
                .add(AllinzeroBlocks.GHOST_WOOD.get().asItem())
                .add(AllinzeroBlocks.GHOST_WOOD_PLANKS.get().asItem())
                .add(AllinzeroBlocks.STRIPPED_GHOST_WOOD.get().asItem())
                .add(AllinzeroBlocks.SOUL_WOOD.get().asItem())
                .add(AllinzeroBlocks.STRIPPED_SOUL_WOOD.get().asItem())
                .add(AllinzeroBlocks.SOUL_WOOD_PLANKS.get().asItem());

        this.tag(ItemTags.PLANKS)
                .add(AllinzeroBlocks.GHOST_WOOD_PLANKS.asItem())
                .add(AllinzeroBlocks.SOUL_WOOD_PLANKS.asItem());

    }
}
