package net.congling.allinzero.util;

import net.congling.allinzero.items.AllinzeroItems;
import net.minecraft.client.renderer.item.ClampedItemPropertyFunction;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.Item;

public class AllinzeroItemProperties {

    public static void addCustomItemProperties(){

        makeCustomBow(AllinzeroItems.SOUL_BOW.get());
        makeCustomBow(AllinzeroItems.GHOST_BOW.get());
    }

    private static void makeCustomBow(Item item) {
        ItemProperties.register(item, ResourceLocation.withDefaultNamespace("pull"), (ClampedItemPropertyFunction)((p_351682_, p_351683_, p_351684_, p_351685_) -> {
            if (p_351684_ == null) {
                return 0.0F;
            } else {
                return CrossbowItem.isCharged(p_351682_) ? 0.0F : (float)(p_351682_.getUseDuration(p_351684_) - p_351684_.getUseItemRemainingTicks()) / (float)CrossbowItem.getChargeDuration(p_351682_, p_351684_);
            }
        }));
        ItemProperties.register(item, ResourceLocation.withDefaultNamespace("pulling"),
                (ClampedItemPropertyFunction)((p_174605_, p_174606_, p_174607_, p_174608_)
                        -> p_174607_ != null && p_174607_.isUsingItem() && p_174607_.getUseItem() == p_174605_ && !CrossbowItem.isCharged(p_174605_) ? 1.0F : 0.0F));


    }
}