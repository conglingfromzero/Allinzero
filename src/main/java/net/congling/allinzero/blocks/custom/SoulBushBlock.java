package net.congling.allinzero.blocks.custom;

import com.mojang.serialization.MapCodec;
import net.congling.allinzero.blocks.AllinzeroBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.BushBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour;

/**
 * 灵魂灌木：可瞬间破坏（无硬度），破坏掉落木棍；
 * 只能种在灵魂沙子或灵魂草方块上。
 */
public class SoulBushBlock extends BushBlock {

    public static final MapCodec<SoulBushBlock> CODEC = simpleCodec(SoulBushBlock::new);

    @Override
    protected MapCodec<? extends BushBlock> codec() {
        return CODEC;
    }

    public SoulBushBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }

    @Override
    protected boolean mayPlaceOn(BlockState state, BlockGetter level, BlockPos pos) {
        return state.is(AllinzeroBlocks.SOUL_SAND.get()) || state.is(AllinzeroBlocks.SOUL_GRASS_BLOCK.get());
    }
}
