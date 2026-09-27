package net.congling.allinzero.effect;

import net.congling.allinzero.Allinzero;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageType;

public class AllinzeroDamageTypes {
    public static final ResourceKey<DamageType> SOUL_EROSION = ResourceKey.create(
            Registries.DAMAGE_TYPE,
            ResourceLocation.fromNamespaceAndPath(Allinzero.MODID, "soul_erosion")
    );

    /** 磷光光弹命中伤害 */
    public static final ResourceKey<DamageType> PHOSPHOR_BOLT = ResourceKey.create(
            Registries.DAMAGE_TYPE,
            ResourceLocation.fromNamespaceAndPath(Allinzero.MODID, "phosphor_bolt")
    );

    /** 灵魂附体到期时的必杀伤害（无视护甲/创造模式/图腾） */
    public static final ResourceKey<DamageType> SOUL_POSSESSION = ResourceKey.create(
            Registries.DAMAGE_TYPE,
            ResourceLocation.fromNamespaceAndPath(Allinzero.MODID, "soul_possession")
    );
}
