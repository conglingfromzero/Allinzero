package net.fromzero.allinzero.datagen;

import net.fromzero.allinzero.block.AllinzeroBlocks;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class AllinzeroBlockLootTables extends BlockLootSubProvider {
    public AllinzeroBlockLootTables(HolderLookup.Provider registries) {
        super(Set.of(), FeatureFlags.REGISTRY.allFlags(), registries);
    }

    protected void generate() {
        this.add((Block) AllinzeroBlocks.SOUL_ORE.get(), this.createSilkTouchOnlyTable((ItemLike)AllinzeroBlocks.SOUL_ORE.get()));
        this.add((Block) AllinzeroBlocks.DEEPSLATE_SOUL_ORE.get(), this.createSilkTouchOnlyTable((ItemLike)AllinzeroBlocks.DEEPSLATE_SOUL_ORE.get()));
        this.add((Block) AllinzeroBlocks.GHOST_ORE.get(), this.createSilkTouchOnlyTable((ItemLike)AllinzeroBlocks.GHOST_ORE.get()));
        this.add((Block) AllinzeroBlocks.DEEPSLATE_GHOST_ORE.get(), this.createSilkTouchOnlyTable((ItemLike)AllinzeroBlocks.DEEPSLATE_GHOST_ORE.get()));
    }


    protected @NotNull Iterable<Block> getKnownBlocks() {
        return (Iterable)BuiltInRegistries.BLOCK.stream().filter((block) -> Optional.of(BuiltInRegistries.BLOCK.getKey(block))
                .filter((key) -> key.getNamespace().equals("allinzero_ore")).isPresent()).collect(Collectors.toSet());
    }
}

