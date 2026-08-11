package com.autoskip.mobile.detection;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public final class AdMarkerRegionTest {
    @Test
    public void acceptsBottomLeftTikTokMarker() {
        assertTrue(AdMarkerRegion.isLowerLeft(
                10, 630, 65, 655,
                0, 0, 326, 664
        ));
    }

    @Test
    public void refusesSameLabelInCaptionOrTopNavigation() {
        assertFalse(AdMarkerRegion.isLowerLeft(
                10, 300, 65, 325,
                0, 0, 326, 664
        ));
        assertFalse(AdMarkerRegion.isLowerLeft(
                220, 630, 300, 655,
                0, 0, 326, 664
        ));
    }

    @Test
    public void refusesInvalidBounds() {
        assertFalse(AdMarkerRegion.isLowerLeft(
                10, 10, 10, 20,
                0, 0, 326, 664
        ));
    }

    @Test
    public void refusesLargeCaptionNode() {
        assertFalse(AdMarkerRegion.isLowerLeft(
                5, 560, 250, 655,
                0, 0, 326, 664
        ));
    }
}
