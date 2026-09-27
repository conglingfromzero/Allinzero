package net.congling.allinzero;

import net.congling.allinzero.blocks.AllinzeroBlocks;
import net.congling.allinzero.util.AllinzeroItemProperties;
import net.congling.allinzero.particles.AllinzeroParticles;
import net.congling.allinzero.particles.ToulingPortalParticle;
import net.congling.allinzero.particles.ToulingReversePortalParticle;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
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
        // 注册自定义弓的拉弓属性，使拉弓时切换 pulling 贴图
        event.enqueueWork(AllinzeroItemProperties::addCustomItemProperties);
        // 注册树叶、玻璃与镂空植物的渲染层，修复透视问题
        event.enqueueWork(() -> {
            // 树叶与镂空植物使用 cutout_mipped（带 mipmap 的镂空），与原版一致
            ItemBlockRenderTypes.setRenderLayer(AllinzeroBlocks.SOUL_LEAVES.get(), RenderType.cutoutMipped());
            ItemBlockRenderTypes.setRenderLayer(AllinzeroBlocks.GHOST_LEAVES.get(), RenderType.cutoutMipped());
            ItemBlockRenderTypes.setRenderLayer(AllinzeroBlocks.SOUL_BUSH.get(), RenderType.cutoutMipped());
            ItemBlockRenderTypes.setRenderLayer(AllinzeroBlocks.SOUL_GRASS.get(), RenderType.cutoutMipped());
            ItemBlockRenderTypes.setRenderLayer(AllinzeroBlocks.GHOST_CANES.get(), RenderType.cutoutMipped());
            ItemBlockRenderTypes.setRenderLayer(AllinzeroBlocks.GHOST_CANES_TOP.get(), RenderType.cutoutMipped());
            ItemBlockRenderTypes.setRenderLayer(AllinzeroBlocks.SOUL_TREE_SAPLING.get(), RenderType.cutoutMipped());
            ItemBlockRenderTypes.setRenderLayer(AllinzeroBlocks.GHOST_TREE_SAPLING.get(), RenderType.cutoutMipped());
            // 灵魂玻璃使用 translucent（半透明混合），其贴图为整体半透明 alpha
            ItemBlockRenderTypes.setRenderLayer(AllinzeroBlocks.SOUL_GLASS.get(), RenderType.translucent());
        });
    }

    @SubscribeEvent
    static void onRegisterParticleProviders(RegisterParticleProvidersEvent event) {
        event.registerSpriteSet(AllinzeroParticles.TOULING_PORTAL.get(), ToulingPortalParticle.Provider::new);
        event.registerSpriteSet(AllinzeroParticles.TOULING_REVERSE_PORTAL.get(), ToulingReversePortalParticle.Provider::new);
    }
}
