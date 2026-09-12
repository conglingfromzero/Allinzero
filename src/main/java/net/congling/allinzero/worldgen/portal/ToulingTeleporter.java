package net.congling.allinzero.worldgen.portal;

import net.congling.allinzero.blocks.AllinzeroBlocks;
import net.congling.allinzero.worldgen.AllinzeroDimensions;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.portal.DimensionTransition;
import net.minecraft.world.phys.Vec3;

public class ToulingTeleporter {
    private static final int SEARCH_RADIUS = 12;
    private static final int SEARCH_MIN_Y = -16;
    private static final int SEARCH_MAX_Y = 48;

    /**
     * 计算传送目的地：主世界 ↔ 透灵维度。优先使用目的维度已有的传送门，
     * 找不到则自动搭建新的灵魂石砖门框 + 3x4 传送门。
     * 传送本身的结算（冷却、changeDimension）由原版 PortalProcessor/handlePortal 机制完成。
     */
    @javax.annotation.Nullable
    public static DimensionTransition getDestination(ServerLevel serverLevel, Entity entity, BlockPos portalPos) {
        net.minecraft.resources.ResourceKey<Level> destinationKey =
                serverLevel.dimension() == AllinzeroDimensions.TOULING_LEVEL
                        ? Level.OVERWORLD
                        : AllinzeroDimensions.TOULING_LEVEL;
        ServerLevel destination = serverLevel.getServer().getLevel(destinationKey);
        if (destination == null) {
            return null;
        }

        Direction.Axis axis = serverLevel.getBlockState(portalPos)
                .getOptionalValue(BlockStateProperties.HORIZONTAL_AXIS)
                .orElse(Direction.Axis.X);

        BlockPos destinationPortal = findOrCreatePortal(destination, entity.blockPosition(), axis);
        if (destinationPortal == null) {
            return null;
        }

        // 出发前在原位置喷发深蓝色传送门粒子
        serverLevel.sendParticles(net.congling.allinzero.particles.AllinzeroParticles.TOULING_PORTAL.get(),
                entity.getX(), entity.getY() + entity.getBbHeight() / 2.0, entity.getZ(),
                60, 0.25, 0.6, 0.25, 0.5);

        Vec3 destinationPos = new Vec3(
                destinationPortal.getX() + 0.5,
                destinationPortal.getY(),
                destinationPortal.getZ() + 0.5
        );

        // 到达后的粒子与音效（以传送后的实体在目的维度执行）
        DimensionTransition.PostDimensionTransition arrivalEffects = arrived -> {
            if (arrived.level() instanceof ServerLevel arrivedLevel) {
                arrivedLevel.sendParticles(net.congling.allinzero.particles.AllinzeroParticles.TOULING_REVERSE_PORTAL.get(),
                        arrived.getX(), arrived.getY() + 1.5, arrived.getZ(),
                        80, 0.8, 1.5, 0.8, 0.06);
                arrivedLevel.playSound(null, arrived.blockPosition(), net.minecraft.sounds.SoundEvents.PORTAL_TRAVEL,
                        net.minecraft.sounds.SoundSource.PLAYERS, 0.6F, 1.0F);
            }
        };

        return new DimensionTransition(
                destination,
                destinationPos,
                entity.getDeltaMovement(),
                entity.getYRot(),
                entity.getXRot(),
                DimensionTransition.PLAY_PORTAL_SOUND
                        .then(DimensionTransition.PLACE_PORTAL_TICKET)
                        .then(arrivalEffects)
        );
    }

    private static BlockPos findOrCreatePortal(ServerLevel level, BlockPos around, Direction.Axis axis) {
        loadChunkNeighborhood(level, around.getX() >> 4, around.getZ() >> 4);

        BlockPos existing = findExistingPortal(level, around);
        if (existing != null) {
            return existing;
        }

        return createPortal(level, around.getX(), around.getZ(), axis);
    }

    private static void loadChunkNeighborhood(ServerLevel level, int chunkX, int chunkZ) {
        for (int dx = -1; dx <= 1; dx++) {
            for (int dz = -1; dz <= 1; dz++) {
                level.getChunk(chunkX + dx, chunkZ + dz);
            }
        }
    }

    @javax.annotation.Nullable
    private static BlockPos findExistingPortal(ServerLevel level, BlockPos around) {
        int minY = Mth.clamp(around.getY() + SEARCH_MIN_Y, level.getMinBuildHeight() + 1, level.getMaxBuildHeight() - 2);
        int maxY = Mth.clamp(around.getY() + SEARCH_MAX_Y, level.getMinBuildHeight() + 1, level.getMaxBuildHeight() - 2);
        BlockPos.MutableBlockPos mutable = new BlockPos.MutableBlockPos();

        for (int y = minY; y <= maxY; y++) {
            for (int dx = -SEARCH_RADIUS; dx <= SEARCH_RADIUS; dx++) {
                for (int dz = -SEARCH_RADIUS; dz <= SEARCH_RADIUS; dz++) {
                    mutable.set(around.getX() + dx, y, around.getZ() + dz);
                    if (level.getBlockState(mutable).is(AllinzeroBlocks.TOULING_PORTAL.get())) {
                        return toBottomPanel(level, mutable.immutable());
                    }
                }
            }
        }

        return null;
    }

    private static BlockPos toBottomPanel(ServerLevel level, BlockPos panel) {
        BlockPos.MutableBlockPos mutable = panel.mutable();
        while (mutable.getY() > level.getMinBuildHeight() + 1
                && level.getBlockState(mutable.below()).is(AllinzeroBlocks.TOULING_PORTAL.get())) {
            mutable.move(Direction.DOWN);
        }
        return mutable.immutable();
    }

    @javax.annotation.Nullable
    private static BlockPos createPortal(ServerLevel level, int centerX, int centerZ, Direction.Axis axis) {
        int blAxisCoord = Mth.floor(axis == Direction.Axis.X ? (double) centerX : (double) centerZ) - 1;
        int blOtherCoord = Mth.floor(axis == Direction.Axis.X ? (double) centerZ : (double) centerX);

        int anchorX = axis == Direction.Axis.X ? blAxisCoord + 1 : blOtherCoord;
        int anchorZ = axis == Direction.Axis.X ? blOtherCoord : blAxisCoord + 1;
        loadChunkNeighborhood(level, anchorX >> 4, anchorZ >> 4);

        int surfaceY = level.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, anchorX, anchorZ);
        int blY = Mth.clamp(surfaceY, level.getMinBuildHeight() + 2, level.getMaxBuildHeight() - 8);

        BlockState frameState = AllinzeroBlocks.SOUL_STONE_BRICKS.get().defaultBlockState();
        BlockState portalState = AllinzeroBlocks.TOULING_PORTAL.get().defaultBlockState()
                .setValue(BlockStateProperties.HORIZONTAL_AXIS, axis);

        BlockPos.MutableBlockPos mutable = new BlockPos.MutableBlockPos();
        for (int along = -1; along <= 3; along++) {
            for (int yOffset = -1; yOffset <= 4; yOffset++) {
                int x = axis == Direction.Axis.X ? blAxisCoord + along : blOtherCoord;
                int z = axis == Direction.Axis.X ? blOtherCoord : blAxisCoord + along;
                boolean frame = along == -1 || along == 3 || yOffset == -1 || yOffset == 4;
                mutable.set(x, blY + yOffset, z);
                level.setBlock(mutable, frame ? frameState : portalState, 18);
            }
        }

        int bottomX = axis == Direction.Axis.X ? blAxisCoord : blOtherCoord;
        int bottomZ = axis == Direction.Axis.X ? blOtherCoord : blAxisCoord;

        double particleX = axis == Direction.Axis.X ? bottomX + 1.5 : blOtherCoord + 0.5;
        double particleZ = axis == Direction.Axis.X ? blOtherCoord + 0.5 : bottomZ + 1.5;
        level.sendParticles(net.congling.allinzero.particles.AllinzeroParticles.TOULING_REVERSE_PORTAL.get(),
                particleX, blY + 2.0, particleZ, 100, 1.0, 1.6, 1.0, 0.06);
        level.playSound(null, bottomX, blY, bottomZ, net.minecraft.sounds.SoundEvents.END_PORTAL_SPAWN,
                net.minecraft.sounds.SoundSource.BLOCKS, 0.6F, 0.8F);

        return new BlockPos(bottomX, blY, bottomZ);
    }
}
