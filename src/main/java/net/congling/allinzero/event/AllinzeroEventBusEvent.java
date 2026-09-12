package net.congling.allinzero.event;

import net.congling.allinzero.Allinzero;
import net.congling.allinzero.entity.AllinzeroEntity;
import net.congling.allinzero.entity.touling.animal.soulcow.SoulCowEntity;
import net.congling.allinzero.entity.touling.boss.touling_congling.ToulingConglingEntity;
import net.congling.allinzero.entity.touling.boss.touling_congling.ToulingConglingModel;
import net.minecraft.world.entity.EntityType;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;

@EventBusSubscriber(
        modid = Allinzero.MODID
)
public class AllinzeroEventBusEvent {
    @SubscribeEvent
    public static void registerLayers(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(ToulingConglingModel.LAYER_LOCATION, ToulingConglingModel::createBodyLayer);
    }

    @SubscribeEvent
    public static void registerAttributes(EntityAttributeCreationEvent event) {
        event.put((EntityType) AllinzeroEntity.TOULING_CONGLING.get(), ToulingConglingEntity.createAttributes().build());
        event.put((EntityType)AllinzeroEntity.SOUL_COW.get(), SoulCowEntity.createAttributes().build());
    }
}
