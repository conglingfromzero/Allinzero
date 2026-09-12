package net.congling.allinzero.entity;

import net.congling.allinzero.Allinzero;
import net.congling.allinzero.entity.touling.animal.soulcow.SoulCowEntity;
import net.congling.allinzero.entity.touling.boss.touling_congling.ToulingConglingEntity;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class AllinzeroEntity {
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES = DeferredRegister.create(BuiltInRegistries.ENTITY_TYPE, Allinzero.MODID);

    public static final Supplier<EntityType<SoulCowEntity>> SOUL_COW = ENTITY_TYPES.register("soul_cow",
            () -> EntityType.Builder.of(SoulCowEntity::new, MobCategory.CREATURE).sized(1.0F, 1.5F).build("soul_cow"));


    public static final Supplier<EntityType<ToulingConglingEntity>> TOULING_CONGLING = ENTITY_TYPES.register("touling_congling",
            () -> EntityType.Builder.of(ToulingConglingEntity::new, MobCategory.MONSTER).sized(1.0F, 1.8F).build("touling_congling"));

    public static void register(IEventBus eventBus) {
        ENTITY_TYPES.register(eventBus);
    }

}
