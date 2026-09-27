package net.congling.allinzero.entity.touling.boss.touling_congling;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.congling.allinzero.Allinzero;
import net.congling.allinzero.entity.touling.monster.phosphor.PhosphorBoltModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;

public class SoulBombRenderer extends EntityRenderer<SoulBombEntity> {
    private static final ResourceLocation TEXTURE =
            ResourceLocation.fromNamespaceAndPath(Allinzero.MODID, "textures/entity/touling/bosses/soul_bomb.png");

    private final PhosphorBoltModel model;

    public SoulBombRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.model = new PhosphorBoltModel(context.bakeLayer(PhosphorBoltModel.LAYER_LOCATION));
    }

    @Override
    public void render(SoulBombEntity entity, float entityYaw, float partialTicks,
                       PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        poseStack.pushPose();
        poseStack.translate(0.0, 0.2, 0.0);
        poseStack.scale(1.4F, 1.4F, 1.4F);
        VertexConsumer consumer = buffer.getBuffer(RenderType.entityTranslucentEmissive(TEXTURE));
        this.model.renderToBuffer(poseStack, consumer, packedLight, OverlayTexture.NO_OVERLAY, -1);
        poseStack.popPose();
        super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);
    }

    @Override
    public ResourceLocation getTextureLocation(SoulBombEntity entity) {
        return TEXTURE;
    }
}
