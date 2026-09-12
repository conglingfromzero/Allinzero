package net.congling.allinzero.event;

import net.congling.allinzero.Allinzero;
import net.congling.allinzero.blocks.AllinzeroBlocks;
import net.congling.allinzero.worldgen.portal.ToulingPortalShape;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.FlintAndSteelItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;

import java.util.Optional;

/**
 * 打火石右键灵魂石砖门框内侧时激活透灵传送门。
 */
@EventBusSubscriber(modid = Allinzero.MODID)
public class ToulingPortalActivator {

    @SubscribeEvent
    public static void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        Level level = event.getLevel();
        if (level.isClientSide()) {
            return;
        }

        ItemStack stack = event.getItemStack();
        if (!(stack.getItem() instanceof FlintAndSteelItem)) {
            return;
        }

        BlockPos clicked = event.getPos();
        if (!level.getBlockState(clicked).is(AllinzeroBlocks.SOUL_STONE_BRICKS.get())) {
            return;
        }

        BlockPos interior = clicked.relative(event.getFace());
        Optional<ToulingPortalShape> shape = ToulingPortalShape.findEmptyPortalShape(level, interior);
        if (shape.isEmpty()) {
            return;
        }

        shape.get().createPortalBlocks();
        Player player = event.getEntity();
        level.playSound(
                null,
                clicked,
                SoundEvents.FLINTANDSTEEL_USE,
                SoundSource.PLAYERS,
                1.0F,
                level.getRandom().nextFloat() * 0.4F + 0.8F
        );

        if (!player.getAbilities().instabuild) {
            EquipmentSlot slot = event.getHand() == InteractionHand.MAIN_HAND
                    ? EquipmentSlot.MAINHAND
                    : EquipmentSlot.OFFHAND;
            stack.hurtAndBreak(1, player, slot);
        }
        player.swing(event.getHand());

        event.setCanceled(true);
        event.setCancellationResult(InteractionResult.SUCCESS);
    }
}
