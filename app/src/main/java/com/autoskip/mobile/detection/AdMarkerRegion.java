package com.autoskip.mobile.detection;

public final class AdMarkerRegion {
    private AdMarkerRegion() {
    }

    public static boolean isLowerLeft(
            int markerLeft,
            int markerTop,
            int markerRight,
            int markerBottom,
            int windowLeft,
            int windowTop,
            int windowRight,
            int windowBottom
    ) {
        int windowWidth = windowRight - windowLeft;
        int windowHeight = windowBottom - windowTop;
        if (windowWidth <= 0 || windowHeight <= 0
                || markerRight <= markerLeft || markerBottom <= markerTop) {
            return false;
        }

        int markerWidth = markerRight - markerLeft;
        int markerHeight = markerBottom - markerTop;
        if (markerWidth > windowWidth * 0.45d || markerHeight > windowHeight * 0.15d) {
            return false;
        }

        long markerCenterX2 = (long) markerLeft + markerRight;
        long markerCenterY2 = (long) markerTop + markerBottom;
        long maxX2 = 2L * windowLeft + Math.round(windowWidth * 1.10d);
        long minY2 = 2L * windowTop + Math.round(windowHeight * 1.10d);
        return markerCenterX2 <= maxX2 && markerCenterY2 >= minY2;
    }
}
