package net.congling.allinzero;

import net.congling.allinzero.entity.touling.boss.touling_congling.ToulingConglingModel;
import net.congling.allinzero.particles.AllinzeroParticles;
import net.congling.allinzero.particles.ToulingPortalParticle;
import net.congling.allinzero.particles.ToulingReversePortalParticle;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterParticleProvidersEvent;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;

@Mod(value = Allinzero.MODID, dist = Dist.CLIENT)
@EventBusSubscriber(modid = Allinzero.MODID, value = Dist.CLIENT)
public class AllinzeroClient {
    public AllinzeroClient(ModContainer container) {
        container.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);
    }

    @SubscribeEvent
    static void onClientSetup(FMLClientSetupEvent event) {

    }

    @SubscribeEvent
    static void onRegisterParticleProviders(RegisterParticleProvidersEvent event) {
        event.registerSpriteSet(AllinzeroParticles.TOULING_PORTAL.get(), ToulingPortalParticle.Provider::new);
        event.registerSpriteSet(AllinzeroParticles.TOULING_REVERSE_PORTAL.get(), ToulingReversePortalParticle.Provider::new);
    }

    @SubscribeEvent
    public static void registerLayerDefinitions(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(ToulingConglingModel.LAYER_LOCATION, ToulingConglingModel::createBodyLayer);
    }
}
