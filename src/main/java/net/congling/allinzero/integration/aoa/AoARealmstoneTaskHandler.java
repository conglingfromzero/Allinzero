package net.congling.allinzero.integration.aoa;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;

/**
 * AoA3 联动：空白纯宝石任务 —— 玩家主手持空白纯宝石击杀幽灵（aoa3:ghost）时，
 * 将其转化为透灵纯宝石，与 AoA3 各纯宝石的获取任务机制一致。
 * <p>
 * 通过注册表 ID 判定，不直接引用 AoA3 类；本类仅在 aoa3 已加载时注册到事件总线。
 */
public class AoARealmstoneTaskHandler {
    private static final ResourceLocation GHOST_ID = ResourceLocation.parse("aoa3:ghost");
    private static final ResourceLocation BLANK_REALMSTONE_ID = ResourceLocation.parse("aoa3:blank_realmstone");

    @SubscribeEvent
    public void onEntityKilled(LivingDeathEvent event) {
        if (!(event.getSource().getEntity() instanceof ServerPlayer player)) {
            return;
        }
        // 被击杀者须为 AoA3 幽灵
        if (!BuiltInRegistries.ENTITY_TYPE.getKey(event.getEntity().getType()).equals(GHOST_ID)) {
            return;
        }
        ItemStack held = player.getItemInHand(InteractionHand.MAIN_HAND);
        Item blankRealmstone = BuiltInRegistries.ITEM.get(BLANK_REALMSTONE_ID);
        if (!held.is(blankRealmstone)) {
            return;
        }
        player.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(AoA3Integration.TOULING_REALMSTONE.get()));
        player.sendSystemMessage(Component.translatable("message.allinzero.touling_realmstone_obtain"));
    }
}
