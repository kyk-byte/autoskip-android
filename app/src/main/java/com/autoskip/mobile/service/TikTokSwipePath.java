package com.autoskip.mobile.service;

import android.graphics.Rect;

final class TikTokSwipePath {
    private static final float X_FRACTION = 0.18f;
    private static final float START_Y_FRACTION = 0.52f;
    private static final float END_Y_FRACTION = 0.20f;

    private TikTokSwipePath() {
    }

    static Coordinates inside(Rect bounds) {
        return inside(bounds.left, bounds.top, bounds.width(), bounds.height());
    }

    static Coordinates inside(int left, int top, int width, int height) {
        return new Coordinates(
                left + width * X_FRACTION,
                top + height * START_Y_FRACTION,
                left + width * X_FRACTION,
                top + height * END_Y_FRACTION
        );
    }

    static final class Coordinates {
        final float startX;
        final float startY;
        final float endX;
        final float endY;

        Coordinates(float startX, float startY, float endX, float endY) {
            this.startX = startX;
            this.startY = startY;
            this.endX = endX;
            this.endY = endY;
        }
    }
}
