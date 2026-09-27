package net.congling.allinzero.entity.touling.animal.soulsheep;

import net.minecraft.client.model.SheepModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

public class SoulSheepRenderer extends MobRenderer<SoulSheepEntity, SheepModel<SoulSheepEntity>> {
    private static final ResourceLocation SOUL_SHEEP_LOCATION =
            ResourceLocation.parse("allinzero:textures/entity/touling/animation/soul_sheep.png");

    public SoulSheepRenderer(EntityRendererProvider.Context context) {
        super(context, new SheepModel<>(context.bakeLayer(ModelLayers.SHEEP)), 0.7F);
        this.addLayer(new SoulSheepFurLayer(this, context.getModelSet()));
    }

    @Override
    public ResourceLocation getTextureLocation(SoulSheepEntity entity) {
        return SOUL_SHEEP_LOCATION;
    }
}
