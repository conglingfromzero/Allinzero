package net.congling.allinzero.entity.touling.animal.soulsheep;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.SheepFurModel;
import net.minecraft.client.model.SheepModel;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

/**
 * 灵魂羊羊毛覆盖层：等价于原版 {@link net.minecraft.client.renderer.entity.layers.SheepFurLayer}，
 * 仅把羊毛贴图替换为灵魂青蓝版本；不做染色（恒为白色，即不对像素染色），也不做 jeb_ 彩虹。
 */
@OnlyIn(Dist.CLIENT)
public class SoulSheepFurLayer extends RenderLayer<SoulSheepEntity, SheepModel<SoulSheepEntity>> {
    private static final ResourceLocation SOUL_SHEEP_FUR_LOCATION =
            ResourceLocation.parse("allinzero:textures/entity/touling/animation/soul_sheep_fur.png");
    private final SheepFurModel<SoulSheepEntity> model;

    public SoulSheepFurLayer(RenderLayerParent<SoulSheepEntity, SheepModel<SoulSheepEntity>> renderer, EntityModelSet modelSet) {
        super(renderer);
        this.model = new SheepFurModel<>(modelSet.bakeLayer(ModelLayers.SHEEP_FUR));
    }

    @Override
    public void render(
            PoseStack poseStack,
            MultiBufferSource buffer,
            int packedLight,
            SoulSheepEntity entity,
            float limbSwing,
            float limbSwingAmount,
            float partialTicks,
            float ageInTicks,
            float netHeadYaw,
            float headPitch
    ) {
        if (entity.isSheared()) {
            return;
        }
        if (entity.isInvisible()) {
            // 与原版一致：隐身时只有发光（光谱箭/发光效果）描边才渲染羊毛层
            if (Minecraft.getInstance().shouldEntityAppearGlowing(entity)) {
                this.getParentModel().copyPropertiesTo(this.model);
                this.model.prepareMobModel(entity, limbSwing, limbSwingAmount, partialTicks);
                this.model.setupAnim(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
                VertexConsumer vertexConsumer = buffer.getBuffer(RenderType.outline(SOUL_SHEEP_FUR_LOCATION));
                this.model.renderToBuffer(poseStack, vertexConsumer, packedLight,
                        LivingEntityRenderer.getOverlayCoords(entity, 0.0F), -16777216);
            }
        } else {
            coloredCutoutModelCopyLayerRender(
                    this.getParentModel(),
                    this.model,
                    SOUL_SHEEP_FUR_LOCATION,
                    poseStack,
                    buffer,
                    packedLight,
                    entity,
                    limbSwing,
                    limbSwingAmount,
                    ageInTicks,
                    netHeadYaw,
                    headPitch,
                    partialTicks,
                    0xFFFFFFFF
            );
        }
    }
}
