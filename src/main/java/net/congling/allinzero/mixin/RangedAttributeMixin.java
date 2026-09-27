package net.congling.allinzero.mixin;

import net.minecraft.world.entity.ai.attributes.RangedAttribute;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(RangedAttribute.class)
public class RangedAttributeMixin {

    @Shadow
    @Mutable
    private double maxValue;

    @Inject(method = "<init>(Ljava/lang/String;DDD)V", at = @At("TAIL"))
    private void allinzero$removeAttributeCaps(String descriptionId, double defaultValue, double min, double max,
                                               CallbackInfo ci) {
        if ("attribute.name.generic.armor".equals(descriptionId)
                || "attribute.name.generic.armor_toughness".equals(descriptionId)
                || "attribute.name.generic.attack_damage".equals(descriptionId)) {
            this.maxValue = Double.MAX_VALUE;
        }
    }
}
