package com.autoskip.mobile.detection;

import android.graphics.Rect;
import android.view.accessibility.AccessibilityNodeInfo;

import java.util.ArrayDeque;
import java.util.Deque;

public final class TikTokAdDetector {
    private static final int MAX_VISITED_NODES = 500;

    private final AdTextMatcher matcher;

    public TikTokAdDetector(AdTextMatcher matcher) {
        this.matcher = matcher;
    }

    public Marker findMarker(AccessibilityNodeInfo root) {
        if (root == null) {
            return null;
        }

        Deque<AccessibilityNodeInfo> pending = new ArrayDeque<>();
        pending.add(root);
        Rect windowBounds = new Rect();
        root.getBoundsInScreen(windowBounds);
        if (windowBounds.isEmpty()) {
            return null;
        }
        int visited = 0;

        while (!pending.isEmpty() && visited < MAX_VISITED_NODES) {
            AccessibilityNodeInfo node = pending.removeFirst();
            visited += 1;

            if (node.isVisibleToUser() && node.isEnabled()) {
                AdTextMatcher.Match match = matcher.match(
                        node.getText(),
                        node.getContentDescription()
                );
                if (match.found()) {
                    Rect bounds = new Rect();
                    node.getBoundsInScreen(bounds);
                    if (!bounds.isEmpty() && AdMarkerRegion.isLowerLeft(
                            bounds.left,
                            bounds.top,
                            bounds.right,
                            bounds.bottom,
                            windowBounds.left,
                            windowBounds.top,
                            windowBounds.right,
                            windowBounds.bottom
                    )) {
                        return new Marker(match.label(), bounds);
                    }
                }
            }

            for (int index = 0; index < node.getChildCount(); index += 1) {
                AccessibilityNodeInfo child = node.getChild(index);
                if (child != null) {
                    pending.addLast(child);
                }
            }
        }
        return null;
    }

    public static final class Marker {
        private final String label;
        private final Rect bounds;

        public Marker(String label, Rect bounds) {
            this.label = label;
            this.bounds = new Rect(bounds);
        }

        public String fingerprint() {
            return "tiktok|" + label + "|" + bounds.flattenToString();
        }
    }
}
