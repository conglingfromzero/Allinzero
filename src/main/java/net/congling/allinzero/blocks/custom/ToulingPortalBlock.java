package net.congling.allinzero.blocks.custom;

import net.congling.allinzero.particles.AllinzeroParticles;
import net.congling.allinzero.worldgen.portal.ToulingTeleporter;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Portal;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.portal.DimensionTransition;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.Nullable;

public class ToulingPortalBlock extends Block implements Portal {
    public static final EnumProperty<Direction.Axis> AXIS = BlockStateProperties.HORIZONTAL_AXIS;

    protected static final VoxelShape X_AXIS_AABB = Block.box(0.0, 0.0, 6.0, 16.0, 16.0, 10.0);
    protected static final VoxelShape Z_AXIS_AABB = Block.box(6.0, 0.0, 0.0, 10.0, 16.0, 16.0);

    public ToulingPortalBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(AXIS, Direction.Axis.X));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(AXIS);
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return state.getValue(AXIS) == Direction.Axis.X ? X_AXIS_AABB : Z_AXIS_AABB;
    }

    @Override
    protected void entityInside(BlockState state, Level level, BlockPos pos, Entity entity) {
        if (entity.canUsePortal(false)) {
            // 交由原版 PortalProcessor 机制结算：冷却期间触碰会持续刷新冷却，
            // 从而避免实体停在目的传送门内时被反复传回（乒乓传送）
            entity.setAsInsidePortal(this, pos);
        }
    }

    @Override
    public int getPortalTransitionTime(ServerLevel level, Entity entity) {
        // 触碰即传送（与原版对非玩家实体一致）
        return 0;
    }

    @Nullable
    @Override
    public DimensionTransition getPortalDestination(ServerLevel level, Entity entity, BlockPos pos) {
        return ToulingTeleporter.getDestination(level, entity, pos);
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        if (random.nextInt(100) == 0) {
            level.playLocalSound(
                    (double) pos.getX() + 0.5,
                    (double) pos.getY() + 0.5,
                    (double) pos.getZ() + 0.5,
                    SoundEvents.PORTAL_AMBIENT,
                    SoundSource.BLOCKS,
                    0.5F,
                    random.nextFloat() * 0.4F + 0.8F,
                    false
            );
        }

        for (int i = 0; i < 4; i++) {
            double x = (double) pos.getX() + random.nextDouble();
            double y = (double) pos.getY() + random.nextDouble();
            double z = (double) pos.getZ() + random.nextDouble();
            double vx = (random.nextDouble() - 0.5) * 0.5;
            double vy = (random.nextDouble() - 0.5) * 0.5;
            double vz = (random.nextDouble() - 0.5) * 0.5;
            int direction = random.nextInt(2) * 2 - 1;

            if (!level.getBlockState(pos.west()).is(this) && !level.getBlockState(pos.east()).is(this)) {
                x = (double) pos.getX() + 0.5 + 0.25 * direction;
                vx = random.nextFloat() * 2.0F * direction;
            } else {
                z = (double) pos.getZ() + 0.5 + 0.25 * direction;
                vz = random.nextFloat() * 2.0F * direction;
            }

            level.addParticle(AllinzeroParticles.TOULING_PORTAL.get(), x, y, z, vx, vy, vz);
        }

        // 灵魂火焰粒子：贴合透灵维度的灵魂主题
        if (random.nextInt(4) == 0) {
            double sx = (double) pos.getX() + 0.5 + (random.nextDouble() - 0.5) * 0.5;
            double sy = (double) pos.getY() + random.nextDouble() * 0.8;
            double sz = (double) pos.getZ() + 0.5 + (random.nextDouble() - 0.5) * 0.5;
            level.addParticle(ParticleTypes.SOUL_FIRE_FLAME, sx, sy, sz, 0.0, 0.01, 0.0);
        }

        // 偶尔飘出灵魂粒子，增强氛围
        if (random.nextInt(8) == 0) {
            double sx = (double) pos.getX() + 0.5 + (random.nextDouble() - 0.5) * 0.6;
            double sy = (double) pos.getY() + 0.2;
            double sz = (double) pos.getZ() + 0.5 + (random.nextDouble() - 0.5) * 0.6;
            level.addParticle(ParticleTypes.SOUL, sx, sy, sz,
                    (random.nextDouble() - 0.5) * 0.02, 0.05, (random.nextDouble() - 0.5) * 0.02);
        }
    }
}
