package net.congling.allinzero.entity.touling.monster.phosphor;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.congling.allinzero.Allinzero;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.AnimationState;

public class PhosphorModel extends HierarchicalModel<PhosphorEntity> {
    public static final ModelLayerLocation LAYER_LOCATION =
            new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(Allinzero.MODID, "phos_phor"), "main");

    private final ModelPart root;
    private final ModelPart body;
    private final AnimationState idleState = new AnimationState();
    private float lastAge = 0.0F;

    public PhosphorModel(ModelPart root) {
        super(RenderType::entityTranslucent);
        this.root = root;
        this.body = root.getChild("body");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();

        PartDefinition body = root.addOrReplaceChild("body",
                CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -4.0F, -4.0F, 8.0F, 8.0F, 8.0F),
                PartPose.offset(0.0F, 14.0F, 0.0F));

        body.addOrReplaceChild("core",
                CubeListBuilder.create().texOffs(32, 16).addBox(-2.0F, -2.0F, -2.0F, 4.0F, 4.0F, 4.0F),
                PartPose.offset(0.0F, 0.0F, 0.0F));

        root.addOrReplaceChild("green",
                CubeListBuilder.create().texOffs(32, 24).addBox(-1.0F, -1.0F, -1.0F, 2.0F, 2.0F, 2.0F),
                PartPose.offset(5.0F, 14.0F, 0.0F));
        root.addOrReplaceChild("blue",
                CubeListBuilder.create().texOffs(40, 24).addBox(-1.0F, -1.0F, -1.0F, 2.0F, 2.0F, 2.0F),
                PartPose.offset(-5.0F, 14.0F, 0.0F));
        root.addOrReplaceChild("sky blue",
                CubeListBuilder.create().texOffs(48, 24).addBox(-1.0F, -1.0F, -1.0F, 2.0F, 2.0F, 2.0F),
                PartPose.offset(0.0F, 9.0F, 0.0F));
        root.addOrReplaceChild("white",
                CubeListBuilder.create().texOffs(32, 28).addBox(-1.0F, -1.0F, -1.0F, 2.0F, 2.0F, 2.0F),
                PartPose.offset(0.0F, 14.0F, 5.0F));
        root.addOrReplaceChild("black",
                CubeListBuilder.create().texOffs(40, 28).addBox(-1.0F, -1.0F, -1.0F, 2.0F, 2.0F, 2.0F),
                PartPose.offset(0.0F, 14.0F, -5.0F));

        root.addOrReplaceChild("bone",
                CubeListBuilder.create().texOffs(48, 28).addBox(-1.0F, 0.0F, -1.0F, 2.0F, 4.0F, 2.0F),
                PartPose.offset(0.0F, 18.0F, 0.0F));

        return LayerDefinition.create(mesh, 64, 64);
    }

    @Override
    public void setupAnim(PhosphorEntity entity, float limbSwing, float limbSwingAmount,
                          float ageInTicks, float netHeadYaw, float headPitch) {
        this.root().getAllParts().forEach(ModelPart::resetPose);

        if (ageInTicks < this.lastAge) {
            this.idleState.stop();
        }
        this.lastAge = ageInTicks;
        this.idleState.startIfStopped((int) ageInTicks);
        this.animate(this.idleState, PhorphosModelAnimation.PGOSPHOR_PENDING, ageInTicks, 1.0F);
    }

    @Override
    public ModelPart root() {
        return this.root;
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer consumer, int packedLight, int packedOverlay, int color) {
        this.root.render(poseStack, consumer, net.minecraft.client.renderer.LightTexture.FULL_BRIGHT, packedOverlay, color);
    }
}
