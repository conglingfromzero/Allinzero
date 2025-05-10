package net.fromzero.allinzero.block;

import net.fromzero.allinzero.Allinzero;
import net.fromzero.allinzero.item.AllinzeroItems;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DropExperienceBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Function;


public class AllinzeroBlocks {

	public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(Allinzero.MODID);

	public static final DeferredBlock<Block> SOUL_GRASS = registerBlock("soul_grass",
			registryName -> new Block(BlockBehaviour.Properties.of().setId(ResourceKey.create(Registries.BLOCK, registryName))
					.strength(0.5f, 0.5f).lightLevel(state -> 0).sound(SoundType.GRASS)));

	public static final DeferredBlock<Block> SOUL_STONE_BLOCK = registerBlock("soul_stone",
			registryName -> new Block(BlockBehaviour.Properties.of().setId(ResourceKey.create(Registries.BLOCK, registryName))
					.strength(1.5f,6.0f).lightLevel(state -> 0).sound(SoundType.STONE)));

	public static final DeferredBlock<Block> SOUL_STONEOCK = registerBlock("soul_stoneock",
			registryName -> new Block(BlockBehaviour.Properties.of().setId(ResourceKey.create(Registries.BLOCK, registryName))
					.strength(-1.0f,3600000.0f).lightLevel(state -> 0).sound(SoundType.STONE)));

	public static final DeferredBlock<Block> GHOST_ORE = registerBlock("ghost_ore",
			registryName -> new DropExperienceBlock(UniformInt.of(3, 8),
					BlockBehaviour.Properties.of().setId(ResourceKey.create(Registries.BLOCK, registryName))
							.mapColor(MapColor.STONE).requiredFeatures()
							.strength(1.6f,6.0f).lightLevel(state -> 0).sound(SoundType.STONE)));

	public static final DeferredBlock<Block> DEEPSLATE_GHOST_ORE = registerBlock("deepslate_ghost_ore",
			registryName -> new DropExperienceBlock(UniformInt.of(3, 8),
					BlockBehaviour.Properties.of().setId(ResourceKey.create(Registries.BLOCK, registryName))
							.mapColor(MapColor.STONE).requiredFeatures()
							.strength(1.6f,6.0f).lightLevel(state -> 0).sound(SoundType.STONE)));

	public static final DeferredBlock<Block> DEEPSLATE_SOUL_ORE = registerBlock("deepslate_soul_ore",
			registryName -> new DropExperienceBlock(UniformInt.of(4, 8),
					BlockBehaviour.Properties.of().setId(ResourceKey.create(Registries.BLOCK, registryName))
							.mapColor(MapColor.STONE).requiredFeatures()
							.strength(1.6f,1.6f).lightLevel(state -> 0).sound(SoundType.STONE)));

	public static final DeferredBlock<Block> SOUL_ORE = registerBlock("soul_ore",
			registryName -> new DropExperienceBlock(UniformInt.of(4, 8),
					BlockBehaviour.Properties.of().setId(ResourceKey.create(Registries.BLOCK, registryName))
					.mapColor(MapColor.STONE).requiredFeatures()
							.strength(1.6f,6.0f).lightLevel(state -> 0).sound(SoundType.STONE)));


	public static <T extends Block> DeferredBlock<T> registerBlock(String name, Function<ResourceLocation, T> block) {
		DeferredBlock<T> toReturn = BLOCKS.register(name, block);
		registerBlockItem(name, toReturn);
		return toReturn;
	}

	private static <T extends Block> void registerBlockItem(String name, DeferredBlock<T> block) {
		AllinzeroItems.ITEMS.register(name, id -> new BlockItem(block.get(), new Item.Properties().setId(ResourceKey.create(Registries.ITEM, id))));
	}

	public static void register(IEventBus eventBus) {
		BLOCKS.register(eventBus);
	}
}
