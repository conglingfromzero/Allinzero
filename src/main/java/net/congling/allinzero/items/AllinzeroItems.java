package net.congling.allinzero.items;

import net.congling.allinzero.Allinzero;
import net.congling.allinzero.blocks.AllinzeroBlocks;
import net.congling.allinzero.entity.AllinzeroEntity;
import net.congling.allinzero.items.custom.AllinzeroBowItem;
import net.congling.allinzero.items.custom.SoulFlintAndSteelItem;
import net.congling.allinzero.items.custom.SoulMilkItem;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.*;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.DeferredSpawnEggItem;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public class AllinzeroItems {

    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(Allinzero.MODID);

    public static final DeferredItem<Item> GHOSTSTONE = ITEMS.register("ghoststone", () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> GHOST_STONE_BLOCK = ITEMS.register("ghost_stone_block", () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> SOULSTONE = ITEMS.register("soulstone", () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> GHOST_SPAR = ITEMS.register("ghost_spar", () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> SOUL_SPAR = ITEMS.register("soul_spar", () -> new Item(new Item.Properties()));
    /** 灵魂晶体：灵魂水晶+幽灵水晶合成，右击幽冥祭坛召唤强化版透灵从零 */
    public static final DeferredItem<Item> SOUL_CRYSTAL = ITEMS.register("soul_crystal", () -> new Item(new Item.Properties()));
    public static final DeferredItem<SoulMilkItem> SOUL_MILK = ITEMS.register("soul_milk", () -> new SoulMilkItem(new Item.Properties().stacksTo(1)));
    public static final DeferredItem<Item> SOUL_BEEF = ITEMS.register("soul_beef",
            () -> new Item((new Item.Properties()).food((new FoodProperties.Builder()).saturationModifier(5.0F).nutrition(8).alwaysEdible().build())));
    public static final DeferredItem<Item> COOKED_SOUL_BEEF = ITEMS.register("cooked_soul_beef",
            () -> new Item((new Item.Properties()).food((new FoodProperties.Builder()).saturationModifier(15.0F).nutrition(10).alwaysEdible().build())));

    public static final DeferredItem<Item> SOUL_MUTTON = ITEMS.register("soul_mutton",
            () -> new Item((new Item.Properties()).food((new FoodProperties.Builder()).saturationModifier(5.0F).nutrition(4).alwaysEdible().build())));
    public static final DeferredItem<Item> COOKED_SOUL_MUTTON = ITEMS.register("cooked_soul_mutton",
            () -> new Item((new Item.Properties()).food((new FoodProperties.Builder()).saturationModifier(12.0F).nutrition(8).alwaysEdible().build())));

    public static final DeferredItem<Item> SOUL_PORKCHOP = ITEMS.register("soul_porkchop",
            () -> new Item((new Item.Properties()).food((new FoodProperties.Builder()).saturationModifier(5.0F).nutrition(8).alwaysEdible().build())));
    public static final DeferredItem<Item> COOKED_SOUL_PORKCHOP = ITEMS.register("cooked_soul_porkchop",
            () -> new Item((new Item.Properties()).food((new FoodProperties.Builder()).saturationModifier(15.0F).nutrition(10).alwaysEdible().build())));

    public static final DeferredItem<SwordItem>  GHOST_SWORD = ITEMS.register("ghost_sword", () -> new SwordItem(AllinzeroToolTiers.GHOST,
            (new Item.Properties()).attributes(SwordItem.createAttributes(AllinzeroToolTiers.GHOST, 10.5F, -2.4F))));
    public static final DeferredItem<AxeItem> GHOST_AXE = ITEMS.register("ghost_axe", () -> new AxeItem(AllinzeroToolTiers.GHOST,
            (new Item.Properties()).attributes(AxeItem.createAttributes(AllinzeroToolTiers.GHOST, 15.5F, -3.0F))));
    public static final DeferredItem<PickaxeItem> GHOST_PICKAXE = ITEMS.register("ghost_pickaxe", () -> new PickaxeItem(AllinzeroToolTiers.GHOST,
            (new Item.Properties()).attributes(PickaxeItem.createAttributes(AllinzeroToolTiers.GHOST, 5.0F, -2.8F))));
    public static final DeferredItem<ShovelItem> GHOST_SHOVEL = ITEMS.register("ghost_shovel", () -> new ShovelItem(AllinzeroToolTiers.GHOST,
            (new Item.Properties()).attributes(SwordItem.createAttributes(AllinzeroToolTiers.GHOST, 3.0F, -2.8F))));
    public static final DeferredItem<HoeItem> GHOST_HOE = ITEMS.register("ghost_hoe", () -> new HoeItem(AllinzeroToolTiers.GHOST,
            (new Item.Properties()).attributes(HoeItem.createAttributes(AllinzeroToolTiers.GHOST, 4.0F, 0.0F))));
    public static final DeferredItem<BowItem> GHOST_BOW = ITEMS.register("ghost_bow", () -> new AllinzeroBowItem(5.0, (new Item.Properties()).durability(500)));

    public static final DeferredItem<SwordItem> SOUL_SWORD = ITEMS.register("soul_sword", () -> new SwordItem(AllinzeroToolTiers.SOUL,
            (new Item.Properties()).attributes(SwordItem.createAttributes(AllinzeroToolTiers.SOUL, 18.5F, -2.4F))));
    public static final DeferredItem<AxeItem> SOUL_AXE = ITEMS.register("soul_axe", () -> new AxeItem(AllinzeroToolTiers.SOUL,
            (new Item.Properties()).attributes(AxeItem.createAttributes(AllinzeroToolTiers.SOUL, 24.5F, -3.0F))));
    public static final DeferredItem<PickaxeItem> SOUL_PICKAXE = ITEMS.register("soul_pickaxe", () -> new PickaxeItem(AllinzeroToolTiers.SOUL,
            (new Item.Properties()).attributes(PickaxeItem.createAttributes(AllinzeroToolTiers.SOUL, 5.0F, -2.8F))));
    public static final DeferredItem<ShovelItem> SOUL_SHOVEL = ITEMS.register("soul_shovel", () -> new ShovelItem(AllinzeroToolTiers.SOUL,
            (new Item.Properties()).attributes(SwordItem.createAttributes(AllinzeroToolTiers.SOUL, 3.0F, -2.8F))));
    public static final DeferredItem<HoeItem> SOUL_HOE = ITEMS.register("soul_hoe", () -> new HoeItem(AllinzeroToolTiers.SOUL,
            (new Item.Properties()).attributes(HoeItem.createAttributes(AllinzeroToolTiers.SOUL, 4.0F, 0.0F))));
    public static final DeferredItem<BowItem> SOUL_BOW = ITEMS.register("soul_bow", () -> new AllinzeroBowItem(7.5, (new Item.Properties()).durability(800)));

    public static final DeferredItem<ArmorItem> GHOST_HELMET = ITEMS.register("ghost_helmet",
            () -> new ArmorItem(AllinzeroArmorMaterials.GHOST_ARMOR_MATERIAL, ArmorItem.Type.HELMET,
            (new Item.Properties()).durability(ArmorItem.Type.HELMET.getDurability(15))));
    public static final DeferredItem<ArmorItem> GHOST_CHESTPLATE = ITEMS.register("ghost_chestplate",
            () -> new ArmorItem(AllinzeroArmorMaterials.GHOST_ARMOR_MATERIAL, ArmorItem.Type.CHESTPLATE,
            (new Item.Properties()).durability(ArmorItem.Type.CHESTPLATE.getDurability(15))));
    public static final DeferredItem<ArmorItem> GHOST_LEGGINGS = ITEMS.register("ghost_leggings",
            () -> new ArmorItem(AllinzeroArmorMaterials.GHOST_ARMOR_MATERIAL, ArmorItem.Type.LEGGINGS,
            (new Item.Properties()).durability(ArmorItem.Type.LEGGINGS.getDurability(15))));
    public static final DeferredItem<ArmorItem> GHOST_BOOTS = ITEMS.register("ghost_boots",
            () -> new ArmorItem(AllinzeroArmorMaterials.GHOST_ARMOR_MATERIAL, ArmorItem.Type.BOOTS,
            (new Item.Properties()).durability(ArmorItem.Type.BOOTS.getDurability(15))));

    public static final DeferredItem<ArmorItem> SOUL_HELMET = ITEMS.register("soul_helmet",
            () -> new ArmorItem(AllinzeroArmorMaterials.SOUL_ARMOR_MATERIAL, ArmorItem.Type.HELMET, (
                    new Item.Properties()).durability(ArmorItem.Type.HELMET.getDurability(20))));
    public static final DeferredItem<ArmorItem> SOUL_CHESTPLATE = ITEMS.register("soul_chestplate",
            () -> new ArmorItem(AllinzeroArmorMaterials.SOUL_ARMOR_MATERIAL, ArmorItem.Type.CHESTPLATE, (
                    new Item.Properties()).durability(ArmorItem.Type.CHESTPLATE.getDurability(20))));
    public static final DeferredItem<ArmorItem> SOUL_LEGGINGS = ITEMS.register("soul_leggings",
            () -> new ArmorItem(AllinzeroArmorMaterials.SOUL_ARMOR_MATERIAL, ArmorItem.Type.LEGGINGS, (
                    new Item.Properties()).durability(ArmorItem.Type.LEGGINGS.getDurability(20))));
    public static final DeferredItem<ArmorItem> SOUL_BOOTS = ITEMS.register("soul_boots",
            () -> new ArmorItem(AllinzeroArmorMaterials.SOUL_ARMOR_MATERIAL, ArmorItem.Type.BOOTS, (
                    new Item.Properties()).durability(ArmorItem.Type.BOOTS.getDurability(20))));

    public static final DeferredItem<Item> SOUL_UPGRADE_SMITHING_TEMPLATE = ITEMS.register("soul_upgrade_smithing_template",
            () -> new Item(new Item.Properties()));
    public static final DeferredItem<SoulFlintAndSteelItem> SOUL_FLINT_AND_STEEL = ITEMS.register("soul_flint_and_steel",
            () -> new SoulFlintAndSteelItem(new Item.Properties().durability(1)));

    public static final DeferredItem<DeferredSpawnEggItem> PHOS_PHOR_SPAWN_EGG = ITEMS.register("phos_phor_spawn_egg",
            () -> new DeferredSpawnEggItem(AllinzeroEntity.PHOS_PHOR, 0x152238, 0x7CFFD4, new Item.Properties()));

    public static final DeferredItem<DeferredSpawnEggItem> SOUL_SHEEP_SPAWN_EGG = ITEMS.register("soul_sheep_spawn_egg",
            () -> new DeferredSpawnEggItem(AllinzeroEntity.SOUL_SHEEP, 0x0E3B40, 0x7CE8E0, new Item.Properties()));

    public static final DeferredItem<DeferredSpawnEggItem> SOUL_PIG_SPAWN_EGG = ITEMS.register("soul_pig_spawn_egg",
            () -> new DeferredSpawnEggItem(AllinzeroEntity.SOUL_PIG, 0x1B4A52, 0x9FE8DC, new Item.Properties()));

    public static final DeferredItem<DeferredSpawnEggItem> TOULING_CONGLING_SPAWN_EGG = ITEMS.register("touling_congling_spawn_egg",
            () -> new DeferredSpawnEggItem(AllinzeroEntity.TOULING_CONGLING, 0x1C2330, 0x00B4E8, new Item.Properties()));

    public static final DeferredItem<BedItem> SOUL_BED = ITEMS.register("soul_bed",
            () -> new BedItem(AllinzeroBlocks.SOUL_BED.get(),
                    new Item.Properties().component(DataComponents.BASE_COLOR, DyeColor.CYAN)));


    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }

}
