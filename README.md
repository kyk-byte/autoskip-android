# AutoSkip

Android MVP that activates YouTube's own visible **Skip / Пропустить** control through `AccessibilityService`.

The app does not block unskippable ads, patch YouTube, inspect network traffic, or access an account. It intentionally does nothing unless the accessibility tree contains strong skip evidence and a visible, enabled, clickable target.

## Current scope

- YouTube package: `com.google.android.youtube`
- Optional experimental YouTube Music package: `com.google.android.apps.youtube.music`
- Exact Russian and English label matching
- Resource-ID matching with countdown/timer exclusions
- Visibility, enabled-state, button/clickable-ancestor validation
- Configurable 0–1 second detection delay
- Global and per-control click cooldown
- Local skip count and explicitly estimated saved time
- Material 3 settings UI, dynamic color, dark theme, TalkBack labels
- No `INTERNET` permission

## Architecture support

The MVP contains no native `.so` libraries or NDK code. Android bytecode therefore runs from one universal APK on ARM, ARM64, x86, and x86_64 devices. ABI-specific APK splits are unnecessary until a future OCR or native image-processing module introduces native binaries.

## Build

Requirements:

- JDK 17
- Android SDK 35
- Gradle 8.8, supplied through the wrapper

```powershell
.\gradlew.bat test assembleDebug
```

Debug APK:

```text
app/build/outputs/apk/debug/app-debug.apk
```

## First run

1. Install and open AutoSkip.
2. Tap **Open settings**.
3. Find **AutoSkip for YouTube** under installed accessibility services.
4. Review Android's disclosure and enable the service.
5. Return to AutoSkip; status should become active.
6. Open YouTube and play a video containing a skippable ad.

If no native skip control is exposed, AutoSkip must take no action.

## Privacy and policy

All settings and statistics use app-local `SharedPreferences`; backups and device transfer are disabled for them. The manifest requests no Internet permission.

Android Accessibility access is highly sensitive. This service is declared with `isAccessibilityTool="false"` because it is narrow UI automation, not a general assistive technology. Any store distribution must provide prominent disclosure, explicit consent, accurate Data Safety answers, and comply with the store's current Accessibility API policy. Independent project; not affiliated with or endorsed by YouTube or Google.

## Verification

Pure matching and cooldown logic can be checked without Android SDK:

```powershell
javac -d work/classes app/src/main/java/com/autoskip/mobile/detection/SkipTextMatcher.java app/src/main/java/com/autoskip/mobile/detection/CooldownController.java work/MatcherSmokeTest.java
java -cp work/classes MatcherSmokeTest
```

Full unit tests run with `gradlew test`. Device behavior requires the manual matrix in [docs/TEST_MATRIX.md](docs/TEST_MATRIX.md).

## Roadmap

1. Validate Accessibility MVP against current YouTube releases.
2. Add captured-tree regression fixtures and more interface languages.
3. Evaluate screenshot/OCR fallback behind separate consent and power controls.
4. Research network traffic classification separately; do not assume DNS blocking can distinguish ads from video delivery.
5. Treat APK modification as a separate maintenance-heavy research project, not part of this app.

