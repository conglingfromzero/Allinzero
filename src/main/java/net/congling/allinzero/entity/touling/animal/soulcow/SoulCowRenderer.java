package net.congling.allinzero.entity.touling.animal.soulcow;

import net.minecraft.client.model.CowModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

public class SoulCowRenderer extends MobRenderer<SoulCowEntity, CowModel<SoulCowEntity>> {
    public SoulCowRenderer(EntityRendererProvider.Context context) {
        super(context, new CowModel(context.bakeLayer(ModelLayers.COW)), 0.5F);
    }

    public ResourceLocation getTextureLocation(SoulCowEntity entity) {
        return ResourceLocation.parse("allinzero:textures/entity/touling/animation/soul_cow.png");
    }
}