package com.autoskip.mobile.data;

import android.content.Context;
import android.content.SharedPreferences;

public final class AppPreferences {
    public static final String FILE_NAME = "autoskip_preferences";
    public static final String KEY_ENABLED = "auto_skip_enabled";
    public static final String KEY_YOUTUBE = "target_youtube";
    public static final String KEY_TIKTOK = "target_tiktok";
    public static final String KEY_DELAY_MS = "detection_delay_ms";
    public static final String KEY_TIKTOK_DELAY_MS = "tiktok_detection_delay_ms";

    public static final int DEFAULT_DELAY_MS = 200;
    public static final int DEFAULT_TIKTOK_DELAY_MS = 500;

    private AppPreferences() {
    }

    public static SharedPreferences from(Context context) {
        return context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
    }

    public static boolean isEnabled(SharedPreferences preferences) {
        return preferences.getBoolean(KEY_ENABLED, true);
    }

    public static boolean isYouTubeEnabled(SharedPreferences preferences) {
        return preferences.getBoolean(KEY_YOUTUBE, true);
    }

    public static boolean isTikTokEnabled(SharedPreferences preferences) {
        return preferences.getBoolean(KEY_TIKTOK, false);
    }

    public static int detectionDelayMs(SharedPreferences preferences) {
        return clampedDelay(preferences.getInt(KEY_DELAY_MS, DEFAULT_DELAY_MS));
    }

    public static int tikTokDetectionDelayMs(SharedPreferences preferences) {
        return clampedDelay(preferences.getInt(
                KEY_TIKTOK_DELAY_MS,
                DEFAULT_TIKTOK_DELAY_MS
        ));
    }

    private static int clampedDelay(int stored) {
        int clamped = Math.max(0, Math.min(1000, stored));
        return Math.round(clamped / 100f) * 100;
    }
}
