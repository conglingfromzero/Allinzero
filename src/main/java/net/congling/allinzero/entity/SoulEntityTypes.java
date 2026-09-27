package net.congling.allinzero.entity;

import net.congling.allinzero.entity.touling.animal.soulcow.SoulCowEntity;
import net.congling.allinzero.entity.touling.animal.soulpig.SoulPigEntity;
import net.congling.allinzero.entity.touling.animal.soulsheep.SoulSheepEntity;
import net.congling.allinzero.entity.touling.boss.touling_congling.ToulingConglingEntity;
import net.congling.allinzero.entity.touling.boss.touling_congling.WraithEntity;
import net.congling.allinzero.entity.touling.monster.phosphor.PhosphorEntity;
import net.congling.allinzero.entity.touling.monster.wandering_wsip.WanderingWsipEntity;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.world.entity.Entity;

/**
 * 灵魂生物类型判定。
 * 归入该类型：原版全部亡灵生物（僵尸/骷髅/幻翼/凋灵等，以原版
 * sensitive_to_smite 亡灵标签判定）以及本模组的灵魂牛、灵魂猪、
 * 灵魂羊、磷光体、怨灵、透灵从零，
 * 还有 AOA3 联动的幽灵（Ghost）与虚空步行者（Void Walker）。
 * 该类型的生物受到火焰易伤加成（见 SoulDamageHandler）。
 */
public final class SoulEntityTypes {
    private SoulEntityTypes() {
    }

    public static boolean isSoul(Entity entity) {
        // 原版亡灵系（含凋灵）
        if (entity.getType().is(EntityTypeTags.SENSITIVE_TO_SMITE)) {
            return true;
        }
        // 本模组灵魂生物
        return entity instanceof SoulCowEntity
                || entity instanceof SoulPigEntity
                || entity instanceof SoulSheepEntity
                || entity instanceof PhosphorEntity
                || entity instanceof WraithEntity
                || entity instanceof WanderingWsipEntity
                || entity instanceof ToulingConglingEntity
                // AOA3 联动生物（可选依赖，仅在已安装时判定）
                || isAoA3Soul(entity);
    }

    /**
     * AOA3（虚无世界3）联动灵魂生物：幽灵、虚空步行者。
     */
    private static boolean isAoA3Soul(Entity entity) {
        if (!net.neoforged.fml.ModList.get().isLoaded("aoa3")) {
            return false;
        }
        return entity instanceof net.tslat.aoa3.content.entity.monster.overworld.GhostEntity
                || entity instanceof net.tslat.aoa3.content.entity.monster.overworld.VoidWalkerEntity;
    }
}
