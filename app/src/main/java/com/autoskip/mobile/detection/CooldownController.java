package com.autoskip.mobile.detection;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public final class CooldownController {
    private static final long GLOBAL_FLOOR_MS = 350L;
    private static final long ENTRY_RETENTION_MS = 60_000L;

    private final Map<String, Long> successfulClicks = new HashMap<>();
    private long lastSuccessfulClickMs = Long.MIN_VALUE;

    public synchronized boolean canClick(String fingerprint, long nowMs, long cooldownMs) {
        if (fingerprint == null || fingerprint.isBlank()) {
            return false;
        }
        if (lastSuccessfulClickMs != Long.MIN_VALUE
                && elapsed(nowMs, lastSuccessfulClickMs) < GLOBAL_FLOOR_MS) {
            return false;
        }
        Long previous = successfulClicks.get(fingerprint);
        return previous == null || elapsed(nowMs, previous) >= Math.max(GLOBAL_FLOOR_MS, cooldownMs);
    }

    public synchronized void markClicked(String fingerprint, long nowMs) {
        successfulClicks.put(fingerprint, nowMs);
        lastSuccessfulClickMs = nowMs;
        prune(nowMs);
    }

    private void prune(long nowMs) {
        if (successfulClicks.size() < 32) {
            return;
        }
        Iterator<Map.Entry<String, Long>> iterator = successfulClicks.entrySet().iterator();
        while (iterator.hasNext()) {
            if (elapsed(nowMs, iterator.next().getValue()) > ENTRY_RETENTION_MS) {
                iterator.remove();
            }
        }
    }

    private long elapsed(long nowMs, long previousMs) {
        if (nowMs < previousMs) {
            return Long.MAX_VALUE;
        }
        return nowMs - previousMs;
    }
}

