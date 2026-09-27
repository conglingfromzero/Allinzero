package net.congling.allinzero.entity.touling.boss.touling_congling;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

/**
 * 透灵从零渲染器：GeckoLib 驱动，阴影 0.5。
 */
public class ToulingConglingRenderer extends GeoEntityRenderer<ToulingConglingEntity> {
    public ToulingConglingRenderer(EntityRendererProvider.Context context) {
        super(context, new ToulingConglingGeoModel());
        this.shadowRadius = 0.5F;
    }
}
