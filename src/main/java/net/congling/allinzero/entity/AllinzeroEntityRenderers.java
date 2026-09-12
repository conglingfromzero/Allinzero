package net.congling.allinzero.entity;

import net.congling.allinzero.Allinzero;
import net.congling.allinzero.entity.touling.animal.soulcow.SoulCowRenderer;
import net.congling.allinzero.entity.touling.boss.touling_congling.ToulingConglingRenderer;
import net.minecraft.world.entity.EntityType;
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
        event.registerEntityRenderer((EntityType)AllinzeroEntity.SOUL_COW.get(), SoulCowRenderer::new);
        event.registerEntityRenderer((EntityType)AllinzeroEntity.TOULING_CONGLING.get(), ToulingConglingRenderer::new);
    }
}
