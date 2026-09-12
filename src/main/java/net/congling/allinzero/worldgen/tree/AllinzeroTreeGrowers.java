package net.congling.allinzero.worldgen.tree;

import net.congling.allinzero.Allinzero;
import net.congling.allinzero.worldgen.feature.AllinzeroConfiguredFeatures;
import net.minecraft.world.level.block.grower.TreeGrower;

import java.util.Optional;

public class AllinzeroTreeGrowers {
    public static final TreeGrower GHOST_TREE = new TreeGrower(Allinzero.MODID + ":ghost_tree",
                                                    Optional.empty(), Optional.of(AllinzeroConfiguredFeatures.GHOST_TREE_KEY), Optional.empty());
    public static final TreeGrower SOUL_TREE = new TreeGrower(Allinzero.MODID + ":soul_tree",
            Optional.empty(), Optional.of(AllinzeroConfiguredFeatures.SOUL_TREE_KEY), Optional.empty());
}
