package net.congling.allinzero.util;

import net.congling.allinzero.items.AllinzeroItems;
import net.minecraft.client.renderer.item.ClampedItemPropertyFunction;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;

/**
 * 自定义弓的客户端物品属性：注册 pull（蓄力进度）/ pulling（是否正在拉弓），
 * 使弓在拉弓时按模型 overrides 切换到 pulling_0/1/2 贴图。
 * 公式与原版 BowItem 完全一致（满蓄力 20 tick，进度 = 已蓄力 tick / 20）。
 */
public class AllinzeroItemProperties {

    /** 满蓄力所需 tick，与原版弓一致 */
    private static final float DRAW_DURATION = 20.0F;

    public static void addCustomItemProperties() {
        makeCustomBow(AllinzeroItems.SOUL_BOW.get());
        makeCustomBow(AllinzeroItems.GHOST_BOW.get());
    }

    private static void makeCustomBow(Item item) {
        ItemProperties.register(item, ResourceLocation.withDefaultNamespace("pull"),
                (ClampedItemPropertyFunction) (stack, level, entity, seed) -> {
                    if (entity == null) {
                        return 0.0F;
                    }
                    return (float) (stack.getUseDuration(entity) - entity.getUseItemRemainingTicks()) / DRAW_DURATION;
                });
        ItemProperties.register(item, ResourceLocation.withDefaultNamespace("pulling"),
                (ClampedItemPropertyFunction) (stack, level, entity, seed)
                        -> entity != null && entity.isUsingItem() && entity.getUseItem() == stack ? 1.0F : 0.0F);
    }
}
