package com.autoskip.mobile;

import android.app.Application;

import com.google.android.material.color.DynamicColors;

public final class AutoSkipApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        DynamicColors.applyToActivitiesIfAvailable(this);
    }
}

