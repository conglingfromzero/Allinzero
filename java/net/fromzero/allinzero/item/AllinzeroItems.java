package net.fromzero.allinzero.item;

import net.fromzero.allinzero.Allinzero;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.*;
import net.minecraft.world.item.equipment.ArmorType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;


import static net.fromzero.allinzero.item.AllinzeroArmorMaterials.GHOST_ARMOR_MATERIAL;
import static net.fromzero.allinzero.item.AllinzeroArmorMaterials.SOUL_ARMOR_MATERIAL;
import static net.fromzero.allinzero.item.AllinzeroToolMaterial.INIFINITY;
import static net.fromzero.allinzero.item.AllinzeroToolMaterial.ULTRA;

public class AllinzeroItems {

	public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(Allinzero.MODID);

	public static final DeferredItem<Item> GHOST_STONE = ITEMS.register("ghoststone", id -> new Item(new Item.Properties().setId(ResourceKey.create(Registries.ITEM, id))));

	public static final DeferredItem<Item> SOUL_STONE = ITEMS.register("soulstone", id -> new Item(new Item.Properties().setId(ResourceKey.create(Registries.ITEM, id))));

	public static final DeferredItem<Item> GHOST_STONE_BLOCK = ITEMS.register("ghost_stone_block", id -> new Item(new Item.Properties().setId(ResourceKey.create(Registries.ITEM, id))));

	public static final DeferredItem<SwordItem> GHOST_SWORD = AllinzeroItems.ITEMS.register("ghost_sword", id -> new SwordItem(INIFINITY, 11f, -2.4f, new Item.Properties().setId(ResourceKey.create(Registries.ITEM, id))));

	public static final DeferredItem<AxeItem> GHOST_AXE = AllinzeroItems.ITEMS.register("ghost_axe", id -> new AxeItem(INIFINITY, 15.5f, -3.0F, new Item.Properties().setId(ResourceKey.create(Registries.ITEM, id))));

	public static final DeferredItem<PickaxeItem> GHOST_PICKAXE = AllinzeroItems.ITEMS.register("ghost_pickaxe", id -> new PickaxeItem(INIFINITY, 7f, -2.8f, new Item.Properties().setId(ResourceKey.create(Registries.ITEM, id))));

	public static final DeferredItem<ShovelItem> GHOST_SHOVEL = AllinzeroItems.ITEMS.register("ghost_shovel", id -> new ShovelItem(INIFINITY, 3f, -2.0f, new Item.Properties().setId(ResourceKey.create(Registries.ITEM, id))));

	public static final DeferredItem<HoeItem> GHOST_HOE = AllinzeroItems.ITEMS.register("ghost_hoe", id -> new HoeItem(INIFINITY, 4f, 0.0f, new Item.Properties().setId(ResourceKey.create(Registries.ITEM, id))));

	public static final DeferredItem<HoeItem> SOUL_HOE = AllinzeroItems.ITEMS.register("soul_hoe", id -> new HoeItem(ULTRA, 5f, 0.0f, new Item.Properties().setId(ResourceKey.create(Registries.ITEM, id))));

	public static final DeferredItem<SwordItem> SOUL_SWORD = AllinzeroItems.ITEMS.register("soul_sword", id -> new SwordItem(ULTRA, 13.5f, -2.4f, new Item.Properties().setId(ResourceKey.create(Registries.ITEM, id))));

	public static final DeferredItem<AxeItem> SOUL_AXE = AllinzeroItems.ITEMS.register("soul_axe", id -> new AxeItem(ULTRA, 20.5f, -3.0F, new Item.Properties().setId(ResourceKey.create(Registries.ITEM, id))));

	public static final DeferredItem<PickaxeItem> SOUL_PICKAXE = AllinzeroItems.ITEMS.register("soul_pickaxe", id -> new PickaxeItem(ULTRA, 11f, -2.8f, new Item.Properties().setId(ResourceKey.create(Registries.ITEM, id))));

	public static final DeferredItem<ShovelItem> SOUL_SHOVEL = AllinzeroItems.ITEMS.register("soul_shovel", id -> new ShovelItem(ULTRA, 7.5f, -3.0f, new Item.Properties().setId(ResourceKey.create(Registries.ITEM, id))));

	public static final DeferredItem<Item> FROMZERO = AllinzeroItems.ITEMS.register("fromzero", id -> new Item(new Item.Properties().setId(ResourceKey.create(Registries.ITEM, id))));

	public static final DeferredItem<ArmorItem> GHOST_HELMET = ITEMS.registerItem("ghost_helmet",
			id -> new ArmorItem(GHOST_ARMOR_MATERIAL, ArmorType.HELMET, id));
	public static final DeferredItem<ArmorItem> GHOST_BOOTS = ITEMS.registerItem("ghost_boots",
			id -> new ArmorItem(GHOST_ARMOR_MATERIAL, ArmorType.BOOTS, id));
	public static final DeferredItem<ArmorItem> GHOST_LEGGINGS = ITEMS.registerItem("ghost_leggings",
			id -> new ArmorItem(GHOST_ARMOR_MATERIAL, ArmorType.LEGGINGS, id));
	public static final DeferredItem<ArmorItem> GHOST_CHESTPLATE = ITEMS.registerItem("ghost_chestplate",
			id -> new ArmorItem(GHOST_ARMOR_MATERIAL, ArmorType.CHESTPLATE, id));

	public static final DeferredItem<ArmorItem> SOUL_HELMET = ITEMS.registerItem("soul_helmet",
			id -> new ArmorItem(SOUL_ARMOR_MATERIAL, ArmorType.HELMET, id));
	public static final DeferredItem<ArmorItem> SOUL_BOOTS = ITEMS.registerItem("soul_boots",
			id -> new ArmorItem(SOUL_ARMOR_MATERIAL, ArmorType.BOOTS, id));
	public static final DeferredItem<ArmorItem> SOUL_LEGGINGS = ITEMS.registerItem("soul_leggings",
			id -> new ArmorItem(SOUL_ARMOR_MATERIAL, ArmorType.LEGGINGS,id));
	public static final DeferredItem<ArmorItem> SOUL_CHESTPLATE = ITEMS.registerItem("soul_chestplate",
			id -> new ArmorItem(SOUL_ARMOR_MATERIAL, ArmorType.CHESTPLATE, id));


	public static void register(IEventBus eventBus) {
		ITEMS.register(eventBus);
	}
}
