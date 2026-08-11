package com.autoskip.mobile.service;

import android.content.ComponentName;
import android.content.Context;
import android.provider.Settings;
import android.text.TextUtils;

public final class AccessibilityStatus {
    private AccessibilityStatus() {
    }

    public static boolean isServiceEnabled(Context context) {
        ComponentName expected = new ComponentName(context, AutoSkipAccessibilityService.class);
        String enabledServices = Settings.Secure.getString(
                context.getContentResolver(),
                Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES
        );
        if (enabledServices == null || enabledServices.isBlank()) {
            return false;
        }

        TextUtils.SimpleStringSplitter splitter = new TextUtils.SimpleStringSplitter(':');
        splitter.setString(enabledServices);
        while (splitter.hasNext()) {
            ComponentName candidate = ComponentName.unflattenFromString(splitter.next());
            if (expected.equals(candidate)) {
                return true;
            }
        }
        return false;
    }
}

