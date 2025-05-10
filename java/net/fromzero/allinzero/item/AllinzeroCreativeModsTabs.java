package net.fromzero.allinzero.item;

import net.fromzero.allinzero.Allinzero;
import net.fromzero.allinzero.block.AllinzeroBlocks;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class AllinzeroCreativeModsTabs {
	
	public static final DeferredRegister<CreativeModeTab> TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, Allinzero.MODID);

	public static final Supplier<CreativeModeTab> Allinzero_ITEMS_TAB = TABS.register("allinzero_items_tab",
			() -> CreativeModeTab.builder().icon(() -> new ItemStack(AllinzeroItems.SOUL_STONE.get()))
					.title(Component.translatable("creativetab.allinzero.allinzero_items"))
					.displayItems((itemDisplayParameters, output) -> {
		output.accept(AllinzeroItems.GHOST_STONE.get());
		output.accept(AllinzeroItems.SOUL_STONE.get());
		output.accept(AllinzeroItems.GHOST_STONE_BLOCK.get());
		output.accept(AllinzeroItems.FROMZERO.get());
	}).build());

	public static final Supplier<CreativeModeTab> Allinzero_BLOCKS_TAB = TABS.register("allinzero_blocks_tab",
			() -> CreativeModeTab.builder().icon(() -> new ItemStack(AllinzeroBlocks.SOUL_GRASS.get()))
					.title(Component.translatable("creativetab.allinzero.allinzero_blocks"))
					.displayItems((itemDisplayParameters, output) -> {
		output.accept(AllinzeroBlocks.SOUL_GRASS.get());
		output.accept(AllinzeroBlocks.SOUL_STONE_BLOCK.get());
		output.accept(AllinzeroBlocks.SOUL_STONEOCK.get());
	}).build());
	
	public static final Supplier<CreativeModeTab> Allinzero_TOOLS_TAB = TABS.register("allinzero_tools_tab",
			() -> CreativeModeTab.builder().icon(() -> new ItemStack(AllinzeroItems.GHOST_PICKAXE.get()))
					.title(Component.translatable("creativetab.allinzero.allinzero_tools"))
					.displayItems((itemDisplayParameters, output) -> {
		output.accept(AllinzeroItems.GHOST_AXE.get());
		output.accept(AllinzeroItems.GHOST_PICKAXE.get());
		output.accept(AllinzeroItems.GHOST_SHOVEL.get());
		output.accept(AllinzeroItems.GHOST_HOE.get());
		output.accept(AllinzeroItems.SOUL_AXE.get());
		output.accept(AllinzeroItems.SOUL_PICKAXE.get());
		output.accept(AllinzeroItems.SOUL_SHOVEL.get());
		output.accept(AllinzeroItems.SOUL_HOE.get());
	}).build());

	public static final Supplier<CreativeModeTab> Allinzero_WEAPONS_TAB = TABS.register("allinzero_weapons_tab",
			() -> CreativeModeTab.builder().icon(() -> new ItemStack(AllinzeroItems.GHOST_SWORD.get()))
					.title(Component.translatable("creativetab.allinzero.allinzero_weapons"))
					.displayItems((itemDisplayParameters, output) -> {
						output.accept(AllinzeroItems.GHOST_SWORD.get());
						output.accept(AllinzeroItems.SOUL_SWORD.get());
						output.accept(AllinzeroItems.GHOST_AXE.get());
						output.accept(AllinzeroItems.SOUL_AXE.get());
					}).build());

	public static final Supplier<CreativeModeTab> Allinzero_ORES_TAB = TABS.register("allinzero_ores_tab",
			() -> CreativeModeTab.builder().icon(() -> new ItemStack(AllinzeroBlocks.GHOST_ORE.get()))
					.title(Component.translatable("creativetab.allinzero.allinzero_ore"))
					.displayItems((itemDisplayParameters, output) -> {
						output.accept(AllinzeroBlocks.GHOST_ORE.get());
						output.accept(AllinzeroBlocks.DEEPSLATE_GHOST_ORE.get());
						output.accept(AllinzeroBlocks.SOUL_ORE.get());
						output.accept(AllinzeroBlocks.DEEPSLATE_SOUL_ORE.get());

					}).build());
	public static final Supplier<CreativeModeTab> Allinzero_ARMORS_TAB = TABS.register("allinzero_armors_tab",
			() -> CreativeModeTab.builder().icon(() -> new ItemStack(AllinzeroItems.GHOST_CHESTPLATE.get()))
					.title(Component.translatable("creativetab.allinzero.allinzero_armors"))
					.displayItems((itemDisplayParameters, output) -> {
						output.accept(AllinzeroItems.GHOST_HELMET.get());
						output.accept(AllinzeroItems.GHOST_CHESTPLATE.get());
						output.accept(AllinzeroItems.GHOST_LEGGINGS.get());
						output.accept(AllinzeroItems.GHOST_BOOTS.get());
						output.accept(AllinzeroItems.SOUL_HELMET.get());
						output.accept(AllinzeroItems.SOUL_CHESTPLATE.get());
						output.accept(AllinzeroItems.SOUL_LEGGINGS.get());
						output.accept(AllinzeroItems.SOUL_BOOTS.get());
					}).build());

	public static void register(IEventBus eventBus) {
		TABS.register(eventBus);
	}
}
