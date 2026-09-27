package net.congling.allinzero.entity;

import net.congling.allinzero.Allinzero;
import net.congling.allinzero.entity.touling.animal.soulcow.SoulCowEntity;
import net.congling.allinzero.entity.touling.animal.soulpig.SoulPigEntity;
import net.congling.allinzero.entity.touling.animal.soulsheep.SoulSheepEntity;
import net.congling.allinzero.entity.touling.boss.touling_congling.SoulBombEntity;
import net.congling.allinzero.entity.touling.boss.touling_congling.SoulDrainWitherEntity;
import net.congling.allinzero.entity.touling.boss.touling_congling.ToulingConglingEntity;
import net.congling.allinzero.entity.touling.boss.touling_congling.WraithEntity;
import net.congling.allinzero.entity.touling.monster.phosphor.PhosphorBoltEntity;
import net.congling.allinzero.entity.touling.monster.phosphor.PhosphorEntity;
import net.congling.allinzero.entity.touling.monster.wandering_wsip.WanderingWsipEntity;
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

    public static final Supplier<EntityType<SoulSheepEntity>> SOUL_SHEEP = ENTITY_TYPES.register("soul_sheep",
            () -> EntityType.Builder.of(SoulSheepEntity::new, MobCategory.CREATURE).sized(0.9F, 1.3F).build("soul_sheep"));

    public static final Supplier<EntityType<SoulPigEntity>> SOUL_PIG = ENTITY_TYPES.register("soul_pig",
            () -> EntityType.Builder.of(SoulPigEntity::new, MobCategory.CREATURE).sized(0.9F, 0.9F).build("soul_pig"));


    public static final Supplier<EntityType<ToulingConglingEntity>> TOULING_CONGLING = ENTITY_TYPES.register("touling_congling",
            () -> EntityType.Builder.of(ToulingConglingEntity::new, MobCategory.MONSTER).sized(1.0F, 1.8F).build("touling_congling"));

    public static final Supplier<EntityType<PhosphorEntity>> PHOS_PHOR = ENTITY_TYPES.register("phos_phor",
            () -> EntityType.Builder.of(PhosphorEntity::new, MobCategory.AMBIENT).sized(0.8F, 0.8F)
                    .clientTrackingRange(12).build("phos_phor"));

    public static final Supplier<EntityType<PhosphorBoltEntity>> PHOSPHOR_BOLT = ENTITY_TYPES.register("phosphor_bolt",
            () -> EntityType.Builder.<PhosphorBoltEntity>of(PhosphorBoltEntity::new, MobCategory.MISC)
                    .sized(0.4F, 0.4F).clientTrackingRange(6).updateInterval(10).build("phosphor_bolt"));

    public static final Supplier<EntityType<WraithEntity>> WRAITH = ENTITY_TYPES.register("wraith",
            () -> EntityType.Builder.of(WraithEntity::new, MobCategory.MONSTER)
                    .sized(0.4F, 0.8F).clientTrackingRange(12).build("wraith"));

    public static final Supplier<EntityType<WanderingWsipEntity>> WANDERING_WSIP = ENTITY_TYPES.register("wandering_wsip",
            () -> EntityType.Builder.of(WanderingWsipEntity::new, MobCategory.MONSTER)
                    .sized(0.7F, 2.0F).clientTrackingRange(10).build("wandering_wsip"));

    public static final Supplier<EntityType<SoulBombEntity>> SOUL_BOMB = ENTITY_TYPES.register("soul_bomb",
            () -> EntityType.Builder.<SoulBombEntity>of(SoulBombEntity::new, MobCategory.MISC)
                    .sized(0.5F, 0.5F).clientTrackingRange(8).updateInterval(10).build("soul_bomb"));

    public static final Supplier<EntityType<SoulDrainWitherEntity>> SOUL_DRAIN_WITHER = ENTITY_TYPES.register("soul_drain_wither",
            () -> EntityType.Builder.of(SoulDrainWitherEntity::new, MobCategory.MONSTER)
                    .sized(0.9F, 3.5F).fireImmune().clientTrackingRange(10).build("soul_drain_wither"));

    public static void register(IEventBus eventBus) {
        ENTITY_TYPES.register(eventBus);
    }

}
