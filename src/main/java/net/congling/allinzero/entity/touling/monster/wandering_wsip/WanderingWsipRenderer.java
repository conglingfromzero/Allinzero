package net.congling.allinzero.entity.touling.monster.wandering_wsip;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class WanderingWsipRenderer extends GeoEntityRenderer<WanderingWsipEntity> {
    public WanderingWsipRenderer(EntityRendererProvider.Context context) {
        super(context, new WanderingWsipModel());
    }
}
