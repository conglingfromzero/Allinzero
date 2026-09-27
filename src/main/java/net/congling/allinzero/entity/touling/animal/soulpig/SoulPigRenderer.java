package net.congling.allinzero.entity.touling.animal.soulpig;

import net.minecraft.client.model.PigModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

public class SoulPigRenderer extends MobRenderer<SoulPigEntity, PigModel<SoulPigEntity>> {
    private static final ResourceLocation SOUL_PIG_LOCATION =
            ResourceLocation.parse("allinzero:textures/entity/touling/animation/soul_pig.png");

    public SoulPigRenderer(EntityRendererProvider.Context context) {
        super(context, new PigModel<>(context.bakeLayer(ModelLayers.PIG)), 0.7F);
    }

    @Override
    public ResourceLocation getTextureLocation(SoulPigEntity entity) {
        return SOUL_PIG_LOCATION;
    }
}
