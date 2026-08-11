package com.autoskip.mobile.service;

import android.accessibilityservice.AccessibilityService;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import com.autoskip.mobile.data.AppPreferences;
import com.autoskip.mobile.data.StatsRepository;
import com.autoskip.mobile.detection.CooldownController;
import com.autoskip.mobile.detection.SkipDetector;
import com.autoskip.mobile.detection.SkipTextMatcher;

public final class AutoSkipAccessibilityService extends AccessibilityService {
    public static final String YOUTUBE_PACKAGE = "com.google.android.youtube";
    public static final String YOUTUBE_MUSIC_PACKAGE = "com.google.android.apps.youtube.music";

    private static final long SAME_CONTROL_COOLDOWN_MS = 1_500L;
    private static final long ESTIMATED_SAVED_PER_SKIP_MS = 5_000L;

    private final Handler mainHandler = new Handler(Looper.getMainLooper());
    private final SkipDetector detector = new SkipDetector(new SkipTextMatcher());
    private final CooldownController cooldown = new CooldownController();

    private SharedPreferences preferences;
    private StatsRepository statsRepository;
    private Runnable pendingScan;

    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();
        preferences = AppPreferences.from(this);
        statsRepository = new StatsRepository(preferences);
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        if (event == null) {
            return;
        }
        if (preferences == null) {
            preferences = AppPreferences.from(this);
            statsRepository = new StatsRepository(preferences);
        }
        if (!AppPreferences.isEnabled(preferences)) {
            cancelPendingScan();
            return;
        }

        String packageName = event.getPackageName() == null
                ? ""
                : event.getPackageName().toString();
        if (!isEnabledTarget(packageName)) {
            return;
        }

        scheduleScan(packageName, AppPreferences.detectionDelayMs(preferences));
    }

    @Override
    public void onInterrupt() {
        cancelPendingScan();
    }

    @Override
    public void onDestroy() {
        cancelPendingScan();
        super.onDestroy();
    }

    private boolean isEnabledTarget(String packageName) {
        if (YOUTUBE_PACKAGE.equals(packageName)) {
            return AppPreferences.isYouTubeEnabled(preferences);
        }
        return YOUTUBE_MUSIC_PACKAGE.equals(packageName)
                && AppPreferences.isYouTubeMusicEnabled(preferences);
    }

    private void scheduleScan(String expectedPackage, int delayMs) {
        if (pendingScan != null) {
            return;
        }
        pendingScan = () -> {
            pendingScan = null;
            scanAndClick(expectedPackage);
        };
        mainHandler.postDelayed(pendingScan, delayMs);
    }

    private void scanAndClick(String expectedPackage) {
        if (!AppPreferences.isEnabled(preferences)) {
            return;
        }
        AccessibilityNodeInfo root = getRootInActiveWindow();
        if (root == null
                || root.getPackageName() == null
                || !expectedPackage.contentEquals(root.getPackageName())
                || !isEnabledTarget(expectedPackage)) {
            return;
        }

        SkipDetector.Candidate candidate = detector.findBest(root);
        if (candidate == null) {
            return;
        }

        long nowElapsedMs = SystemClock.elapsedRealtime();
        if (!cooldown.canClick(candidate.fingerprint(), nowElapsedMs, SAME_CONTROL_COOLDOWN_MS)) {
            return;
        }

        boolean clicked = candidate.node().performAction(AccessibilityNodeInfo.ACTION_CLICK);
        if (clicked) {
            cooldown.markClicked(candidate.fingerprint(), nowElapsedMs);
            statsRepository.recordSkip(ESTIMATED_SAVED_PER_SKIP_MS, System.currentTimeMillis());
        }
    }

    private void cancelPendingScan() {
        if (pendingScan != null) {
            mainHandler.removeCallbacks(pendingScan);
            pendingScan = null;
        }
    }
}
