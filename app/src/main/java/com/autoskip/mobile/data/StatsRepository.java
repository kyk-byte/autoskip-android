package com.autoskip.mobile.data;

import android.content.SharedPreferences;

public final class StatsRepository {
    public static final String KEY_SKIPPED_COUNT = "stats_skipped_count";
    public static final String KEY_ESTIMATED_SAVED_MS = "stats_estimated_saved_ms";
    public static final String KEY_LAST_SKIP_EPOCH_MS = "stats_last_skip_epoch_ms";

    private final SharedPreferences preferences;

    public StatsRepository(SharedPreferences preferences) {
        this.preferences = preferences;
    }

    public void recordSkip(long estimatedSavedMs, long epochMs) {
        long count = preferences.getLong(KEY_SKIPPED_COUNT, 0L);
        long saved = preferences.getLong(KEY_ESTIMATED_SAVED_MS, 0L);
        preferences.edit()
                .putLong(KEY_SKIPPED_COUNT, count + 1L)
                .putLong(KEY_ESTIMATED_SAVED_MS, saved + Math.max(0L, estimatedSavedMs))
                .putLong(KEY_LAST_SKIP_EPOCH_MS, epochMs)
                .apply();
    }

    public Snapshot snapshot() {
        return new Snapshot(
                preferences.getLong(KEY_SKIPPED_COUNT, 0L),
                preferences.getLong(KEY_ESTIMATED_SAVED_MS, 0L),
                preferences.getLong(KEY_LAST_SKIP_EPOCH_MS, 0L)
        );
    }

    public void reset() {
        preferences.edit()
                .remove(KEY_SKIPPED_COUNT)
                .remove(KEY_ESTIMATED_SAVED_MS)
                .remove(KEY_LAST_SKIP_EPOCH_MS)
                .apply();
    }

    public static final class Snapshot {
        private final long skippedCount;
        private final long estimatedSavedMs;
        private final long lastSkipEpochMs;

        public Snapshot(long skippedCount, long estimatedSavedMs, long lastSkipEpochMs) {
            this.skippedCount = skippedCount;
            this.estimatedSavedMs = estimatedSavedMs;
            this.lastSkipEpochMs = lastSkipEpochMs;
        }

        public long skippedCount() {
            return skippedCount;
        }

        public long estimatedSavedMs() {
            return estimatedSavedMs;
        }

        public long lastSkipEpochMs() {
            return lastSkipEpochMs;
        }
    }
}
