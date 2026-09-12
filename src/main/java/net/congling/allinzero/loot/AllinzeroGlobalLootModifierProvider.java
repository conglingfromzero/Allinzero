package net.congling.allinzero.loot;

import net.congling.allinzero.Allinzero;
import net.congling.allinzero.items.AllinzeroItems;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemRandomChanceCondition;
import net.neoforged.neoforge.common.data.GlobalLootModifierProvider;
import net.neoforged.neoforge.common.loot.LootTableIdCondition;

import java.util.concurrent.CompletableFuture;

public class AllinzeroGlobalLootModifierProvider extends GlobalLootModifierProvider {
    public AllinzeroGlobalLootModifierProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries){
        super(output, registries, Allinzero.MODID);
    }

    @Override
    protected void start() {
        this.add("berry_from_wither",
                new AddItemModifier(new LootItemCondition[]{
                        new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("entities/bosses/wither")).build(),
                        LootItemRandomChanceCondition.randomChance(1.0f).build()
                }, AllinzeroItems.SOUL_UPGRADE_SMITHING_TEMPLATE.get()));
    }

}
