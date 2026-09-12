package net.congling.allinzero.datagen;

import net.congling.allinzero.Allinzero;
import net.congling.allinzero.loot.AllinzeroGlobalLootModifierProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.data.BlockTagsProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.data.event.GatherDataEvent;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@EventBusSubscriber(modid = Allinzero.MODID)
public class DataGenerators {

    @SubscribeEvent
    public static void gatherData(GatherDataEvent event){
        DataGenerator generator = event.getGenerator();
        PackOutput packOutput = generator.getPackOutput();
        ExistingFileHelper existingFileHelper = event.getExistingFileHelper();
        CompletableFuture<HolderLookup.Provider> lookupProvider = event.getLookupProvider();

//        generator.addProvider(event.includeServer(), new LootTableProvider(packOutput, Collections.emptySet(),
//                List.of(new LootTableProvider.SubProviderEntry(AllinzeroBlockLootTableProvider::new, LootContextParamSets.BLOCK)), lookupProvider));
//
//        generator.addProvider(event.includeServer(), new AllinzeroRecipeProvider(packOutput, lookupProvider));
//
//        BlockTagsProvider blockTagsProvider = new AllinzeroBlockTagsProvider(packOutput, lookupProvider,existingFileHelper);
//        generator.addProvider(event.includeServer(), blockTagsProvider);
//        generator.addProvider(event.includeServer(), new AllinzeroItemTagsProvider(packOutput, lookupProvider, blockTagsProvider.contentsGetter(), existingFileHelper));
//
//        generator.addProvider(event.includeServer(), new AllinzeroBlockStateProvider(packOutput, existingFileHelper));
//        generator.addProvider(event.includeServer(), new AllinzeroItemModelProvider(packOutput, existingFileHelper));

        generator.addProvider(event.includeServer(), new AllinzeroGlobalLootModifierProvider(packOutput,lookupProvider));
    }
}
