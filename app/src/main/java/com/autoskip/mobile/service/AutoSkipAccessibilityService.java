package com.autoskip.mobile.service;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.GestureDescription;
import android.content.SharedPreferences;
import android.graphics.Path;
import android.graphics.Rect;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import com.autoskip.mobile.data.AppPreferences;
import com.autoskip.mobile.data.StatsRepository;
import com.autoskip.mobile.detection.CooldownController;
import com.autoskip.mobile.detection.AdTextMatcher;
import com.autoskip.mobile.detection.SkipDetector;
import com.autoskip.mobile.detection.SkipTextMatcher;
import com.autoskip.mobile.detection.TikTokAdDetector;

public final class AutoSkipAccessibilityService extends AccessibilityService {
    public static final String YOUTUBE_PACKAGE = "com.google.android.youtube";
    public static final String TIKTOK_PACKAGE = "com.zhiliaoapp.musically";

    private static final long SAME_CONTROL_COOLDOWN_MS = 1_500L;
    private static final long TIKTOK_SWIPE_COOLDOWN_MS = 3_000L;
    private static final long ESTIMATED_SAVED_PER_SKIP_MS = 5_000L;

    private final Handler mainHandler = new Handler(Looper.getMainLooper());
    private final SkipDetector detector = new SkipDetector(new SkipTextMatcher());
    private final TikTokAdDetector tikTokAdDetector = new TikTokAdDetector(new AdTextMatcher());
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

        int delayMs = TIKTOK_PACKAGE.equals(packageName)
                ? AppPreferences.tikTokDetectionDelayMs(preferences)
                : AppPreferences.detectionDelayMs(preferences);
        scheduleScan(packageName, delayMs);
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
        if (TIKTOK_PACKAGE.equals(packageName)) {
            return AppPreferences.isTikTokEnabled(preferences);
        }
        return false;
    }

    private void scheduleScan(String expectedPackage, int delayMs) {
        if (pendingScan != null) {
            return;
        }
        pendingScan = () -> {
            pendingScan = null;
            scanAndAct(expectedPackage);
        };
        mainHandler.postDelayed(pendingScan, delayMs);
    }

    private void scanAndAct(String expectedPackage) {
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

        if (TIKTOK_PACKAGE.equals(expectedPackage)) {
            scanAndSwipeTikTok(root);
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
            statsRepository.recordSkip(
                    StatsRepository.Target.YOUTUBE,
                    ESTIMATED_SAVED_PER_SKIP_MS,
                    System.currentTimeMillis()
            );
        }
    }

    private void scanAndSwipeTikTok(AccessibilityNodeInfo root) {
        TikTokAdDetector.Marker marker = tikTokAdDetector.findMarker(root);
        if (marker == null) {
            return;
        }

        long nowElapsedMs = SystemClock.elapsedRealtime();
        String fingerprint = marker.fingerprint();
        if (!cooldown.canClick(fingerprint, nowElapsedMs, TIKTOK_SWIPE_COOLDOWN_MS)) {
            return;
        }

        Rect windowBounds = new Rect();
        root.getBoundsInScreen(windowBounds);
        if (windowBounds.width() < 100 || windowBounds.height() < 300) {
            return;
        }

        TikTokSwipePath.Coordinates swipe = TikTokSwipePath.inside(windowBounds);
        Path path = new Path();
        path.moveTo(swipe.startX, swipe.startY);
        path.lineTo(swipe.endX, swipe.endY);

        GestureDescription gesture = new GestureDescription.Builder()
                .addStroke(new GestureDescription.StrokeDescription(path, 0L, 280L))
                .build();
        boolean dispatched = dispatchGesture(
                gesture,
                new GestureResultCallback() {
                    @Override
                    public void onCompleted(GestureDescription gestureDescription) {
                        statsRepository.recordSkip(
                                StatsRepository.Target.TIKTOK,
                                ESTIMATED_SAVED_PER_SKIP_MS,
                                System.currentTimeMillis()
                        );
                    }
                },
                mainHandler
        );
        if (dispatched) {
            cooldown.markClicked(fingerprint, nowElapsedMs);
        }
    }

    private void cancelPendingScan() {
        if (pendingScan != null) {
            mainHandler.removeCallbacks(pendingScan);
            pendingScan = null;
        }
    }
}
