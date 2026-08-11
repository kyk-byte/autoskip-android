package com.autoskip.mobile.detection;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public final class SkipTextMatcherTest {
    private final SkipTextMatcher matcher = new SkipTextMatcher();

    @Test
    public void matchesEnglishAndRussianExactLabels() {
        assertTrue(matcher.match("Skip ad", null, null).found());
        assertTrue(matcher.match("  ПРОПУСТИТЬ\u00A0РЕКЛАМУ  ", null, null).found());
    }

    @Test
    public void refusesCountdownAndPartialPhrases() {
        assertFalse(matcher.match("Skip in 5", null, null).found());
        assertFalse(matcher.match("Do not skip", null, null).found());
        assertFalse(matcher.match(null, null, "id/skip_ad_countdown").found());
    }

    @Test
    public void combinesIndependentEvidence() {
        SkipTextMatcher.Match match = matcher.match(
                "Skip",
                null,
                "com.google.android.youtube:id/skip_ad_button"
        );
        assertEquals(SkipTextMatcher.Evidence.LABEL_AND_VIEW_ID, match.evidence());
        assertEquals(100, match.score());
    }

    @Test
    public void acceptsKnownResourceId() {
        SkipTextMatcher.Match match = matcher.match(
                null,
                null,
                "com.google.android.youtube:id/skip_button"
        );
        assertEquals(SkipTextMatcher.Evidence.VIEW_ID, match.evidence());
    }
}

