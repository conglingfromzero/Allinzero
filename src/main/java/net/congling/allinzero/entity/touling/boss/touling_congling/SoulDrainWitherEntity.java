package net.congling.allinzero.entity.touling.boss.touling_congling;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.boss.wither.WitherBoss;
import net.minecraft.world.level.Level;

import java.util.UUID;

/**
 * 强化版透灵从零召唤的凋零：全方位数值削弱 20%。
 * <ul>
 *     <li>生命 300×0.8 = 240；护甲 4×0.8 = 3.2；</li>
 *     <li>移动/飞行速度 0.6×0.8 = 0.48；索敌范围 40×0.8 = 32；</li>
 *     <li>造成的伤害由 SoulDamageHandler 统一 ×0.8（头颅爆炸/接触）。</li>
 * </ul>
 * 该实体由祭坛召唤的 BOSS 直接 addFreshEntity 生成（不经凋零骷髅头颅放置流程），
 * 因此没有原版 220t 出场无敌与第 11 秒的爆炸；owner 以持久数据记录，供 BOSS 死亡清理。
 */
public class SoulDrainWitherEntity extends WitherBoss {
    /** ForgeData 中记录主人 UUID 的键 */
    public static final String OWNER_KEY = "SoulDrainWitherOwner";

    public SoulDrainWitherEntity(EntityType<? extends SoulDrainWitherEntity> type, Level level) {
        super(type, level);
        this.xpReward = 0;
        this.setPersistenceRequired();
    }

    public static AttributeSupplier.Builder createAttributes() {
        return WitherBoss.createAttributes()
                .add(Attributes.MAX_HEALTH, 240.0)
                .add(Attributes.MOVEMENT_SPEED, 0.48)
                .add(Attributes.FLYING_SPEED, 0.48)
                .add(Attributes.FOLLOW_RANGE, 32.0)
                .add(Attributes.ARMOR, 3.2);
    }

    /** 绑定主人（透灵从零）UUID */
    public void bindOwner(UUID ownerUuid) {
        this.getPersistentData().putUUID(OWNER_KEY, ownerUuid);
    }

    /** 是否属于指定主人 */
    public boolean isOwnedBy(UUID ownerUuid) {
        return this.getPersistentData().hasUUID(OWNER_KEY)
                && this.getPersistentData().getUUID(OWNER_KEY).equals(ownerUuid);
    }

    // 屏蔽继承自凋零的独立 Boss 血条：召唤物不应各显一条紫血条

    @Override
    public void startSeenByPlayer(ServerPlayer player) {
    }

    @Override
    public void stopSeenByPlayer(ServerPlayer player) {
    }
}
