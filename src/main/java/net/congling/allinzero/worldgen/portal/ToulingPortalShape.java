package net.congling.allinzero.worldgen.portal;

import net.congling.allinzero.blocks.AllinzeroBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;
import java.util.Optional;
import java.util.function.Predicate;

public class ToulingPortalShape {
    private static final int MIN_WIDTH = 3;
    public static final int MAX_WIDTH = 3;
    private static final int MIN_HEIGHT = 4;
    public static final int MAX_HEIGHT = 4;

    private static final BlockBehaviour.StatePredicate FRAME =
            (state, level, pos) -> state.is(AllinzeroBlocks.SOUL_STONE_BRICKS.get());

    private final LevelAccessor level;
    private final Direction.Axis axis;
    private final Direction rightDir;
    private int numPortalBlocks;
    @Nullable
    private BlockPos bottomLeft;
    private int height;
    private final int width;

    public static Optional<ToulingPortalShape> findEmptyPortalShape(LevelAccessor level, BlockPos bottomLeft) {
        Optional<ToulingPortalShape> optional = Optional
                .of(new ToulingPortalShape(level, bottomLeft, Direction.Axis.X))
                .filter(shape -> shape.isValid() && shape.numPortalBlocks == 0);
        if (optional.isPresent()) {
            return optional;
        }
        return Optional
                .of(new ToulingPortalShape(level, bottomLeft, Direction.Axis.Z))
                .filter(shape -> shape.isValid() && shape.numPortalBlocks == 0);
    }

    public ToulingPortalShape(LevelAccessor level, BlockPos bottomLeft, Direction.Axis axis) {
        this.level = level;
        this.axis = axis;
        this.rightDir = axis == Direction.Axis.X ? Direction.WEST : Direction.SOUTH;
        this.bottomLeft = this.calculateBottomLeft(bottomLeft);
        if (this.bottomLeft == null) {
            this.bottomLeft = bottomLeft;
            this.width = 1;
            this.height = 1;
        } else {
            this.width = this.calculateWidth();
            if (this.width > 0) {
                this.height = this.calculateHeight();
            }
        }
    }

    @Nullable
    private BlockPos calculateBottomLeft(BlockPos pos) {
        int minY = Math.max(this.level.getMinBuildHeight(), pos.getY() - 21);

        while (pos.getY() > minY && isEmpty(this.level.getBlockState(pos.below()))) {
            pos = pos.below();
        }

        Direction direction = this.rightDir.getOpposite();
        int distance = this.getDistanceUntilEdgeAboveFrame(pos, direction) - 1;
        return distance < 0 ? null : pos.relative(direction, distance);
    }

    private int calculateWidth() {
        int distance = this.getDistanceUntilEdgeAboveFrame(this.bottomLeft, this.rightDir);
        return distance >= MIN_WIDTH && distance <= MAX_WIDTH ? distance : 0;
    }

    private int getDistanceUntilEdgeAboveFrame(BlockPos pos, Direction direction) {
        BlockPos.MutableBlockPos mutable = new BlockPos.MutableBlockPos();

        for (int i = 0; i <= MAX_WIDTH; i++) {
            mutable.set(pos).move(direction, i);
            BlockState state = this.level.getBlockState(mutable);
            if (!isEmpty(state)) {
                if (FRAME.test(state, this.level, mutable)) {
                    return i;
                }
                break;
            }

            BlockState below = this.level.getBlockState(mutable.move(Direction.DOWN));
            if (!FRAME.test(below, this.level, mutable)) {
                break;
            }
        }

        return 0;
    }

    private int calculateHeight() {
        BlockPos.MutableBlockPos mutable = new BlockPos.MutableBlockPos();
        int distance = this.getDistanceUntilTop(mutable);
        return distance >= MIN_HEIGHT && distance <= MAX_HEIGHT && this.hasTopFrame(mutable, distance) ? distance : 0;
    }

    private boolean hasTopFrame(BlockPos.MutableBlockPos pos, int distanceToTop) {
        for (int i = 0; i < this.width; i++) {
            BlockPos.MutableBlockPos mutable = pos.set(this.bottomLeft)
                    .move(Direction.UP, distanceToTop)
                    .move(this.rightDir, i);
            if (!FRAME.test(this.level.getBlockState(mutable), this.level, mutable)) {
                return false;
            }
        }

        return true;
    }

    private int getDistanceUntilTop(BlockPos.MutableBlockPos pos) {
        for (int i = 0; i < MAX_HEIGHT; i++) {
            pos.set(this.bottomLeft).move(Direction.UP, i).move(this.rightDir, -1);
            if (!FRAME.test(this.level.getBlockState(pos), this.level, pos)) {
                return i;
            }

            pos.set(this.bottomLeft).move(Direction.UP, i).move(this.rightDir, this.width);
            if (!FRAME.test(this.level.getBlockState(pos), this.level, pos)) {
                return i;
            }

            for (int j = 0; j < this.width; j++) {
                pos.set(this.bottomLeft).move(Direction.UP, i).move(this.rightDir, j);
                BlockState state = this.level.getBlockState(pos);
                if (!isEmpty(state)) {
                    return i;
                }

                if (state.is(AllinzeroBlocks.TOULING_PORTAL.get())) {
                    this.numPortalBlocks++;
                }
            }
        }

        return MAX_HEIGHT;
    }

    private static boolean isEmpty(BlockState state) {
        return state.isAir() || state.is(BlockTags.FIRE) || state.is(AllinzeroBlocks.TOULING_PORTAL.get());
    }

    public boolean isValid() {
        return this.bottomLeft != null
                && this.width >= MIN_WIDTH && this.width <= MAX_WIDTH
                && this.height >= MIN_HEIGHT && this.height <= MAX_HEIGHT;
    }

    public BlockPos getCenter() {
        return this.bottomLeft.relative(this.rightDir, (this.width - 1) / 2)
                .relative(Direction.UP, this.height / 2);
    }

    public void createPortalBlocks() {
        BlockState portalState = AllinzeroBlocks.TOULING_PORTAL.get().defaultBlockState()
                .setValue(net.minecraft.world.level.block.state.properties.BlockStateProperties.HORIZONTAL_AXIS, this.axis);
        BlockPos.betweenClosed(
                this.bottomLeft,
                this.bottomLeft.relative(Direction.UP, this.height - 1).relative(this.rightDir, this.width - 1)
        ).forEach(pos -> this.level.setBlock(pos, portalState, 18));
    }
}
