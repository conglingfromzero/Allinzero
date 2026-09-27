package net.congling.allinzero.entity.touling.boss.touling_congling;

import net.congling.allinzero.Allinzero;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.VexModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

/**
 * 怨灵模型：复用原版 {@link VexModel} 的几何体（createBodyLayer），
 * 但类型参数绑定为 {@link WraithEntity}（因其不再继承 Vex）。
 */
public class WraithModel extends HierarchicalModel<WraithEntity> {
    public static final ModelLayerLocation LAYER_LOCATION =
            new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(Allinzero.MODID, "wraith"), "main");

    private final ModelPart root;

    public WraithModel(ModelPart root) {
        this.root = root;
    }

    public static LayerDefinition createBodyLayer() {
        return VexModel.createBodyLayer();
    }

    @Override
    public void setupAnim(WraithEntity entity, float limbSwing, float limbSwingAmount,
                          float ageInTicks, float netHeadYaw, float headPitch) {
        this.root.getAllParts().forEach(ModelPart::resetPose);
        this.root.yRot = netHeadYaw * Mth.DEG_TO_RAD;
        this.root.xRot = headPitch * Mth.DEG_TO_RAD;
    }

    @Override
    public ModelPart root() {
        return this.root;
    }
}
