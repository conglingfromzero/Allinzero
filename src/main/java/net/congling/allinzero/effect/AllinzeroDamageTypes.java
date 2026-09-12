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
}
