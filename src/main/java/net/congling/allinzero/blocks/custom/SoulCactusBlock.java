package net.congling.allinzero.blocks.custom;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.CactusBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour;

/**
 * 灵魂仙人掌：与原版仙人掌一致（扎伤、生长、碰撞箱），
 * 区别是可在灵魂沙子上放置/存活，而不仅限于原版沙子。
 */
public class SoulCactusBlock extends CactusBlock {

    public SoulCactusBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }

    @Override
    protected boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        // 水平方向不能挨着固体方块或岩浆（与原版一致）
        for (Direction direction : Direction.Plane.HORIZONTAL) {
            BlockState blockstate = level.getBlockState(pos.relative(direction));
            if (blockstate.isSolid() || level.getFluidState(pos.relative(direction)).is(FluidTags.LAVA)) {
                return false;
            }
        }

        BlockState below = level.getBlockState(pos.below());
        // 可在灵魂仙人掌上叠放，或以灵魂沙子为底；上方不能是液体
        return (below.is(this) || below.is(net.congling.allinzero.blocks.AllinzeroBlocks.SOUL_SAND.get()))
                && !level.getBlockState(pos.above()).liquid();
    }
}
