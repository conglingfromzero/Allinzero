package net.congling.allinzero.items.custom;

import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.ItemStack;

/**
 * 可自定义箭矢基础伤害的弓。
 * 1.21.1 箭矢命中伤害 = baseDamage × 箭矢速度（满弓箭速 3.0），
 * 原版弓 baseDamage 为 2.0（满伤 6）。
 * 通过在 customArrow 中改写 baseDamage 实现更高满伤：
 * 幽灵弓 5.0 → 满伤 15；灵魂弓 7.5 → 满伤 22.5。
 */
public class AllinzeroBowItem extends BowItem {
    private final double arrowBaseDamage;

    public AllinzeroBowItem(double arrowBaseDamage, Properties properties) {
        super(properties);
        this.arrowBaseDamage = arrowBaseDamage;
    }

    @Override
    public AbstractArrow customArrow(AbstractArrow arrow, ItemStack projectileStack, ItemStack weaponStack) {
        arrow.setBaseDamage(this.arrowBaseDamage);
        return arrow;
    }
}
