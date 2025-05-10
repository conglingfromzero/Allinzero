package net.fromzero.allinzero.item;

import net.minecraft.Util;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.equipment.ArmorMaterial;
import net.minecraft.world.item.equipment.ArmorType;
import net.minecraft.world.item.equipment.EquipmentAssets;
import net.neoforged.neoforge.common.Tags;

import java.util.EnumMap;

public class AllinzeroArmorMaterials {
    public static final ArmorMaterial GHOST_ARMOR_MATERIAL = new ArmorMaterial(
            15,
            Util.make(new EnumMap<>(ArmorType.class), map -> {
                map.put(ArmorType.BOOTS, 4);
                map.put(ArmorType.LEGGINGS, 7);
                map.put(ArmorType.CHESTPLATE, 8);
                map.put(ArmorType.HELMET, 4);
                map.put(ArmorType.BODY, 4);
            }),
            20,
            SoundEvents.ARMOR_EQUIP_GENERIC,
            4,
            0,
            Tags.Items.INGOTS_COPPER,
            ResourceKey.create(EquipmentAssets.ROOT_ID, ResourceLocation.fromNamespaceAndPath("allinzero", "ghost"))
    );
    public static final ArmorMaterial SOUL_ARMOR_MATERIAL = new ArmorMaterial(
            15,
            Util.make(new EnumMap<>(ArmorType.class), map -> {
                map.put(ArmorType.BOOTS, 5);
                map.put(ArmorType.LEGGINGS, 7);
                map.put(ArmorType.CHESTPLATE, 9);
                map.put(ArmorType.HELMET, 6);
                map.put(ArmorType.BODY, 4);
            }),
            20,
            SoundEvents.ARMOR_EQUIP_GENERIC,
            5,
            0,
            Tags.Items.INGOTS_COPPER,
            ResourceKey.create(EquipmentAssets.ROOT_ID, ResourceLocation.fromNamespaceAndPath("allinzero", "soul"))
    );
}
