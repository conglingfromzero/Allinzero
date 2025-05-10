package net.fromzero.allinzero.datagen;

import com.google.common.collect.Sets;
import com.google.common.collect.UnmodifiableIterator;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.WritableRegistry;
import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.ValidationContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;

import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class AllinzeroLootTables extends LootTableProvider {
    public AllinzeroLootTables(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, Set.of(), List.of(new LootTableProvider.SubProviderEntry(AllinzeroBlockLootTables::new, LootContextParamSets.BLOCK)), registries);
    }

    protected void validate(WritableRegistry<LootTable> writableRegistry, ValidationContext validationContext, ProblemReporter.Collector problemReporter$collector) {
        Set<ResourceKey<LootTable>> modLootTablesId = (Set) BuiltInLootTables.all().stream().filter((idx) -> idx
                .registry().getNamespace().equals("allinzero_ore")).collect(Collectors.toSet());
        UnmodifiableIterator var5 = Sets.difference(modLootTablesId, writableRegistry.keySet()).iterator();

        while (var5.hasNext()) {
            ResourceKey<LootTable> id = (ResourceKey) var5.next();
            validationContext.reportProblem("Missing built-in table:" + String.valueOf(id));
        }
            writableRegistry.forEach((LootTable) -> LootTable.validate(validationContext));
        }

    }
