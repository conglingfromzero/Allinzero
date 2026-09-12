package net.congling.allinzero;

import net.congling.allinzero.blocks.AllinzeroBlocks;
import net.congling.allinzero.effect.AllinzeroEffects;
import net.congling.allinzero.entity.AllinzeroEntity;
import net.congling.allinzero.entity.touling.boss.touling_congling.ToulingConglingModel;
import net.congling.allinzero.items.AllinzeroCreativeTabs;
import net.congling.allinzero.items.AllinzeroItems;
import net.congling.allinzero.loot.AllinzeroLootModifiers;
import net.congling.allinzero.particles.AllinzeroParticles;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.event.server.ServerStartingEvent;

@Mod(Allinzero.MODID)
public class Allinzero {
    public static final String MODID = "allinzero";

    public Allinzero(IEventBus modEventBus, ModContainer modContainer) {

        modEventBus.addListener(this::registerLayerDefinitions);
        modEventBus.addListener(this::commonSetup);

        AllinzeroBlocks.register(modEventBus);
        AllinzeroItems.register(modEventBus);
        AllinzeroCreativeTabs.register(modEventBus);
        AllinzeroEntity.register(modEventBus);
        AllinzeroLootModifiers.register(modEventBus);
        AllinzeroParticles.register(modEventBus);
        AllinzeroEffects.register(modEventBus);

        NeoForge.EVENT_BUS.register(this);

        modEventBus.addListener(this::addCreative);

        modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }

    private void commonSetup(FMLCommonSetupEvent event) {
    }

    private void addCreative(BuildCreativeModeTabContentsEvent event) {
    }

    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
    }

    public void registerLayerDefinitions(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(ToulingConglingModel.LAYER_LOCATION, ToulingConglingModel::createBodyLayer);
    }

}
