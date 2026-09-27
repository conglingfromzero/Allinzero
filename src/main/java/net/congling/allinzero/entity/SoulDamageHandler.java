package net.congling.allinzero.entity;

import net.congling.allinzero.Allinzero;
import net.congling.allinzero.effect.AllinzeroDamageTypes;
import net.congling.allinzero.entity.touling.boss.touling_congling.SoulDrainWitherEntity;
import net.congling.allinzero.event.ToulingEnvironmentEvents;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;

/**
 * 灵魂生物的火焰易伤机制（灵魂代偿）：
 * - 受到火焰或岩浆伤害：受伤 +100%（×2）
 * - 被火焰附加 I 武器命中：受伤 +200%（×3）
 * - 被火焰附加 II（及以上）武器命中：受伤 +350%（×4.5）
 * - 被附魔火矢的弓箭命中：受伤 +200%（×3）
 * 在护甲减免之后、最终扣血之前按倍率放大伤害。
 *
 * 强化版透灵从零召唤的削弱凋零（SoulDrainWitherEntity）：出伤统一 -20%（×0.8）。
 *
 * 幽冥/灵魂全套盔甲（玩家）：
 * - 受到灵魂生物类型生物的攻击：减伤 75%（×0.25）
 * - 受到灵魂侵蚀伤害：减伤 75%（×0.25）
 */
@EventBusSubscriber(modid = Allinzero.MODID)
public class SoulDamageHandler {

    @SubscribeEvent
    public static void onLivingDamage(LivingDamageEvent.Pre event) {
        LivingEntity target = event.getEntity();
        DamageSource source = event.getSource();

        float mult = 1.0F;
        if (SoulEntityTypes.isSoul(target)) {
            // 火焰/岩浆类伤害源（着火持续、岩浆、火球、营火等）
            if (source.is(DamageTypeTags.IS_FIRE)) {
                mult = 2.0F;
            }

            // 火焰附加近战：直接攻击、非弹射物，且攻击者主手武器带任意等级火焰附加 → +200%
            if (mult == 1.0F
                    && source.getEntity() instanceof LivingEntity attacker
                    && source.getDirectEntity() == attacker
                    && (source.is(DamageTypes.PLAYER_ATTACK) || source.is(DamageTypes.MOB_ATTACK))) {
                if (!attacker.getMainHandItem().isEmpty()) {
                    int faLevel = getLevel(attacker, Enchantments.FIRE_ASPECT);
                    if (faLevel >= 2) {
                        mult = 4.5F;
                    } else if (faLevel == 1) {
                        mult = 3.0F;
                    }
                }
            }

            // 火矢弓箭：箭矢在燃烧（火矢射出的箭会被点燃），或射手主手弓带火矢
            if (mult == 1.0F && source.getDirectEntity() instanceof AbstractArrow arrow) {
                if (arrow.isOnFire()
                        || (source.getEntity() instanceof LivingEntity shooter
                            && !shooter.getMainHandItem().isEmpty()
                            && getLevel(shooter, Enchantments.FLAME) > 0)) {
                    mult = 3.0F;
                }
            }
        } else if (target instanceof Player player) {
            // 幽冥/灵魂全套盔甲：对灵魂生物攻击与灵魂侵蚀伤害减伤 75%
            if (ToulingEnvironmentEvents.wearsFullGhostSet(player)
                    || ToulingEnvironmentEvents.wearsFullSoulSet(player)) {
                if (source.is(AllinzeroDamageTypes.SOUL_EROSION)) {
                    mult = 0.25F;
                } else if (source.getEntity() instanceof LivingEntity attacker
                        && SoulEntityTypes.isSoul(attacker)) {
                    mult = 0.25F;
                }
            }
        }

        // 削弱凋零：其造成的一切伤害统一 -20%（直接头颅命中、爆炸等）
        if (source.getEntity() instanceof SoulDrainWitherEntity) {
            mult *= 0.8F;
        }

        if (mult != 1.0F) {
            event.setNewDamage(event.getNewDamage() * mult);
        }
    }

    private static int getLevel(LivingEntity entity, net.minecraft.resources.ResourceKey<Enchantment> key) {
        Holder<Enchantment> holder = entity.level().registryAccess()
                .lookupOrThrow(Registries.ENCHANTMENT).getOrThrow(key);
        return entity.getMainHandItem().getEnchantmentLevel(holder);
    }
}
