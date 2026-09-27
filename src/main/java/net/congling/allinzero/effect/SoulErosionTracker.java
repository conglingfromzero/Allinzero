package net.congling.allinzero.effect;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class SoulErosionTracker {

    public static final int TICKS_PER_LEVEL = 20 * 30;
    public static final int MAX_LEVEL = 3;
    public static final int IMMUNITY_TICKS = 20 * 50;

    private static final Map<UUID, Data> PLAYERS = new ConcurrentHashMap<>();

    private SoulErosionTracker() {
    }

    public static Data get(UUID playerId) {
        return PLAYERS.computeIfAbsent(playerId, k -> new Data());
    }

    public static void remove(UUID playerId) {
        PLAYERS.remove(playerId);
    }

    public static void applyImmunity(UUID playerId, int ticks) {
        Data data = get(playerId);
        data.immunityTicks = ticks;
        data.exposedTicks = 0;
    }

    public static int levelForExposure(int exposedTicks) {
        return Math.min(MAX_LEVEL, exposedTicks / TICKS_PER_LEVEL);
    }

    public static final class Data {
        private int exposedTicks = 0;
        private int immunityTicks = 0;

        public int getExposedTicks() {
            return exposedTicks;
        }

        public void incrementExposure() {
            this.exposedTicks++;
        }

        public void resetExposure() {
            this.exposedTicks = 0;
        }

        public int getImmunityTicks() {
            return immunityTicks;
        }

        public boolean isImmune() {
            return immunityTicks > 0;
        }

        public void decrementImmunity() {
            if (immunityTicks > 0) {
                immunityTicks--;
            }
        }
    }
}
