package net.congling.allinzero.blocks.custom;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BushBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

/**
 * 灵魂杆 / 灵魂茎：甘蔗形态的交叉面片植物。
 * 与原版甘蔗的区别：不要求邻水生长、不做生长逻辑；
 * 允许在同类方块上叠放（茎可置于杆上），碰撞箱与原版甘蔗一致。
 */
public class GhostCanesBlock extends BushBlock {
    public static final MapCodec<GhostCanesBlock> CODEC = simpleCodec(GhostCanesBlock::new);
    protected static final VoxelShape SHAPE = Block.box(2.0, 0.0, 2.0, 14.0, 16.0, 14.0);

    public GhostCanesBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }

    @Override
    protected MapCodec<? extends BushBlock> codec() {
        return CODEC;
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    /** 可置于泥土/农田（父类默认）或同类方块上（茎与杆上下叠放） */
    @Override
    protected boolean mayPlaceOn(BlockState state, BlockGetter level, BlockPos pos) {
        return state.getBlock() instanceof GhostCanesBlock || super.mayPlaceOn(state, level, pos);
    }
}
