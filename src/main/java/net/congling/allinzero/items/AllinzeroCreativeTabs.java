package net.congling.allinzero.items;

import net.congling.allinzero.Allinzero;
import net.congling.allinzero.blocks.AllinzeroBlocks;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class AllinzeroCreativeTabs {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TAB = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, Allinzero.MODID);

    public static final Supplier<CreativeModeTab> ALLINZERO_ITEMS_TAB = CREATIVE_MODE_TAB.register("allinzero_items_tab",
            () -> CreativeModeTab.builder().icon(() -> new ItemStack((ItemLike)AllinzeroItems.GHOSTSTONE.get()))
                    .title(Component.translatable("creativetab.allinzero.allinzero_items")).displayItems((itemDisplayParameters, output) -> {
        output.accept(AllinzeroItems.GHOSTSTONE);
        output.accept(AllinzeroItems.SOULSTONE);
        output.accept(AllinzeroItems.GHOST_STONE_BLOCK);
        output.accept(AllinzeroItems.GHOST_SPAR);
        output.accept(AllinzeroItems.SOUL_SPAR);
        output.accept(AllinzeroItems.SOUL_UPGRADE_SMITHING_TEMPLATE);
    }).build());

    public static final Supplier<CreativeModeTab> ALLINZERO_BLOCKS_TAB = CREATIVE_MODE_TAB.register("allinzero_blocks_tab",
            () -> CreativeModeTab.builder().icon(() -> new ItemStack((ItemLike)AllinzeroBlocks.SOUL_GRASS_BLOCK.get()))
                    .title(Component.translatable("creativetab.allinzero.allinzero_blocks")).displayItems((itemDisplayParameters, output) -> {
        output.accept(AllinzeroBlocks.SOUL_GRASS_BLOCK);
        output.accept(AllinzeroBlocks.SOUL_STONE);
        output.accept(AllinzeroBlocks.SOUL_STONEOCK);
        output.accept(AllinzeroBlocks.SOUL_GLASS);
        output.accept(AllinzeroBlocks.SOUL_LEAVES);
        output.accept(AllinzeroBlocks.SOUL_WOOD);
        output.accept(AllinzeroBlocks.GHOST_WOOD);
        output.accept(AllinzeroBlocks.GHOST_LEAVES);
        output.accept(AllinzeroBlocks.STRIPPED_SOUL_WOOD);
        output.accept(AllinzeroBlocks.STRIPPED_GHOST_WOOD);
        output.accept(AllinzeroBlocks.SOUL_WOOD_PLANKS);
        output.accept(AllinzeroBlocks.GHOST_WOOD_PLANKS);
    }).build());

    public static final Supplier<CreativeModeTab> ALLINZERO_PLANTS_TAB = CREATIVE_MODE_TAB.register("allinzero_plants_tab",
            () -> CreativeModeTab.builder().icon(() -> new ItemStack((ItemLike)AllinzeroBlocks.SOUL_GRASS.get()))
                    .title(Component.translatable("creativetab.allinzero.allinzero_blocks")).displayItems((itemDisplayParameters, output) -> {
                        output.accept(AllinzeroBlocks.SOUL_GRASS);
                        output.accept(AllinzeroBlocks.SOUL_STONE_BRICKS);
                        output.accept(AllinzeroBlocks.GHOST_CANES);
                        output.accept(AllinzeroBlocks.GHOST_CANES_TOP);
                        output.accept(AllinzeroBlocks.SOUL_TREE_SAPLING);
                        output.accept(AllinzeroBlocks.GHOST_TREE_SAPLING);
                    }).build());

    public static final Supplier<CreativeModeTab> ALLINZERO_ORES_TAB = CREATIVE_MODE_TAB.register("allinzero_ores_tab",
            () -> CreativeModeTab.builder().icon(() -> new ItemStack((ItemLike)AllinzeroBlocks.GHOST_ORE.get()))
                    .title(Component.translatable("creativetab.allinzero.allinzero_ore")).displayItems((itemDisplayParameters, output) -> {
        output.accept(AllinzeroBlocks.GHOST_ORE);
        output.accept(AllinzeroBlocks.DEEPSLATE_GHOST_ORE);
        output.accept(AllinzeroBlocks.TOULING_GHOST_ORE);
        output.accept(AllinzeroBlocks.SOUL_ORE);
        output.accept(AllinzeroBlocks.DEEPSLATE_SOUL_ORE);
        output.accept(AllinzeroBlocks.TOULING_SOUL_ORE);
    }).build());

    public static final Supplier<CreativeModeTab> ALLINZERO_TOOLS_TAB = CREATIVE_MODE_TAB.register("allinzero_tools_tab",
            () -> CreativeModeTab.builder().icon(() -> new ItemStack((ItemLike)AllinzeroItems.GHOST_PICKAXE.get()))
                    .title(Component.translatable("creativetab.allinzero.allinzero_tools")).displayItems((itemDisplayParameters, output) -> {
        output.accept(AllinzeroItems.GHOST_AXE);
        output.accept(AllinzeroItems.GHOST_PICKAXE);
        output.accept(AllinzeroItems.GHOST_HOE);
        output.accept(AllinzeroItems.GHOST_SHOVEL);
        output.accept(AllinzeroItems.SOUL_AXE);
        output.accept(AllinzeroItems.SOUL_PICKAXE);
        output.accept(AllinzeroItems.SOUL_HOE);
        output.accept(AllinzeroItems.SOUL_SHOVEL);
        output.accept(AllinzeroItems.SOUL_FLINT_AND_STEEL);
    }).build());

    public static final Supplier<CreativeModeTab> ALLINZERO_WEAPONS_TAB = CREATIVE_MODE_TAB.register("allinzero_weapons_tab",
            () -> CreativeModeTab.builder().icon(() -> new ItemStack((ItemLike)AllinzeroItems.GHOST_SWORD.get()))
                    .title(Component.translatable("creativetab.allinzero.allinzero_weapons")).displayItems((itemDisplayParameters, output) -> {
        output.accept(AllinzeroItems.GHOST_SWORD);
        output.accept(AllinzeroItems.GHOST_BOW);
        output.accept(AllinzeroItems.GHOST_AXE);
        output.accept(AllinzeroItems.SOUL_SWORD);
        output.accept(AllinzeroItems.SOUL_BOW);
        output.accept(AllinzeroItems.SOUL_AXE);
    }).build());

    public static final Supplier<CreativeModeTab> ALLINZERO_ARMORS_TAB = CREATIVE_MODE_TAB.register("allinzero_armors_tab",
            () -> CreativeModeTab.builder().icon(() -> new ItemStack((ItemLike)AllinzeroItems.GHOST_HELMET.get()))
                    .title(Component.translatable("creativetab.allinzero.allinzero_armors")).displayItems((itemDisplayParameters, output) -> {
        output.accept(AllinzeroItems.GHOST_HELMET);
        output.accept(AllinzeroItems.GHOST_CHESTPLATE);
        output.accept(AllinzeroItems.GHOST_LEGGINGS);
        output.accept(AllinzeroItems.GHOST_BOOTS);
        output.accept(AllinzeroItems.SOUL_HELMET);
        output.accept(AllinzeroItems.SOUL_CHESTPLATE);
        output.accept(AllinzeroItems.SOUL_LEGGINGS);
        output.accept(AllinzeroItems.SOUL_BOOTS);
    }).build());

    public static final Supplier<CreativeModeTab> ALLINZERO_FOODS_TAB = CREATIVE_MODE_TAB.register("allinzero_foods_tab",
            () -> CreativeModeTab.builder().icon(() -> new ItemStack((ItemLike)AllinzeroItems.COOKED_SOUL_BEEF.get()))
                    .title(Component.translatable("creativetab.allinzero.allinzero_foods")).displayItems((itemDisplayParameters, output) -> {
        output.accept(AllinzeroItems.SOUL_BEEF);
        output.accept(AllinzeroItems.COOKED_SOUL_BEEF);
        output.accept(AllinzeroItems.SOUL_MILK);
    }).build());

    public static void register(IEventBus eventBus) {
        CREATIVE_MODE_TAB.register(eventBus);
    }

}
