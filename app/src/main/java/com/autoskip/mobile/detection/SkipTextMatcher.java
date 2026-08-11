package com.autoskip.mobile.detection;

import java.text.Normalizer;
import java.util.Locale;
import java.util.Set;

public final class SkipTextMatcher {
    private static final Set<String> EXACT_LABELS = Set.of(
            "skip",
            "skip ad",
            "skip ads",
            "пропустить",
            "пропустить рекламу",
            "пропустить объявление"
    );

    private static final Set<String> EXACT_VIEW_IDS = Set.of(
            "skip_ad_button",
            "skip_button",
            "ad_skip_button",
            "skip_ad"
    );

    public Match match(CharSequence text, CharSequence contentDescription, String viewId) {
        boolean labelMatch = isExactLabel(text) || isExactLabel(contentDescription);
        boolean viewIdMatch = isSkipViewId(viewId);

        if (labelMatch && viewIdMatch) {
            return new Match(Evidence.LABEL_AND_VIEW_ID, 100);
        }
        if (labelMatch) {
            return new Match(Evidence.LABEL, 72);
        }
        if (viewIdMatch) {
            return new Match(Evidence.VIEW_ID, 52);
        }
        return Match.NONE;
    }

    public boolean isExactLabel(CharSequence value) {
        return EXACT_LABELS.contains(normalize(value));
    }

    public boolean isSkipViewId(String viewId) {
        String normalized = normalizeViewId(viewId);
        if (normalized.isEmpty()
                || normalized.contains("countdown")
                || normalized.contains("timer")
                || normalized.contains("label")) {
            return false;
        }

        int separator = normalized.lastIndexOf('/');
        String resourceName = separator >= 0 ? normalized.substring(separator + 1) : normalized;
        return EXACT_VIEW_IDS.contains(resourceName);
    }

    public String normalize(CharSequence value) {
        if (value == null) {
            return "";
        }
        String normalized = Normalizer.normalize(value.toString(), Normalizer.Form.NFKC)
                .replace('\u00A0', ' ')
                .trim()
                .toLowerCase(Locale.ROOT);
        return normalized.replaceAll("\\s+", " ");
    }

    private String normalizeViewId(String value) {
        if (value == null) {
            return "";
        }
        return value.trim().toLowerCase(Locale.ROOT);
    }

    public enum Evidence {
        NONE,
        LABEL,
        VIEW_ID,
        LABEL_AND_VIEW_ID
    }

    public static final class Match {
        public static final Match NONE = new Match(Evidence.NONE, 0);

        private final Evidence evidence;
        private final int score;

        public Match(Evidence evidence, int score) {
            this.evidence = evidence;
            this.score = score;
        }

        public Evidence evidence() {
            return evidence;
        }

        public int score() {
            return score;
        }

        public boolean found() {
            return evidence != Evidence.NONE;
        }
    }
}
