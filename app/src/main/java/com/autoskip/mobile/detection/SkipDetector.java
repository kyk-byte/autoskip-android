package com.autoskip.mobile.detection;

import android.graphics.Rect;
import android.view.accessibility.AccessibilityNodeInfo;

import java.util.ArrayDeque;
import java.util.Deque;

public final class SkipDetector {
    private static final int MAX_VISITED_NODES = 500;
    private static final int MAX_CLICKABLE_ANCESTORS = 3;

    private final SkipTextMatcher matcher;

    public SkipDetector(SkipTextMatcher matcher) {
        this.matcher = matcher;
    }

    public Candidate findBest(AccessibilityNodeInfo root) {
        if (root == null) {
            return null;
        }

        Deque<AccessibilityNodeInfo> pending = new ArrayDeque<>();
        pending.add(root);
        Candidate best = null;
        int visited = 0;

        while (!pending.isEmpty() && visited < MAX_VISITED_NODES) {
            AccessibilityNodeInfo node = pending.removeFirst();
            visited += 1;

            Candidate candidate = evaluate(node);
            if (candidate != null && (best == null || candidate.score() > best.score())) {
                best = candidate;
                if (best.score() >= 130) {
                    break;
                }
            }

            for (int index = 0; index < node.getChildCount(); index += 1) {
                AccessibilityNodeInfo child = node.getChild(index);
                if (child != null) {
                    pending.addLast(child);
                }
            }
        }

        return best;
    }

    private Candidate evaluate(AccessibilityNodeInfo evidenceNode) {
        if (!evidenceNode.isVisibleToUser() || !evidenceNode.isEnabled()) {
            return null;
        }

        SkipTextMatcher.Match match = matcher.match(
                evidenceNode.getText(),
                evidenceNode.getContentDescription(),
                evidenceNode.getViewIdResourceName()
        );
        if (!match.found()) {
            return null;
        }

        AccessibilityNodeInfo clickTarget = findClickableTarget(evidenceNode);
        if (clickTarget == null || !clickTarget.isVisibleToUser() || !clickTarget.isEnabled()) {
            return null;
        }

        // ID-only evidence must point directly at a button-like clickable node.
        if (match.evidence() == SkipTextMatcher.Evidence.VIEW_ID
                && clickTarget != evidenceNode) {
            return null;
        }

        int score = match.score() + 20;
        CharSequence className = clickTarget.getClassName();
        if (className != null && className.toString().endsWith("Button")) {
            score += 10;
        }

        Rect bounds = new Rect();
        clickTarget.getBoundsInScreen(bounds);
        if (bounds.isEmpty()) {
            return null;
        }

        String label = matcher.normalize(evidenceNode.getText());
        if (label.isEmpty()) {
            label = matcher.normalize(evidenceNode.getContentDescription());
        }
        String fingerprint = String.valueOf(clickTarget.getPackageName())
                + "|" + String.valueOf(evidenceNode.getViewIdResourceName())
                + "|" + label
                + "|" + bounds.flattenToString();

        return new Candidate(clickTarget, fingerprint, match.evidence(), score);
    }

    private AccessibilityNodeInfo findClickableTarget(AccessibilityNodeInfo node) {
        AccessibilityNodeInfo current = node;
        for (int depth = 0; depth <= MAX_CLICKABLE_ANCESTORS && current != null; depth += 1) {
            if (current.isClickable()) {
                return current;
            }
            current = current.getParent();
        }
        return null;
    }

    public static final class Candidate {
        private final AccessibilityNodeInfo node;
        private final String fingerprint;
        private final SkipTextMatcher.Evidence evidence;
        private final int score;

        public Candidate(
                AccessibilityNodeInfo node,
                String fingerprint,
                SkipTextMatcher.Evidence evidence,
                int score
        ) {
            this.node = node;
            this.fingerprint = fingerprint;
            this.evidence = evidence;
            this.score = score;
        }

        public AccessibilityNodeInfo node() {
            return node;
        }

        public String fingerprint() {
            return fingerprint;
        }

        public SkipTextMatcher.Evidence evidence() {
            return evidence;
        }

        public int score() {
            return score;
        }
    }
}
