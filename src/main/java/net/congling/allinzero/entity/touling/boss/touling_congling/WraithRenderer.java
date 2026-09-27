package net.congling.allinzero.entity.touling.boss.touling_congling;

import net.congling.allinzero.Allinzero;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

public class WraithRenderer extends MobRenderer<WraithEntity, WraithModel> {
    private static final ResourceLocation WRAITH_LOCATION =
            ResourceLocation.fromNamespaceAndPath(Allinzero.MODID, "textures/entity/touling/monster/wraith/wraith.png");

    public WraithRenderer(EntityRendererProvider.Context context) {
        super(context, new WraithModel(context.bakeLayer(WraithModel.LAYER_LOCATION)), 0.3F);
    }

    @Override
    public ResourceLocation getTextureLocation(WraithEntity entity) {
        return WRAITH_LOCATION;
    }
}
