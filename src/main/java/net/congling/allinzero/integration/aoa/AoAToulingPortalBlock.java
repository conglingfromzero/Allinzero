package net.congling.allinzero.integration.aoa;

import net.congling.allinzero.worldgen.AllinzeroDimensions;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.tslat.aoa3.common.registration.block.AoABlocks;
import net.tslat.aoa3.content.block.functional.portal.PortalBlock;
import net.tslat.aoa3.util.ColourUtil;

/**
 * 虚无世界3 联动：透灵传送门方块。
 * 由透灵纯宝石在远古岩石框架（外框 5×6）上点亮，行为与 AoA3 各维度传送门一致，
 * 走入即可在透灵维度与主世界之间往返。
 */
public class AoAToulingPortalBlock extends PortalBlock {
    public AoAToulingPortalBlock(Properties properties) {
        // 透灵主题青蓝色粒子，环境音沿用原版传送门嗡鸣
        super(properties, AllinzeroDimensions.TOULING_LEVEL, ColourUtil.RGB(64, 196, 226), () -> SoundEvents.PORTAL_AMBIENT);
    }

    /**
     * 传送门框架方块：AoA3 统一为远古岩石。
     */
    @Override
    public Block getPortalFrame() {
        return AoABlocks.ANCIENT_ROCK.get();
    }

    /**
     * AoA3 的 entityInside 内置 USEABLE_PORTALS 白名单（仅含其原版 11 个维度传送门），
     * 联动传送门必须重写此方法，否则玩家走入传送门不会触发传送。
     */
    @Override
    public void entityInside(BlockState state, Level level, BlockPos pos, Entity entity) {
        if (entity.canUsePortal(false)) {
            entity.setAsInsidePortal(this, pos);
        }
    }
}
