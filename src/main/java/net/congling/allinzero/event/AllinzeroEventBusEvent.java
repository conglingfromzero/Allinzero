package net.congling.allinzero.event;

import net.congling.allinzero.Allinzero;
import net.congling.allinzero.blocks.AllinzeroBlocks;
import net.congling.allinzero.entity.AllinzeroEntity;
import net.congling.allinzero.entity.touling.animal.soulcow.SoulCowEntity;
import net.congling.allinzero.entity.touling.animal.soulpig.SoulPigEntity;
import net.congling.allinzero.entity.touling.animal.soulsheep.SoulSheepEntity;
import net.congling.allinzero.entity.touling.boss.touling_congling.SoulDrainWitherEntity;
import net.congling.allinzero.entity.touling.boss.touling_congling.ToulingConglingEntity;
import net.congling.allinzero.entity.touling.boss.touling_congling.WraithEntity;
import net.congling.allinzero.entity.touling.boss.touling_congling.WraithModel;
import net.congling.allinzero.entity.touling.monster.phosphor.PhosphorBoltModel;
import net.congling.allinzero.entity.touling.monster.phosphor.PhosphorEntity;
import net.congling.allinzero.entity.touling.monster.phosphor.PhosphorModel;
import net.congling.allinzero.entity.touling.monster.wandering_wsip.WanderingWsipEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.SpawnPlacementTypes;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.neoforge.event.entity.RegisterSpawnPlacementsEvent;

@EventBusSubscriber(
        modid = Allinzero.MODID
)
public class AllinzeroEventBusEvent {
    @SubscribeEvent
    public static void registerLayers(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(WraithModel.LAYER_LOCATION, WraithModel::createBodyLayer);
        event.registerLayerDefinition(PhosphorModel.LAYER_LOCATION, PhosphorModel::createBodyLayer);
        event.registerLayerDefinition(PhosphorBoltModel.LAYER_LOCATION, PhosphorBoltModel::createBodyLayer);
    }

    @SubscribeEvent
    public static void registerAttributes(EntityAttributeCreationEvent event) {
        event.put((EntityType) AllinzeroEntity.TOULING_CONGLING.get(), ToulingConglingEntity.createAttributes().build());
        event.put((EntityType)AllinzeroEntity.SOUL_COW.get(), SoulCowEntity.createAttributes().build());
        event.put((EntityType)AllinzeroEntity.SOUL_SHEEP.get(), SoulSheepEntity.createAttributes().build());
        event.put((EntityType)AllinzeroEntity.SOUL_PIG.get(), SoulPigEntity.createAttributes().build());
        event.put((EntityType) AllinzeroEntity.PHOS_PHOR.get(), PhosphorEntity.createAttributes().build());
        event.put((EntityType) AllinzeroEntity.WRAITH.get(), WraithEntity.createAttributes().build());
        event.put((EntityType) AllinzeroEntity.SOUL_DRAIN_WITHER.get(), SoulDrainWitherEntity.createAttributes().build());
        event.put((EntityType) AllinzeroEntity.WANDERING_WSIP.get(), WanderingWsipEntity.createAttributes().build());
    }

    @SubscribeEvent
    public static void registerSpawnPlacements(RegisterSpawnPlacementsEvent event) {
        // 磷光体：无特殊限制的空中生成点，生成位置及上方一格须为空气
        event.register(AllinzeroEntity.PHOS_PHOR.get(), SpawnPlacementTypes.NO_RESTRICTIONS,
                Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                (EntityType<PhosphorEntity> type, ServerLevelAccessor level, MobSpawnType reason,
                 BlockPos pos, RandomSource random) ->
                        level.getBlockState(pos).isAir() && level.getBlockState(pos.above()).isAir(),
                RegisterSpawnPlacementsEvent.Operation.REPLACE);
        // 灵魂羊：地面生成，限定在灵魂草方块或灵魂石头上
        event.register(AllinzeroEntity.SOUL_SHEEP.get(), SpawnPlacementTypes.ON_GROUND,
                Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                (EntityType<SoulSheepEntity> type, ServerLevelAccessor level, MobSpawnType reason,
                 BlockPos pos, RandomSource random) ->
                        level.getBlockState(pos.below()).is(AllinzeroBlocks.SOUL_GRASS_BLOCK.get())
                                || level.getBlockState(pos.below()).is(AllinzeroBlocks.SOUL_STONE.get()),
                RegisterSpawnPlacementsEvent.Operation.REPLACE);
        // 灵魂猪：地面生成，限定在灵魂草方块或灵魂石头上
        event.register(AllinzeroEntity.SOUL_PIG.get(), SpawnPlacementTypes.ON_GROUND,
                Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                (EntityType<SoulPigEntity> type, ServerLevelAccessor level, MobSpawnType reason,
                 BlockPos pos, RandomSource random) ->
                        level.getBlockState(pos.below()).is(AllinzeroBlocks.SOUL_GRASS_BLOCK.get())
                                || level.getBlockState(pos.below()).is(AllinzeroBlocks.SOUL_STONE.get()),
                RegisterSpawnPlacementsEvent.Operation.REPLACE);
        // 游荡怨灵：地面生成，只站在透灵沙漠的灵魂沙上
        event.register(AllinzeroEntity.WANDERING_WSIP.get(), SpawnPlacementTypes.ON_GROUND,
                Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                (EntityType<WanderingWsipEntity> type, ServerLevelAccessor level, MobSpawnType reason,
                 BlockPos pos, RandomSource random) ->
                        level.getBlockState(pos.below()).is(AllinzeroBlocks.SOUL_SAND.get())
                                && level.getBlockState(pos).isAir()
                                && level.getBlockState(pos.above()).isAir(),
                RegisterSpawnPlacementsEvent.Operation.REPLACE);
    }
}
