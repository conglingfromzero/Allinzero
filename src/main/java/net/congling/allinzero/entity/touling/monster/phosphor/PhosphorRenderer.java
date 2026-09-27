package net.congling.allinzero.entity.touling.monster.phosphor;

import net.congling.allinzero.Allinzero;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

public class PhosphorRenderer extends MobRenderer<PhosphorEntity, PhosphorModel> {
    private static final ResourceLocation TEXTURE =
            ResourceLocation.fromNamespaceAndPath(Allinzero.MODID, "textures/entity/touling/monster/phosphor/phosphor.png");

    public PhosphorRenderer(EntityRendererProvider.Context context) {
        super(context, new PhosphorModel(context.bakeLayer(PhosphorModel.LAYER_LOCATION)), 0.35F);
    }

    @Override
    public ResourceLocation getTextureLocation(PhosphorEntity entity) {
        return TEXTURE;
    }
}
