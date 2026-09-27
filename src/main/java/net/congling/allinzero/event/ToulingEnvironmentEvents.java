package net.congling.allinzero.event;

import net.congling.allinzero.Allinzero;
import net.congling.allinzero.effect.AllinzeroEffects;
import net.congling.allinzero.effect.SoulErosionTracker;
import net.congling.allinzero.items.AllinzeroItems;
import net.congling.allinzero.worldgen.AllinzeroDimensions;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

@EventBusSubscriber(modid = Allinzero.MODID)
public class ToulingEnvironmentEvents {

    private static final int EFFECT_DURATION = 200;

    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Post event) {
        Player player = event.getEntity();
        if (player.level().isClientSide) {
            return;
        }

        SoulErosionTracker.Data data = SoulErosionTracker.get(player.getUUID());
        if (data.isImmune()) {
            data.decrementImmunity();
        }

        boolean inTouling = player.level().dimension() == AllinzeroDimensions.TOULING_LEVEL;

        if (!inTouling || data.isImmune()) {
            data.resetExposure();
            return;
        }

        data.incrementExposure();
        // 全套幽冥/灵魂盔甲：环境灵魂侵蚀等级降低 2/3 级
        int reduction = getErosionReduction(player);
        int level = SoulErosionTracker.levelForExposure(data.getExposedTicks()) - reduction;
        if (level <= 0) {
            return;
        }

        MobEffectInstance current = player.getEffect(AllinzeroEffects.SOUL_EROSION);
        boolean needApply = current == null
                || current.getAmplifier() < level - 1
                || current.getDuration() <= EFFECT_DURATION / 2;
        if (needApply) {
            player.addEffect(new MobEffectInstance(
                    AllinzeroEffects.SOUL_EROSION, EFFECT_DURATION, level - 1, false, true, true));
        }
    }

    @SubscribeEvent
    public static void onPlayerLoggedOut(PlayerEvent.PlayerLoggedOutEvent event) {
        SoulErosionTracker.remove(event.getEntity().getUUID());
    }

    /**
     * 套装灵魂侵蚀减免：全套灵魂盔甲降低 3 级，全套幽冥盔甲降低 2 级。
     */
    public static int getErosionReduction(Player player) {
        if (wearsFullSoulSet(player)) {
            return 3;
        }
        if (wearsFullGhostSet(player)) {
            return 2;
        }
        return 0;
    }

    public static boolean wearsFullGhostSet(Player player) {
        return player.getItemBySlot(EquipmentSlot.HEAD).is(AllinzeroItems.GHOST_HELMET.get())
                && player.getItemBySlot(EquipmentSlot.CHEST).is(AllinzeroItems.GHOST_CHESTPLATE.get())
                && player.getItemBySlot(EquipmentSlot.LEGS).is(AllinzeroItems.GHOST_LEGGINGS.get())
                && player.getItemBySlot(EquipmentSlot.FEET).is(AllinzeroItems.GHOST_BOOTS.get());
    }

    public static boolean wearsFullSoulSet(Player player) {
        return player.getItemBySlot(EquipmentSlot.HEAD).is(AllinzeroItems.SOUL_HELMET.get())
                && player.getItemBySlot(EquipmentSlot.CHEST).is(AllinzeroItems.SOUL_CHESTPLATE.get())
                && player.getItemBySlot(EquipmentSlot.LEGS).is(AllinzeroItems.SOUL_LEGGINGS.get())
                && player.getItemBySlot(EquipmentSlot.FEET).is(AllinzeroItems.SOUL_BOOTS.get());
    }
}
