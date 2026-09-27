package net.congling.allinzero.entity.touling.monster.wandering_wsip;

import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class WanderingWsipModel extends GeoModel<WanderingWsipEntity> {
    private static final ResourceLocation MODEL = ResourceLocation.fromNamespaceAndPath(
            "allinzero", "geo/touling/monster/wandering_wsip/wandering_wisp.geo.json");
    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(
            "allinzero", "textures/entity/touling/monster/wandering_wsip/wandering_wsip.png");
    private static final ResourceLocation ANIMATION = ResourceLocation.fromNamespaceAndPath(
            "allinzero", "animations/touling/wandering_wsip/wandering_wsip.animation.json");

    @Override
    public ResourceLocation getModelResource(WanderingWsipEntity animatable) {
        return MODEL;
    }

    @Override
    public ResourceLocation getTextureResource(WanderingWsipEntity animatable) {
        return TEXTURE;
    }

    @Override
    public ResourceLocation getAnimationResource(WanderingWsipEntity animatable) {
        return ANIMATION;
    }
}
