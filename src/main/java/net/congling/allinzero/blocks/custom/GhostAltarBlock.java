package net.congling.allinzero.blocks.custom;

import net.congling.allinzero.entity.AllinzeroEntity;
import net.congling.allinzero.entity.touling.boss.touling_congling.ToulingConglingEntity;
import net.congling.allinzero.items.AllinzeroItems;
import net.congling.allinzero.worldgen.AllinzeroBiomes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.TickTask;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Marker;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.InteractionResult;

import java.util.List;

/**
 * 幽冥祭坛：右击后祭坛先喷出大量灵魂火粒子，第 3 秒（60t）时生成透灵从零。
 * 消耗材料为灵魂晶体（soul_crystal）或灵魂水晶（soul_spar）。
 */
public class GhostAltarBlock extends Block {

    /** 同维度此半径内已存在透灵从零时，不允许重复召唤 */
    private static final double ALREADY_SUMMONED_RADIUS = 96.0;
    /** 统计挑战者（非旁观玩家）的半径：2 名及以上 → 强化版血量 8000 */
    private static final double CHALLENGER_RADIUS = 64.0;
    /** 召唤延迟：3 秒 */
    private static final int SUMMON_DELAY_TICKS = 60;
    /** 标记实体（Marker）标签：用于防止 3 秒等待期内重复触发 */
    private static final String PENDING_TAG = "allinzero_altar_pending";

    public GhostAltarBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player,
                                               BlockHitResult hitResult) {
        if (level.isClientSide) {
            // 背包内有灵魂晶体/灵魂水晶则播放挥手动作
            return (findSummonItem(player).isEmpty()) ? InteractionResult.PASS : InteractionResult.SUCCESS;
        }

        ItemStack crystal = findSummonItem(player);
        if (crystal.isEmpty()) {
            player.displayClientMessage(
                    Component.translatable("message.allinzero.ghost_altar.need_crystal"), true);
            level.playSound(null, pos, SoundEvents.DISPENSER_FAIL, SoundSource.BLOCKS, 0.8F, 0.8F);
            return InteractionResult.FAIL;
        }

        if (bossAlreadyPresent(level, pos)) {
            player.displayClientMessage(
                    Component.translatable("message.allinzero.ghost_altar.already_summoned"), true);
            level.playSound(null, pos, SoundEvents.DISPENSER_FAIL, SoundSource.BLOCKS, 0.8F, 1.2F);
            return InteractionResult.FAIL;
        }

        if (pendingAlreadyPresent(level, pos)) {
            // 3 秒等待期内不允许重复触发
            level.playSound(null, pos, SoundEvents.DISPENSER_FAIL, SoundSource.BLOCKS, 0.8F, 1.0F);
            return InteractionResult.FAIL;
        }

        ServerLevel serverLevel = (ServerLevel) level;

        // 召唤成功才消耗材料
        crystal.shrink(1);

        // 放置等待期标记
        Marker marker = EntityType.MARKER.create(serverLevel);
        if (marker != null) {
            marker.moveTo(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, 0.0F, 0.0F);
            marker.addTag(PENDING_TAG);
            serverLevel.addFreshEntity(marker);
        }

        Component summonerName = player.getDisplayName();
        // 灵魂晶体 → 强化版；灵魂水晶仅在 2+ 玩家于第 3 秒到场时才升级
        boolean crystalEnhanced = crystal.is(AllinzeroItems.SOUL_CRYSTAL.get());

        // 祭坛立刻喷出大量灵魂火粒子
        this.playInitialBurst(serverLevel, pos);

        // 等待期内的粒子脉冲
        serverLevel.getServer().tell(new TickTask(
                serverLevel.getServer().getTickCount() + 20, () -> this.playPulse(serverLevel, pos)));
        serverLevel.getServer().tell(new TickTask(
                serverLevel.getServer().getTickCount() + 40, () -> this.playPulse(serverLevel, pos)));

        // 第 3 秒：生成 BOSS
        serverLevel.getServer().tell(new TickTask(
                serverLevel.getServer().getTickCount() + SUMMON_DELAY_TICKS,
                () -> this.executeSummon(serverLevel, pos, player, summonerName, crystalEnhanced)));

        return InteractionResult.SUCCESS;
    }

    /** 第 3 秒执行：校验状态后生成透灵从零 */
    private void executeSummon(ServerLevel level, BlockPos pos, Player player, Component summonerName,
                               boolean crystalEnhanced) {
        Marker marker = this.findPendingMarker(level, pos);

        // 祭坛已被破坏：取消召唤
        if (!level.getBlockState(pos).is(this)) {
            if (marker != null) {
                marker.discard();
            }
            return;
        }
        // 已经有 BOSS：取消
        if (this.bossAlreadyPresent(level, pos)) {
            if (marker != null) {
                marker.discard();
            }
            return;
        }

        // 统计祭坛周围 64 格内的挑战者（非旁观玩家）
        int challengers = this.countChallengers(level, pos);
        boolean manyChallengers = challengers >= 2;
        boolean enhanced = crystalEnhanced || manyChallengers;

        ToulingConglingEntity boss = this.summonBoss(level, pos, player, enhanced, manyChallengers);
        if (boss == null) {
            if (marker != null) {
                marker.discard();
            }
            return;
        }
        if (marker != null) {
            marker.discard();
        }

        this.playSummonEffects(level, pos);
        if (player != null && !player.isRemoved()) {
            player.displayClientMessage(Component.translatable(enhanced
                    ? "message.allinzero.ghost_altar.summoned_enhanced"
                    : "message.allinzero.ghost_altar.summoned"), true);
        }
        // 全服消息栏广播召唤宣言
        level.getServer().getPlayerList().broadcastSystemMessage(
                Component.translatable("message.allinzero.touling_summoned", summonerName), false);
    }

    /** 统计祭坛周围 CHALLENGER_RADIUS 内非旁观模式的玩家数 */
    private int countChallengers(ServerLevel level, BlockPos pos) {
        AABB area = AABB.ofSize(Vec3.atCenterOf(pos),
                CHALLENGER_RADIUS, CHALLENGER_RADIUS, CHALLENGER_RADIUS);
        return level.getEntitiesOfClass(Player.class, area,
                p -> p.isAlive() && !p.isSpectator()).size();
    }

    /** 寻找灵魂晶体或灵魂水晶 */
    private static ItemStack findSummonItem(Player player) {
        for (ItemStack stack : player.getInventory().items) {
            if (stack.is(AllinzeroItems.SOUL_CRYSTAL.get())) {
                return stack;
            }
        }
        for (ItemStack stack : player.getInventory().items) {
            if (stack.is(AllinzeroItems.SOUL_SPAR.get())) {
                return stack;
            }
        }
        return ItemStack.EMPTY;
    }

    private boolean bossAlreadyPresent(Level level, BlockPos pos) {
        AABB area = AABB.ofSize(Vec3.atCenterOf(pos),
                ALREADY_SUMMONED_RADIUS, ALREADY_SUMMONED_RADIUS, ALREADY_SUMMONED_RADIUS);
        List<ToulingConglingEntity> bosses =
                level.getEntitiesOfClass(ToulingConglingEntity.class, area, ToulingConglingEntity::isAlive);
        return !bosses.isEmpty();
    }

    private boolean pendingAlreadyPresent(Level level, BlockPos pos) {
        return this.findPendingMarker(level, pos) != null;
    }

    private Marker findPendingMarker(Level level, BlockPos pos) {
        AABB area = AABB.ofSize(Vec3.atCenterOf(pos), 2.0, 2.0, 2.0);
        List<Marker> markers = level.getEntitiesOfClass(Marker.class, area,
                m -> m.getTags().contains(PENDING_TAG));
        return markers.isEmpty() ? null : markers.get(0);
    }

    private ToulingConglingEntity summonBoss(ServerLevel level, BlockPos pos, Player player,
                                             boolean enhanced, boolean manyChallengers) {
        ToulingConglingEntity boss = AllinzeroEntity.TOULING_CONGLING.get().create(level);
        if (boss == null) {
            return null;
        }
        double x = pos.getX() + 0.5;
        double y = pos.getY() + 1.0;
        double z = pos.getZ() + 0.5;
        // 面向玩家（玩家已离线则保持默认朝向）
        float yRot = (player == null || player.isRemoved())
                ? 0.0F
                : (float) (Math.toDegrees(Math.atan2(x - player.getX(), z - player.getZ())));
        boss.moveTo(x, y, z, yRot, 0.0F);
        // 强化版锁定：护甲/韧性按 enhanced；血量 8000 仅当 2 名及以上挑战者（须在入世界前）
        if (enhanced) {
            boss.setEnhancedMode(true, manyChallengers);
        }
        boss.finalizeSpawn(level, level.getCurrentDifficultyAt(pos), MobSpawnType.TRIGGERED, null);
        // 按祭坛所在群系附加永久增益（时长 -1 = 无限）
        if (level.getBiome(pos).is(AllinzeroBiomes.TOULING_FOREST)) {
            // 透灵森林：抗性提升 II + 生命恢复 III
            boss.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, -1, 1, false, true));
            boss.addEffect(new MobEffectInstance(MobEffects.REGENERATION, -1, 2, false, true));
        } else if (level.getBiome(pos).is(AllinzeroBiomes.TOULING_DESERT)) {
            // 透灵沙漠：力量 V
            boss.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, -1, 4, false, true));
        }
        level.addFreshEntity(boss);
        return boss;
    }

    /** 触发瞬间：大量灵魂火粒子喷涌 */
    private void playInitialBurst(ServerLevel level, BlockPos pos) {
        level.playSound(null, pos, SoundEvents.EVOKER_PREPARE_SUMMON, SoundSource.HOSTILE, 1.2F, 0.7F);

        double cx = pos.getX() + 0.5;
        double cy = pos.getY() + 1.0;
        double cz = pos.getZ() + 0.5;
        level.sendParticles(ParticleTypes.SOUL_FIRE_FLAME, cx, pos.getY() + 0.3, cz,
                120, 0.7, 0.5, 0.7, 0.04);
        level.sendParticles(ParticleTypes.SCULK_SOUL, cx, cy, cz, 60, 0.8, 0.8, 0.8, 0.03);

        // 上升的灵魂环
        for (int i = 0; i < 24; i++) {
            double angle = Math.PI * 2.0 * i / 24.0;
            level.sendParticles(ParticleTypes.SOUL,
                    cx + Math.cos(angle) * 1.2, pos.getY() + 0.2, cz + Math.sin(angle) * 1.2,
                    1, 0.0, 0.08, 0.0, 0.0);
        }
    }

    /** 等待期粒子脉冲 */
    private void playPulse(ServerLevel level, BlockPos pos) {
        double cx = pos.getX() + 0.5;
        double cz = pos.getZ() + 0.5;
        level.sendParticles(ParticleTypes.SOUL_FIRE_FLAME, cx, pos.getY() + 0.4, cz,
                40, 0.6, 0.6, 0.6, 0.05);
        level.sendParticles(ParticleTypes.SCULK_SOUL, cx, pos.getY() + 1.0, cz,
                20, 0.5, 0.5, 0.5, 0.02);
        level.playSound(null, pos, SoundEvents.BEACON_AMBIENT, SoundSource.BLOCKS, 0.8F, 0.6F);
    }

    private void playSummonEffects(ServerLevel level, BlockPos pos) {
        level.playSound(null, pos, SoundEvents.BEACON_ACTIVATE, SoundSource.BLOCKS, 1.0F, 0.6F);

        double cx = pos.getX() + 0.5;
        double cy = pos.getY() + 1.0;
        double cz = pos.getZ() + 0.5;
        level.sendParticles(ParticleTypes.SOUL_FIRE_FLAME, cx, cy, cz, 100, 0.9, 1.0, 0.9, 0.05);
        level.sendParticles(ParticleTypes.SCULK_SOUL, cx, cy, cz, 80, 0.8, 0.8, 0.8, 0.03);
    }
}
