package com.autoskip.mobile.data;

import android.content.Context;
import android.content.SharedPreferences;

public final class AppPreferences {
    public static final String FILE_NAME = "autoskip_preferences";
    public static final String KEY_ENABLED = "auto_skip_enabled";
    public static final String KEY_YOUTUBE = "target_youtube";
    public static final String KEY_YOUTUBE_MUSIC = "target_youtube_music";
    public static final String KEY_DELAY_MS = "detection_delay_ms";

    public static final int DEFAULT_DELAY_MS = 200;

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

    public static boolean isYouTubeMusicEnabled(SharedPreferences preferences) {
        return preferences.getBoolean(KEY_YOUTUBE_MUSIC, false);
    }

    public static int detectionDelayMs(SharedPreferences preferences) {
        int stored = preferences.getInt(KEY_DELAY_MS, DEFAULT_DELAY_MS);
        int clamped = Math.max(0, Math.min(1000, stored));
        return Math.round(clamped / 200f) * 200;
    }
}
