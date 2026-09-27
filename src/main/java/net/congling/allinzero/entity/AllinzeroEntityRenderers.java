package net.congling.allinzero.entity;

import net.congling.allinzero.Allinzero;
import net.congling.allinzero.entity.touling.animal.soulcow.SoulCowRenderer;
import net.congling.allinzero.entity.touling.animal.soulpig.SoulPigRenderer;
import net.congling.allinzero.entity.touling.animal.soulsheep.SoulSheepRenderer;
import net.congling.allinzero.entity.touling.boss.touling_congling.SoulBombRenderer;
import net.congling.allinzero.entity.touling.boss.touling_congling.ToulingConglingRenderer;
import net.congling.allinzero.entity.touling.boss.touling_congling.WraithRenderer;
import net.minecraft.client.renderer.entity.WitherBossRenderer;
import net.congling.allinzero.entity.touling.monster.phosphor.PhosphorBoltRenderer;
import net.congling.allinzero.entity.touling.monster.phosphor.PhosphorRenderer;
import net.congling.allinzero.entity.touling.monster.wandering_wsip.WanderingWsipRenderer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;

@EventBusSubscriber(
        modid = Allinzero.MODID,
        value = {Dist.CLIENT}
)
public class AllinzeroEntityRenderers {
    @SubscribeEvent
    public static void registerEntityRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(AllinzeroEntity.SOUL_COW.get(), SoulCowRenderer::new);
        event.registerEntityRenderer(AllinzeroEntity.SOUL_SHEEP.get(), SoulSheepRenderer::new);
        event.registerEntityRenderer(AllinzeroEntity.SOUL_PIG.get(), SoulPigRenderer::new);
        event.registerEntityRenderer(AllinzeroEntity.TOULING_CONGLING.get(), ToulingConglingRenderer::new);
        event.registerEntityRenderer(AllinzeroEntity.WRAITH.get(), WraithRenderer::new);
        event.registerEntityRenderer(AllinzeroEntity.WANDERING_WSIP.get(), WanderingWsipRenderer::new);
        event.registerEntityRenderer(AllinzeroEntity.SOUL_BOMB.get(), SoulBombRenderer::new);
        event.registerEntityRenderer(AllinzeroEntity.PHOS_PHOR.get(), PhosphorRenderer::new);
        event.registerEntityRenderer(AllinzeroEntity.PHOSPHOR_BOLT.get(), PhosphorBoltRenderer::new);
        event.registerEntityRenderer(AllinzeroEntity.SOUL_DRAIN_WITHER.get(), WitherBossRenderer::new);
    }
}
