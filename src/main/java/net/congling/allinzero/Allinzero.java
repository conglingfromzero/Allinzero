package net.congling.allinzero;

import net.congling.allinzero.blocks.AllinzeroBlocks;
import net.congling.allinzero.effect.AllinzeroEffects;
import net.congling.allinzero.entity.AllinzeroEntity;
import net.congling.allinzero.items.AllinzeroCreativeTabs;
import net.congling.allinzero.items.AllinzeroItems;
import net.congling.allinzero.loot.AllinzeroLootModifiers;
import net.congling.allinzero.particles.AllinzeroParticles;
import net.congling.allinzero.sound.AllinzeroSounds;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.event.server.ServerStartingEvent;

@Mod(Allinzero.MODID)
public class Allinzero {
    public static final String MODID = "allinzero";

    public Allinzero(IEventBus modEventBus, ModContainer modContainer) {

        modEventBus.addListener(this::commonSetup);

        AllinzeroBlocks.register(modEventBus);
        AllinzeroItems.register(modEventBus);
        AllinzeroCreativeTabs.register(modEventBus);
        AllinzeroEntity.register(modEventBus);
        AllinzeroLootModifiers.register(modEventBus);
        AllinzeroParticles.register(modEventBus);
        AllinzeroEffects.register(modEventBus);
        AllinzeroSounds.register(modEventBus);

        // 仅在虚无世界3（AoA3）已安装时启用联动：透灵纯宝石 + 透灵远古传送门 + 纯宝石任务
        if (net.neoforged.fml.ModList.get().isLoaded("aoa3")) {
            net.congling.allinzero.integration.aoa.AoA3Integration.init(modEventBus);
            NeoForge.EVENT_BUS.register(new net.congling.allinzero.integration.aoa.AoARealmstoneTaskHandler());
        }

        NeoForge.EVENT_BUS.register(this);

        modEventBus.addListener(this::addCreative);

        modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }

    private void commonSetup(FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            // 把自定义灵魂床加入原版床方块实体类型的合法方块集合，修复放置时 BedBlockEntity 校验崩溃
            net.congling.allinzero.mixin.BlockEntityTypeAccessor bedType =
                    (net.congling.allinzero.mixin.BlockEntityTypeAccessor) (Object)
                            net.minecraft.world.level.block.entity.BlockEntityType.BED;
            java.util.Set<net.minecraft.world.level.block.Block> validBeds =
                    new java.util.HashSet<>(bedType.allinzero$getValidBlocks());
            validBeds.add(AllinzeroBlocks.SOUL_BED.get());
            bedType.allinzero$setValidBlocks(validBeds);
        });
    }

    private void addCreative(BuildCreativeModeTabContentsEvent event) {
    }

    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
    }

}
