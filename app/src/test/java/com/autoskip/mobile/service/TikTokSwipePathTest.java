package com.autoskip.mobile.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public final class TikTokSwipePathTest {
    @Test
    public void startsInsideMainMediaInsteadOfCaptionOrCarouselFooter() {
        TikTokSwipePath.Coordinates swipe = TikTokSwipePath.inside(0, 0, 1080, 2412);

        assertEquals(194.4f, swipe.startX, 0.1f);
        assertEquals(1254.24f, swipe.startY, 0.1f);
        assertEquals(482.4f, swipe.endY, 0.1f);
        assertEquals(swipe.startX, swipe.endX, 0.01f);
        assertTrue(swipe.endY < swipe.startY);
    }

    @Test
    public void respectsOffsetWindowBounds() {
        TikTokSwipePath.Coordinates swipe = TikTokSwipePath.inside(20, 100, 1000, 2000);

        assertEquals(200f, swipe.startX, 0.1f);
        assertEquals(1140f, swipe.startY, 0.1f);
        assertEquals(500f, swipe.endY, 0.1f);
    }
}
