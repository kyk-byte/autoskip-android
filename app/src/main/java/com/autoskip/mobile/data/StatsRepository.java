package com.autoskip.mobile.data;

import android.content.SharedPreferences;

public final class StatsRepository {
    public static final String KEY_SKIPPED_COUNT = "stats_skipped_count";
    public static final String KEY_ESTIMATED_SAVED_MS = "stats_estimated_saved_ms";
    public static final String KEY_LAST_SKIP_EPOCH_MS = "stats_last_skip_epoch_ms";
    public static final String KEY_YOUTUBE_SKIPPED_COUNT = "stats_youtube_skipped_count";
    public static final String KEY_TIKTOK_SKIPPED_COUNT = "stats_tiktok_skipped_count";

    private final SharedPreferences preferences;

    public StatsRepository(SharedPreferences preferences) {
        this.preferences = preferences;
    }

    public void recordSkip(Target target, long estimatedSavedMs, long epochMs) {
        long count = preferences.getLong(KEY_SKIPPED_COUNT, 0L);
        long saved = preferences.getLong(KEY_ESTIMATED_SAVED_MS, 0L);
        SharedPreferences.Editor editor = preferences.edit()
                .putLong(KEY_SKIPPED_COUNT, count + 1L)
                .putLong(KEY_ESTIMATED_SAVED_MS, saved + Math.max(0L, estimatedSavedMs))
                .putLong(KEY_LAST_SKIP_EPOCH_MS, epochMs);

        if (target == Target.TIKTOK) {
            long tikTokCount = preferences.getLong(KEY_TIKTOK_SKIPPED_COUNT, 0L);
            editor.putLong(KEY_TIKTOK_SKIPPED_COUNT, tikTokCount + 1L);
        } else {
            long youtubeCount = migratedYouTubeCount(count);
            editor.putLong(KEY_YOUTUBE_SKIPPED_COUNT, youtubeCount + 1L);
        }
        editor.apply();
    }

    public Snapshot snapshot() {
        long totalCount = preferences.getLong(KEY_SKIPPED_COUNT, 0L);
        return new Snapshot(
                totalCount,
                migratedYouTubeCount(totalCount),
                preferences.getLong(KEY_TIKTOK_SKIPPED_COUNT, 0L),
                preferences.getLong(KEY_ESTIMATED_SAVED_MS, 0L),
                preferences.getLong(KEY_LAST_SKIP_EPOCH_MS, 0L)
        );
    }

    public void reset() {
        preferences.edit()
                .remove(KEY_SKIPPED_COUNT)
                .remove(KEY_ESTIMATED_SAVED_MS)
                .remove(KEY_LAST_SKIP_EPOCH_MS)
                .remove(KEY_YOUTUBE_SKIPPED_COUNT)
                .remove(KEY_TIKTOK_SKIPPED_COUNT)
                .apply();
    }

    private long migratedYouTubeCount(long totalCount) {
        if (preferences.contains(KEY_YOUTUBE_SKIPPED_COUNT)) {
            return preferences.getLong(KEY_YOUTUBE_SKIPPED_COUNT, 0L);
        }
        return Math.max(0L, totalCount - preferences.getLong(KEY_TIKTOK_SKIPPED_COUNT, 0L));
    }

    public enum Target {
        YOUTUBE,
        TIKTOK
    }

    public static final class Snapshot {
        private final long skippedCount;
        private final long youtubeSkippedCount;
        private final long tikTokSkippedCount;
        private final long estimatedSavedMs;
        private final long lastSkipEpochMs;

        public Snapshot(
                long skippedCount,
                long youtubeSkippedCount,
                long tikTokSkippedCount,
                long estimatedSavedMs,
                long lastSkipEpochMs
        ) {
            this.skippedCount = skippedCount;
            this.youtubeSkippedCount = youtubeSkippedCount;
            this.tikTokSkippedCount = tikTokSkippedCount;
            this.estimatedSavedMs = estimatedSavedMs;
            this.lastSkipEpochMs = lastSkipEpochMs;
        }

        public long skippedCount() {
            return skippedCount;
        }

        public long estimatedSavedMs() {
            return estimatedSavedMs;
        }

        public long youtubeSkippedCount() {
            return youtubeSkippedCount;
        }

        public long tikTokSkippedCount() {
            return tikTokSkippedCount;
        }

        public long lastSkipEpochMs() {
            return lastSkipEpochMs;
        }
    }
}
