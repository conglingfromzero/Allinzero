package net.congling.allinzero.blocks;

import net.congling.allinzero.Allinzero;
import net.congling.allinzero.blocks.custom.GhostAltarBlock;
import net.congling.allinzero.blocks.custom.GhostCanesBlock;
import net.congling.allinzero.blocks.custom.SoulBedBlock;
import net.congling.allinzero.blocks.custom.SoulBushBlock;
import net.congling.allinzero.blocks.custom.SoulCactusBlock;
import net.congling.allinzero.blocks.custom.ToulingPortalBlock;
import net.congling.allinzero.items.AllinzeroItems;
import net.congling.allinzero.worldgen.tree.AllinzeroTreeGrowers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class AllinzeroBlocks {
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(Allinzero.MODID);
    
    public static final DeferredBlock<Block> SOUL_GRASS_BLOCK = registerBlock("soul_grass_block",
            () -> new Block(BlockBehaviour.Properties.of().strength(0.5F, 0.5F).lightLevel((state) -> 0).sound(SoundType.GRASS)
                    .requiresCorrectToolForDrops()));

    public static final DeferredBlock<Block> SOUL_STONE = registerBlock("soul_stone",
            () -> new Block(BlockBehaviour.Properties.of().strength(1.5F, 6.0F).lightLevel((state) -> 0).sound(SoundType.STONE)
                    .requiresCorrectToolForDrops()));

    public static final DeferredBlock<Block> SOUL_STONEOCK = registerBlock("soul_stoneock",
            () -> new Block(BlockBehaviour.Properties.of().strength(-1.0F, 3600000.0F).lightLevel((state) -> 0).sound(SoundType.STONE)));

    public static final DeferredBlock<Block> GHOST_ORE = registerBlock("ghost_ore",
            () -> new DropExperienceBlock(UniformInt.of(3, 8), BlockBehaviour.Properties.of().mapColor(MapColor.STONE)
                    .strength(1.6F, 6.0F).lightLevel((state) -> 0).sound(SoundType.STONE)
                    .requiresCorrectToolForDrops()));

    public static final DeferredBlock<Block> DEEPSLATE_GHOST_ORE = registerBlock("deepslate_ghost_ore",
            () -> new DropExperienceBlock(UniformInt.of(3, 8), BlockBehaviour.Properties.of().mapColor(MapColor.STONE)
                    .strength(1.6F, 6.0F).lightLevel((state) -> 0).sound(SoundType.STONE)
                    .requiresCorrectToolForDrops()));

    public static final DeferredBlock<Block> TOULING_GHOST_ORE = registerBlock("touling_ghost_ore",
            () -> new DropExperienceBlock(UniformInt.of(5, 10), BlockBehaviour.Properties.of().mapColor(MapColor.STONE)
                    .strength(1.6F, 6.0F).lightLevel((state) -> 0).sound(SoundType.STONE)
                    .requiresCorrectToolForDrops()));

    public static final DeferredBlock<Block> SOUL_ORE = registerBlock("soul_ore",
            () -> new DropExperienceBlock(UniformInt.of(5, 10), BlockBehaviour.Properties.of().mapColor(MapColor.STONE)
                    .strength(1.6F, 6.0F).lightLevel((state) -> 0).sound(SoundType.STONE)
                    .requiresCorrectToolForDrops()));

    public static final DeferredBlock<Block> DEEPSLATE_SOUL_ORE = registerBlock("deepslate_soul_ore",
            () -> new DropExperienceBlock(UniformInt.of(5, 10), BlockBehaviour.Properties.of().mapColor(MapColor.STONE)
                    .strength(1.6F, 6.0F).lightLevel((state) -> 0).sound(SoundType.STONE)
                    .requiresCorrectToolForDrops()));

    public static final DeferredBlock<Block> TOULING_SOUL_ORE = registerBlock("touling_soul_ore",
            () -> new DropExperienceBlock(UniformInt.of(8, 15), BlockBehaviour.Properties.of().mapColor(MapColor.STONE)
                    .strength(1.6F, 6.0F).lightLevel((state) -> 0).sound(SoundType.STONE)
                    .requiresCorrectToolForDrops()));

    public static final DeferredBlock<Block> SOUL_WOOD = registerBlock("soul_wood",
            () -> new Block(BlockBehaviour.Properties.of().strength(1.5F, 10.0F).lightLevel((state) -> 0).sound(SoundType.WOOD)));

    public static final DeferredBlock<Block> GHOST_WOOD = registerBlock("ghost_wood",
            () -> new Block(BlockBehaviour.Properties.of().strength(1.5F, 10.0F).lightLevel((state) -> 0).sound(SoundType.WOOD)));

    public static final DeferredBlock<Block> STRIPPED_SOUL_WOOD = registerBlock("stripped_soul_wood",
            () -> new Block(BlockBehaviour.Properties.of().strength(1.5F, 10.0F).lightLevel((state) -> 0).sound(SoundType.WOOD)));

    public static final DeferredBlock<Block> STRIPPED_GHOST_WOOD = registerBlock("stripped_ghost_wood",
            () -> new Block(BlockBehaviour.Properties.of().strength(1.5F, 10.0F).lightLevel((state) -> 0).sound(SoundType.WOOD)));

    public static final DeferredBlock<Block> SOUL_WOOD_PLANKS = registerBlock("soul_wood_planks",
            () -> new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_PLANKS)){

                @Override
                public boolean isFlammable(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
                    return true;
                }

                @Override
                public int getFlammability(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
                    return 0;
                }

                @Override
                public int getFireSpreadSpeed(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
                    return 0;
                }
            });

    public static final DeferredBlock<Block> GHOST_WOOD_PLANKS = registerBlock("ghost_wood_planks",
            () -> new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_PLANKS)){

                @Override
                public boolean isFlammable(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
                    return true;
                }

                @Override
                public int getFlammability(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
                    return 0;
                }

                @Override
                public int getFireSpreadSpeed(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
                    return 0;
                }
            });

    public static final DeferredBlock<Block> SOUL_LEAVES = registerBlock("soul_leaves",
            () -> new LeavesBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_LEAVES)){

        @Override
        public boolean isFlammable(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
            return true;
        }

        @Override
        public int getFlammability(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
            return 0;
        }

        @Override
        public int getFireSpreadSpeed(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
            return 0;
        }
    });

    public static final DeferredBlock<Block> GHOST_LEAVES = registerBlock("ghost_leaves",
            () -> new LeavesBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_LEAVES)){

                @Override
                public boolean isFlammable(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
                    return true;
                }

                @Override
                public int getFlammability(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
                    return 0;
                }

                @Override
                public int getFireSpreadSpeed(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
                    return 0;
                }
            });

    public static final DeferredBlock<Block> GHOST_TREE_SAPLING = registerBlock("ghost_tree_sapling",
            () -> new SaplingBlock(AllinzeroTreeGrowers.GHOST_TREE, BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_SAPLING)){

                    @Override
                    public boolean isFlammable(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
                        return true;
                    }

                    @Override
                    public int getFlammability(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
                        return 0;
                    }

                    @Override
                    public int getFireSpreadSpeed(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
                        return 0;
                    }

            });

    public static final DeferredBlock<Block> SOUL_TREE_SAPLING = registerBlock("soul_tree_sapling",
            () -> new SaplingBlock(AllinzeroTreeGrowers.SOUL_TREE, BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_SAPLING)){

                @Override
                public boolean isFlammable(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
                    return true;
                }

                @Override
                public int getFlammability(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
                    return 0;
                }

                @Override
                public int getFireSpreadSpeed(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
                    return 0;
                }

            });

    public static final DeferredBlock<Block> SOUL_GLASS = registerBlock("soul_glass",
            () -> new TransparentBlock(BlockBehaviour.Properties.of().strength(0.3F, 0.3F).sound(SoundType.GLASS)
                    .noOcclusion()));

    public static final DeferredBlock<Block> SOUL_GRASS = registerBlock("soul_grass",
            () -> new TallGrassBlock(BlockBehaviour.Properties.of().strength(0.0F, 0.0F).lightLevel((state) -> 0).sound(SoundType.GRASS)
                    .noCollission().noOcclusion()));

    public static final DeferredBlock<Block> GHOST_CANES = registerBlock("ghost_canes",
            () -> new GhostCanesBlock(BlockBehaviour.Properties.of().strength(0.0F, 0.0F).lightLevel((state) -> 0).sound(SoundType.GRASS)
                    .noOcclusion().pushReaction(PushReaction.DESTROY)));

    public static final DeferredBlock<Block> GHOST_CANES_TOP = registerBlock("ghost_canes_top",
            () -> new GhostCanesBlock(BlockBehaviour.Properties.of().strength(0.0F, 0.0F).lightLevel((state) -> 0).sound(SoundType.GRASS)
                    .noOcclusion().pushReaction(PushReaction.DESTROY)));

    public static final DeferredBlock<Block> SOUL_STONE_BRICKS = registerBlock("soul_stone_bricks",
            () -> new Block(BlockBehaviour.Properties.of().strength(1.5F, 6.0F).lightLevel((state) -> 0).sound(SoundType.STONE)));

    public static final DeferredBlock<StairBlock> SOUL_STONE_STAIRS = registerBlock("soul_stone_stairs",
            () -> new StairBlock(SOUL_STONE.get().defaultBlockState(),
                    BlockBehaviour.Properties.ofFullCopy(SOUL_STONE.get())));

    public static final DeferredBlock<SlabBlock> SOUL_STONE_SLAB = registerBlock("soul_stone_slab",
            () -> new SlabBlock(BlockBehaviour.Properties.ofFullCopy(SOUL_STONE.get())));

    public static final DeferredBlock<WallBlock> SOUL_STONE_WALL = registerBlock("soul_stone_wall",
            () -> new WallBlock(BlockBehaviour.Properties.ofFullCopy(SOUL_STONE.get())));

    public static final DeferredBlock<Block> SOUL_WOOL = registerBlock("soul_wool",
            () -> new Block(BlockBehaviour.Properties.of().strength(0.8F).lightLevel((state) -> 0).sound(SoundType.WOOL)));

    public static final DeferredBlock<Block> SOUL_WOOL_CARPET = registerBlock("soul_wool_carpet",
            () -> new CarpetBlock(BlockBehaviour.Properties.of().strength(0.1F).lightLevel((state) -> 0).sound(SoundType.WOOL)));

    /** 灵魂床：复用原版床逻辑（睡觉/设重生点），渲染走原版 BedRenderer（青色贴图被本模组覆盖为灵魂配色） */
    public static final DeferredBlock<BedBlock> SOUL_BED = registerBlockNoItem("soul_bed",
            () -> new SoulBedBlock(DyeColor.CYAN, BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_CYAN)
                    .strength(0.2F).lightLevel((state) -> 0).sound(SoundType.WOOD).pushReaction(PushReaction.DESTROY)));

    public static final DeferredBlock<Block> GHOST_ALTAR = registerBlock("ghost_altar",
            () -> new GhostAltarBlock(BlockBehaviour.Properties.of()
                    .strength(-1.0F, 3600000.0F).lightLevel((state) -> 7).sound(SoundType.STONE)));

    public static final DeferredBlock<Block> TOULING_PORTAL = registerBlock("touling_portal",
            () -> new ToulingPortalBlock(BlockBehaviour.Properties.of().
                    strength(-1.0F,3600000.0F).lightLevel((state) -> 15).sound(SoundType.GLASS).noLootTable()
                    .noOcclusion()));

    public static final DeferredBlock<Block> SOUL_SAND = registerBlock("soul_sand",
            () -> new SoulSandBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.SOUL_SAND)));

    public static final DeferredBlock<Block> SOUL_BUSH = registerBlock("soul_bush",
            () -> new SoulBushBlock(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_LIGHT_BLUE)
                    .strength(0.0F).sound(SoundType.GRASS).noCollission().noOcclusion()
                    .pushReaction(PushReaction.DESTROY)));

    public static final DeferredBlock<Block> SOUL_CACTUS = registerBlock("soul_cactus",
            () -> new SoulCactusBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.CACTUS)));

    private static <T extends Block> DeferredBlock<T> registerBlock(String name, Supplier<T> block) {
        DeferredBlock<T> toReturn = BLOCKS.register(name, block);
        registerBlockItem(name, toReturn);
        return toReturn;
    }

    private static <T extends Block> DeferredBlock<T> registerBlockNoItem(String name, Supplier<T> block) {
        return BLOCKS.register(name, block);
    }

    private static <T extends Block> void registerBlockItem(String name, DeferredBlock<T> block) {
        AllinzeroItems.ITEMS.register(name, () -> new BlockItem(block.get(), new Item.Properties()));
    }

    public static void register(IEventBus eventBus) {
        BLOCKS.register(eventBus);
    }
}
