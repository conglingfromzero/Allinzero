package net.congling.allinzero.items.custom;

import net.congling.allinzero.worldgen.portal.ToulingPortalShape;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;

import java.util.Optional;

public class SoulFlintAndSteelItem extends Item {

    public SoulFlintAndSteelItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        BlockPos clickedPos = context.getClickedPos();

        Optional<ToulingPortalShape> optional = ToulingPortalShape.findEmptyPortalShape(level, clickedPos);
        if (optional.isEmpty()) {
            optional = ToulingPortalShape.findEmptyPortalShape(level, clickedPos.relative(context.getClickedFace()));
        }

        if (optional.isEmpty()) {
            return InteractionResult.FAIL;
        }

        if (!level.isClientSide) {
            optional.get().createPortalBlocks();
            level.playSound(null, clickedPos, SoundEvents.PORTAL_TRIGGER, SoundSource.BLOCKS,
                    1.0F, level.random.nextFloat() * 0.4F + 0.8F);
            level.playSound(null, clickedPos, SoundEvents.PORTAL_AMBIENT, SoundSource.BLOCKS,
                    0.8F, 0.6F);

            // 门框中心喷发深蓝色汇聚粒子，表现传送门成型
            if (level instanceof net.minecraft.server.level.ServerLevel serverLevel) {
                BlockPos center = optional.get().getCenter();
                serverLevel.sendParticles(net.congling.allinzero.particles.AllinzeroParticles.TOULING_REVERSE_PORTAL.get(),
                        center.getX() + 0.5, center.getY() + 0.5, center.getZ() + 0.5,
                        80, 1.0, 1.5, 1.0, 0.08);
                serverLevel.sendParticles(net.minecraft.core.particles.ParticleTypes.SOUL_FIRE_FLAME,
                        center.getX() + 0.5, center.getY() + 0.5, center.getZ() + 0.5,
                        20, 1.0, 1.2, 1.0, 0.02);
            }

            Player player = context.getPlayer();
            ItemStack stack = context.getItemInHand();
            if (player != null) {
                stack.hurtAndBreak(1, player, LivingEntity.getSlotForHand(context.getHand()));
            }
        }

        return InteractionResult.sidedSuccess(level.isClientSide);
    }
}
