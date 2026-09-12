package net.congling.allinzero.items;

import net.congling.allinzero.util.AllinzeroTags;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.neoforged.neoforge.common.SimpleTier;

public class AllinzeroToolTiers {
    public static final Tier GHOST = new SimpleTier(AllinzeroTags.Blocks.INCORRECT_FOR_GHOST_TOOL, 1561, 8.0F, 0.0F, 12, () -> Ingredient.of(new ItemLike[]{AllinzeroItems.GHOSTSTONE}));

    public static final Tier SOUL = new SimpleTier(AllinzeroTags.Blocks.INCORRECT_FOR_SOUL_TOOL, 2031, 9.0F, 0.0F, 15, () -> Ingredient.of(new ItemLike[]{AllinzeroItems.SOULSTONE}));

}
