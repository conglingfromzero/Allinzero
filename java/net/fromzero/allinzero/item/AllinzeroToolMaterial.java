package net.fromzero.allinzero.item;

import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.ToolMaterial;

public class AllinzeroToolMaterial {
	
	public static final ToolMaterial INIFINITY = new ToolMaterial(BlockTags.INCORRECT_FOR_DIAMOND_TOOL,
			1561, 8.0F, 0F, 12, ItemTags.DIAMOND_TOOL_MATERIALS);
	public static final ToolMaterial ULTRA = new ToolMaterial(BlockTags.INCORRECT_FOR_NETHERITE_TOOL,
			2031, 9.0F, 0F, 15, ItemTags.NETHERITE_TOOL_MATERIALS);
}
