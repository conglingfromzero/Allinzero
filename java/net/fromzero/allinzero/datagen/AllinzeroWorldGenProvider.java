package net.fromzero.allinzero.datagen;

import net.fromzero.allinzero.worldgen.featre.AllinzeroConfiguredFeatures;
import net.fromzero.allinzero.worldgen.featre.AllinzeroPlacedFeatures;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.DatapackBuiltinEntriesProvider;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.Set;
import java.util.concurrent.CompletableFuture;

public class AllinzeroWorldGenProvider extends DatapackBuiltinEntriesProvider {
    public AllinzeroWorldGenProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries){
        super(output, registries, (new RegistrySetBuilder()).add(Registries.CONFIGURED_FEATURE, AllinzeroConfiguredFeatures::bootstrap)
                .add(Registries.PLACED_FEATURE, AllinzeroPlacedFeatures::bootstrap)
                .add(NeoForgeRegistries.Keys.BIOME_MODIFIERS, AllinzeroBiomesModifiers::bootstrap), Set.of("allinzero_ore"));
    }
}
