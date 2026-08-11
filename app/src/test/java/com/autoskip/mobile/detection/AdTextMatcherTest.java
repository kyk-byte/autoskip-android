package com.autoskip.mobile.detection;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public final class AdTextMatcherTest {
    private final AdTextMatcher matcher = new AdTextMatcher();

    @Test
    public void matchesExactEnglishAndRussianLabels() {
        assertTrue(matcher.match("Sponsored", null).found());
        assertTrue(matcher.match("  РЕКЛАМА\u00A0", null).found());
        assertTrue(matcher.match(null, "Advertisement").found());
    }

    @Test
    public void refusesPartialOrCallToActionText() {
        assertFalse(matcher.match("Sponsored by Example", null).found());
        assertFalse(matcher.match("Ad", null).found());
        assertFalse(matcher.match("Shop now", null).found());
        assertFalse(matcher.match("This video discusses advertisement", null).found());
    }

    @Test
    public void returnsNormalizedFingerprintLabel() {
        AdTextMatcher.Match match = matcher.match("  PAID   PARTNERSHIP ", null);
        assertEquals("paid partnership", match.label());
    }
}
