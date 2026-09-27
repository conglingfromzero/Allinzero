package net.congling.allinzero.integration.aoa;

import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.tslat.aoa3.content.item.misc.Realmstone;

/**
 * 透灵纯宝石：在 AoA3 远古传送门框架的力量雕刻符文上使用，
 * 可点亮通往透灵维度的传送门，功能与 AoA3 所有纯宝石一致。
 * <p>
 * 必须继承 AoA3 的 Realmstone —— CarvedRuneOfPower 通过
 * {@code stack.getItem() instanceof Realmstone} 判定能否激活框架。
 */
public class ToulingRealmstoneItem extends Realmstone {
    public ToulingRealmstoneItem(DeferredBlock<Block> portalBlock) {
        super(portalBlock, "touling");
    }
}
