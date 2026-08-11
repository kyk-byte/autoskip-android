package com.autoskip.mobile.detection;

import java.util.Locale;
import java.util.Set;

public final class AdTextMatcher {
    private static final Set<String> EXACT_AD_LABELS = Set.of(
            "advertisement",
            "sponsored",
            "paid partnership",
            "реклама",
            "рекламное объявление",
            "спонсировано",
            "платное партнерство",
            "платное партнёрство"
    );

    public Match match(CharSequence text, CharSequence contentDescription) {
        String textLabel = normalize(text);
        if (EXACT_AD_LABELS.contains(textLabel)) {
            return new Match(true, textLabel);
        }

        String descriptionLabel = normalize(contentDescription);
        if (EXACT_AD_LABELS.contains(descriptionLabel)) {
            return new Match(true, descriptionLabel);
        }
        return new Match(false, "");
    }

    public String normalize(CharSequence value) {
        if (value == null) {
            return "";
        }
        return value.toString()
                .replace('\u00A0', ' ')
                .trim()
                .replaceAll("\\s+", " ")
                .toLowerCase(Locale.ROOT);
    }

    public static final class Match {
        private final boolean found;
        private final String label;

        public Match(boolean found, String label) {
            this.found = found;
            this.label = label;
        }

        public boolean found() {
            return found;
        }

        public String label() {
            return label;
        }
    }
}
