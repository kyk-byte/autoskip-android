# Product

<!-- impeccable:product-schema 1 -->

## Platform

android

## Stack

Inferred from the supplied brief: Java 17, Android Views, Material 3, Gradle. The stack was delegated by the brief's Kotlin/Java allowance; Java keeps the first build small and ABI-independent.

## Users

Android users who watch YouTube or TikTok and want strongly identified ads skipped without manual timing.

## Product Purpose

AutoSkip observes only supported YouTube and TikTok accessibility trees. It activates a visible YouTube skip control or performs one TikTok feed swipe after an exact lower-left ad marker, then records local per-platform statistics. Success means reliable action with no unrelated clicks or swipes.

## Positioning

The MVP acts only when the target app exposes strong skip evidence through Android Accessibility. It does not patch YouTube, intercept account data, inspect network traffic, or claim to block unskippable ads.

## Operating Context

The user installs the app, explicitly enables its Accessibility service in Android settings, chooses supported target apps, then watches normally. The service runs only for `com.google.android.youtube` and `com.zhiliaoapp.musically`.

## Capabilities and Constraints

- Detect skip controls by exact localized label, content description, resource ID, visibility, enabled state, and clickability.
- Support Russian and English labels in the MVP.
- Apply separate configurable YouTube and TikTok detection delays plus action cooldowns.
- Keep per-platform counters and estimated saved time locally on-device.
- TikTok remains off by default and requires an exact supported ad label in the lower-left marker region before one upward swipe.
- Do nothing when evidence is weak or no skip control is available.
- Ship no native libraries, so one APK is compatible with ARM, ARM64, x86, and x86_64 runtimes.
- Screenshot/OCR fallback, network filtering, and client modification remain unimplemented research stages.
- YouTube UI identifiers can change; detector fixtures and device testing must be maintained after app updates.

## Brand Commitments

“AutoSkip” is a provisional functional name inferred from the supplied panel sketch. Tone: direct, calm, technically honest.

## Evidence on Hand

The supplied plan defines the architecture, target labels, settings outline, test matrix, and staged research roadmap. No production logo, screenshots, benchmark data, legal claims, or store listing assets were supplied; future work must not fabricate them.

## Product Principles

1. Strong evidence before action.
2. Native control activation before heavier fallbacks.
3. Least privilege and on-device data only.
4. Architecture independence by default.
5. Honest status, estimates, and limitations.

## Accessibility & Inclusion

The control app itself must support TalkBack, 48 dp touch targets, font scaling, light/dark themes, and Android system navigation. Its Accessibility service is an automation feature, not a claim of a general-purpose assistive tool.
