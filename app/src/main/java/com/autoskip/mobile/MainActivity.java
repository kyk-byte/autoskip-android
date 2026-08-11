package com.autoskip.mobile;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.provider.Settings;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;

import com.autoskip.mobile.data.AppPreferences;
import com.autoskip.mobile.data.StatsRepository;
import com.autoskip.mobile.databinding.ActivityMainBinding;
import com.autoskip.mobile.service.AccessibilityStatus;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.text.NumberFormat;
import java.util.Locale;

public final class MainActivity extends AppCompatActivity
        implements SharedPreferences.OnSharedPreferenceChangeListener {

    private ActivityMainBinding binding;
    private SharedPreferences preferences;
    private StatsRepository statsRepository;
    private boolean rendering;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        applySystemInsets();

        preferences = AppPreferences.from(this);
        statsRepository = new StatsRepository(preferences);
        bindActions();
        renderAll();
    }

    @Override
    protected void onStart() {
        super.onStart();
        preferences.registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        renderAll();
    }

    @Override
    protected void onStop() {
        preferences.unregisterOnSharedPreferenceChangeListener(this);
        super.onStop();
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        renderAll();
    }

    private void applySystemInsets() {
        int originalLeft = binding.root.getPaddingLeft();
        int originalTop = binding.root.getPaddingTop();
        int originalRight = binding.root.getPaddingRight();
        int originalBottom = binding.root.getPaddingBottom();
        ViewCompat.setOnApplyWindowInsetsListener(binding.root, (view, windowInsets) -> {
            androidx.core.graphics.Insets bars = windowInsets.getInsets(
                    WindowInsetsCompat.Type.systemBars() | WindowInsetsCompat.Type.displayCutout()
            );
            view.setPadding(
                    originalLeft + bars.left,
                    originalTop + bars.top,
                    originalRight + bars.right,
                    originalBottom + bars.bottom
            );
            return windowInsets;
        });
    }

    private void bindActions() {
        binding.statusAction.setOnClickListener(view -> openAccessibilitySettings());
        binding.delaySlider.setLabelFormatter(
                value -> getString(R.string.delay_value_seconds, value / 1000f)
        );

        binding.autoSkipSwitch.setOnCheckedChangeListener((button, checked) -> {
            if (!rendering) {
                preferences.edit().putBoolean(AppPreferences.KEY_ENABLED, checked).apply();
            }
        });
        binding.youtubeSwitch.setOnCheckedChangeListener((button, checked) -> {
            if (!rendering) {
                preferences.edit().putBoolean(AppPreferences.KEY_YOUTUBE, checked).apply();
            }
        });
        binding.tiktokSwitch.setOnCheckedChangeListener((button, checked) -> {
            if (!rendering) {
                preferences.edit().putBoolean(AppPreferences.KEY_TIKTOK, checked).apply();
            }
        });
        binding.delaySlider.addOnChangeListener((slider, value, fromUser) -> {
            if (fromUser && !rendering) {
                preferences.edit().putInt(AppPreferences.KEY_DELAY_MS, Math.round(value)).apply();
            }
        });
        binding.resetStatsButton.setOnClickListener(view -> confirmStatsReset());
    }

    private void renderAll() {
        if (binding == null || preferences == null) {
            return;
        }
        rendering = true;
        binding.autoSkipSwitch.setChecked(AppPreferences.isEnabled(preferences));
        binding.youtubeSwitch.setChecked(AppPreferences.isYouTubeEnabled(preferences));
        binding.tiktokSwitch.setChecked(AppPreferences.isTikTokEnabled(preferences));

        int delayMs = AppPreferences.detectionDelayMs(preferences);
        binding.delaySlider.setValue(delayMs);
        binding.delayValue.setText(getString(R.string.delay_value_seconds, delayMs / 1000f));
        binding.versionText.setText(getString(R.string.version_label, BuildConfig.VERSION_NAME));

        renderServiceStatus();
        renderStats();
        rendering = false;
    }

    private void renderServiceStatus() {
        boolean systemEnabled = AccessibilityStatus.isServiceEnabled(this);
        boolean autoSkipEnabled = AppPreferences.isEnabled(preferences);
        int color;

        if (!systemEnabled) {
            binding.statusTitle.setText(R.string.status_access_required);
            binding.statusDescription.setText(R.string.status_access_required_description);
            binding.statusAction.setText(R.string.open_accessibility_settings);
            color = ContextCompat.getColor(this, R.color.status_warning);
        } else if (!autoSkipEnabled) {
            binding.statusTitle.setText(R.string.status_paused);
            binding.statusDescription.setText(R.string.status_paused_description);
            binding.statusAction.setText(R.string.open_accessibility_settings);
            color = ContextCompat.getColor(this, R.color.status_neutral);
        } else {
            binding.statusTitle.setText(R.string.status_active);
            binding.statusDescription.setText(R.string.status_active_description);
            binding.statusAction.setText(R.string.review_accessibility_access);
            color = ContextCompat.getColor(this, R.color.status_active);
        }

        binding.statusIndicator.setBackgroundTintList(ColorStateList.valueOf(color));
    }

    private void renderStats() {
        StatsRepository.Snapshot snapshot = statsRepository.snapshot();
        NumberFormat numberFormat = NumberFormat.getIntegerInstance(Locale.getDefault());
        binding.skippedCount.setText(numberFormat.format(snapshot.skippedCount()));
        binding.savedTime.setText(formatDuration(snapshot.estimatedSavedMs()));
    }

    private String formatDuration(long durationMs) {
        long totalSeconds = Math.max(0L, durationMs / 1000L);
        long minutes = totalSeconds / 60L;
        long seconds = totalSeconds % 60L;
        if (minutes > 0L) {
            return getString(R.string.duration_minutes_seconds, minutes, seconds);
        }
        return getString(R.string.duration_seconds, seconds);
    }

    private void openAccessibilitySettings() {
        Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
        try {
            startActivity(intent);
        } catch (RuntimeException error) {
            startActivity(new Intent(Settings.ACTION_SETTINGS));
        }
    }

    private void confirmStatsReset() {
        new MaterialAlertDialogBuilder(this)
                .setTitle(R.string.reset_stats_title)
                .setMessage(R.string.reset_stats_message)
                .setNegativeButton(R.string.cancel, null)
                .setPositiveButton(R.string.reset, (dialog, which) -> statsRepository.reset())
                .show();
    }
}
