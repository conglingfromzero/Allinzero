package net.congling.allinzero.blocks.custom;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import net.congling.allinzero.worldgen.AllinzeroDimensions;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BedBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BedPart;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

/**
 * 灵魂床：仅在透灵维度可正常睡觉、设置重生点；
 * 在其他维度右键使用时，触发与原版床在下界完全相同的爆炸效果。
 */
public class SoulBedBlock extends BedBlock {
    public static final MapCodec<SoulBedBlock> SOUL_CODEC = RecordCodecBuilder.mapCodec(
            inst -> inst.group(
                    DyeColor.CODEC.fieldOf("color").forGetter(BedBlock::getColor),
                    propertiesCodec()
            ).apply(inst, SoulBedBlock::new)
    );

    public SoulBedBlock(DyeColor color, BlockBehaviour.Properties properties) {
        super(color, properties);
    }

    @Override
    @SuppressWarnings("unchecked")
    public MapCodec<BedBlock> codec() {
        return (MapCodec<BedBlock>) (MapCodec<?>) SOUL_CODEC;
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        if (level.isClientSide) {
            return InteractionResult.CONSUME;
        }
        // 头部判定与原版一致：点到床尾时重定向到床头
        if (state.getValue(PART) != BedPart.HEAD) {
            pos = pos.relative(state.getValue(FACING));
            state = level.getBlockState(pos);
            if (!state.is(this)) {
                return InteractionResult.CONSUME;
            }
        }

        if (level.dimension() != AllinzeroDimensions.TOULING_LEVEL) {
            // 非透灵维度：复刻原版下界床爆炸（移除整张床 + 5 级 BLOCK 爆炸）
            level.removeBlock(pos, false);
            BlockPos footPos = pos.relative(state.getValue(FACING).getOpposite());
            if (level.getBlockState(footPos).is(this)) {
                level.removeBlock(footPos, false);
            }

            Vec3 vec3 = pos.getCenter();
            level.explode(null, level.damageSources().badRespawnPointExplosion(vec3), null, vec3,
                    5.0F, true, Level.ExplosionInteraction.BLOCK);
            return InteractionResult.SUCCESS;
        } else if (state.getValue(OCCUPIED)) {
            if (!this.soulKickVillagerOutOfBed(level, pos)) {
                player.displayClientMessage(Component.translatable("block.minecraft.bed.occupied"), true);
            }
            return InteractionResult.SUCCESS;
        } else {
            player.startSleepInBed(pos).ifLeft(reason -> {
                if (reason.getMessage() != null) {
                    player.displayClientMessage(reason.getMessage(), true);
                }
            });
            return InteractionResult.SUCCESS;
        }
    }

    /** 原版 kickVillagerOutOfBed 为 private，此处等价复制：把正在床上睡觉的村民赶下床 */
    private boolean soulKickVillagerOutOfBed(Level level, BlockPos pos) {
        List<Villager> list = level.getEntitiesOfClass(Villager.class, new AABB(pos), LivingEntity::isSleeping);
        if (list.isEmpty()) {
            return false;
        }
        list.get(0).stopSleeping();
        return true;
    }
}
