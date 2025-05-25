package net.fromzero.allinzero.block;

import java.util.function.Function;
import net.fromzero.allinzero.item.AllinzeroItems;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.flag.FeatureFlag;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DropExperienceBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

public class AllinzeroBlocks {
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks("allinzero");
    public static final DeferredBlock<Block> SOUL_GRASS = registerBlock("soul_grass", (registryName) -> new Block(Properties.of().setId(ResourceKey.create(Registries.BLOCK, registryName)).strength(0.5F, 0.5F).lightLevel((state) -> 0).sound(SoundType.GRASS)));
    public static final DeferredBlock<Block> SOUL_STONE_BLOCK = registerBlock("soul_stone", (registryName) -> new Block(Properties.of().setId(ResourceKey.create(Registries.BLOCK, registryName)).strength(1.5F, 6.0F).lightLevel((state) -> 0).sound(SoundType.STONE)));
    public static final DeferredBlock<Block> SOUL_STONEOCK = registerBlock("soul_stoneock", (registryName) -> new Block(Properties.of().setId(ResourceKey.create(Registries.BLOCK, registryName)).strength(-1.0F, 3600000.0F).lightLevel((state) -> 0).sound(SoundType.STONE)));
    public static final DeferredBlock<Block> GHOST_ORE = registerBlock("ghost_ore", (registryName) -> new DropExperienceBlock(UniformInt.of(3, 8), Properties.of().setId(ResourceKey.create(Registries.BLOCK, registryName)).mapColor(MapColor.STONE).requiredFeatures(new FeatureFlag[0]).strength(1.6F, 6.0F).lightLevel((state) -> 0).sound(SoundType.STONE)));
    public static final DeferredBlock<Block> DEEPSLATE_GHOST_ORE = registerBlock("deepslate_ghost_ore", (registryName) -> new DropExperienceBlock(UniformInt.of(3, 8), Properties.of().setId(ResourceKey.create(Registries.BLOCK, registryName)).mapColor(MapColor.STONE).requiredFeatures(new FeatureFlag[0]).strength(1.6F, 6.0F).lightLevel((state) -> 0).sound(SoundType.STONE)));
    public static final DeferredBlock<Block> DEEPSLATE_SOUL_ORE = registerBlock("deepslate_soul_ore", (registryName) -> new DropExperienceBlock(UniformInt.of(4, 8), Properties.of().setId(ResourceKey.create(Registries.BLOCK, registryName)).mapColor(MapColor.STONE).requiredFeatures(new FeatureFlag[0]).strength(1.6F, 1.6F).lightLevel((state) -> 0).sound(SoundType.STONE)));
    public static final DeferredBlock<Block> SOUL_ORE = registerBlock("soul_ore", (registryName) -> new DropExperienceBlock(UniformInt.of(4, 8), Properties.of().setId(ResourceKey.create(Registries.BLOCK, registryName)).mapColor(MapColor.STONE).requiredFeatures(new FeatureFlag[0]).strength(1.6F, 6.0F).lightLevel((state) -> 0).sound(SoundType.STONE)));
    public static final DeferredBlock<ToulingPortalBlock> TOULING_PORTAL = registerBlock("touling_portal",(registryName) -> new ToulingPortalBlock(Properties.of().setId(ResourceKey.create(Registries.BLOCK, registryName)).strength(-1.0f, 360000.0f).lightLevel((state) -> 1).sound(SoundType.GLASS)));

    public static <T extends Block> DeferredBlock<T> registerBlock(String name, Function<ResourceLocation, T> block) {
        DeferredBlock<T> toReturn = BLOCKS.register(name, block);
        registerBlockItem(name, toReturn);
        return toReturn;
    }

    private static <T extends Block> void registerBlockItem(String name, DeferredBlock<T> block) {
        AllinzeroItems.ITEMS.register(name, (id) -> new BlockItem((Block)block.get(), (new Item.Properties()).setId(ResourceKey.create(Registries.ITEM, id))));
    }
}
