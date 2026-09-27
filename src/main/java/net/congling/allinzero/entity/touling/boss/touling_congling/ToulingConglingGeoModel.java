package net.congling.allinzero.entity.touling.boss.touling_congling;

import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

/**
 * 透灵从零 GeckoLib 模型：
 * geo/touling/boss 下的模型、animations/touling/boss 下的动画，
 * 贴图沿用原 BOSS 贴图。
 */
public class ToulingConglingGeoModel extends GeoModel<ToulingConglingEntity> {
    private static final ResourceLocation MODEL = ResourceLocation.fromNamespaceAndPath(
            "allinzero", "geo/touling/boss/touling_congling.geo.json");
    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(
            "allinzero", "textures/entity/touling/bosses/touling_congling.png");
    private static final ResourceLocation ANIMATION = ResourceLocation.fromNamespaceAndPath(
            "allinzero", "animations/touling/boss/touling_congling.animation.json");

    @Override
    public ResourceLocation getModelResource(ToulingConglingEntity animatable) {
        return MODEL;
    }

    @Override
    public ResourceLocation getTextureResource(ToulingConglingEntity animatable) {
        return TEXTURE;
    }

    @Override
    public ResourceLocation getAnimationResource(ToulingConglingEntity animatable) {
        return ANIMATION;
    }
}
