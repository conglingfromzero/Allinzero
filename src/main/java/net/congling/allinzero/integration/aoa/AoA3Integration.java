package net.congling.allinzero.integration.aoa;

import net.congling.allinzero.Allinzero;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.PushReaction;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

/**
 * AoA3（虚无世界3）条件联动注册。
 * <p>
 * 本类仅在 aoa3 已加载时由主类调用 {@link #init(IEventBus)}。
 * AoA3 缺失时本类不会被任何代码引用，JVM 不会加载/校验本类，
 * 因此不会出现 NoClassDefFoundError，模组本体可正常运行。
 */
public class AoA3Integration {
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(Allinzero.MODID);
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(Allinzero.MODID);

    /**
     * 透灵传送门方块。属性对齐 AoA3 BlockRegistrar.basePortal()：
     * 无碰撞箱、不可破坏、玻璃音效、亮度 11、活塞不可推动、无掉落表，
     * 另外显式 noOcclusion 防止渲染剔除问题。
     */
    public static final DeferredBlock<Block> TOULING_PORTAL = BLOCKS.register("aoa_touling_portal",
            () -> new AoAToulingPortalBlock(BlockBehaviour.Properties.of()
                    .noCollission()
                    .strength(-1.0F, 1.0E9F)
                    .sound(SoundType.GLASS)
                    .lightLevel(state -> 11)
                    .pushReaction(PushReaction.BLOCK)
                    .isViewBlocking((state, level, pos) -> false)
                    .noLootTable()
                    .noOcclusion()));

    /**
     * 透灵纯宝石。
     */
    public static final DeferredItem<ToulingRealmstoneItem> TOULING_REALMSTONE = ITEMS.register("touling_realmstone",
            () -> new ToulingRealmstoneItem(TOULING_PORTAL));

    public static void init(IEventBus modEventBus) {
        BLOCKS.register(modEventBus);
        ITEMS.register(modEventBus);
    }
}
