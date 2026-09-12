package net.congling.allinzero.entity.touling.boss.touling_congling;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

public class ToulingConglingRenderer extends MobRenderer<ToulingConglingEntity, ToulingConglingModel<ToulingConglingEntity>> {
    public ToulingConglingRenderer(EntityRendererProvider.Context context) {
        super(context, new ToulingConglingModel<>(context.bakeLayer(ToulingConglingModel.LAYER_LOCATION)), 0.5F);
    }

    public ResourceLocation getTextureLocation(ToulingConglingEntity entity) {
        return ResourceLocation.parse("allinzero:textures/entity/touling/bosses/touling_congling.png");
    }

}

