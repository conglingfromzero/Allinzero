package net.congling.allinzero.items.custom;

import net.congling.allinzero.effect.AllinzeroEffects;
import net.congling.allinzero.effect.SoulErosionTracker;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;

public class SoulMilkItem extends Item {
    public SoulMilkItem(Properties properties) {
        super(properties);
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity entity) {
        if (!level.isClientSide) {
            // 解除灵魂侵蚀与灵魂附体，并针对灵魂侵蚀获得 50s 免疫
            entity.removeEffect(AllinzeroEffects.SOUL_EROSION);
            entity.removeEffect(AllinzeroEffects.SOUL_POSSESSION);
            SoulErosionTracker.applyImmunity(entity.getUUID(), SoulErosionTracker.IMMUNITY_TICKS);

            if (entity instanceof ServerPlayer serverPlayer) {
                serverPlayer.awardStat(net.minecraft.stats.Stats.ITEM_USED.get(this));
            }
        }

        if (entity instanceof Player player && !player.getAbilities().instabuild) {
            stack.shrink(1);
        }
        return stack;
    }

    @Override
    public int getUseDuration(ItemStack stack, LivingEntity entity) {
        return 32;
    }

    @Override
    public UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.DRINK;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        player.playSound(SoundEvents.GENERIC_DRINK, 0.5F, 1.0F);
        return ItemUtils.startUsingInstantly(level, player, hand);
    }
}
