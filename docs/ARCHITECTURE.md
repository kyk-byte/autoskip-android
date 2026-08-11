# Architecture

## Runtime path

```text
YouTube accessibility event
  -> package and preference gate
  -> one pending delayed scan
  -> bounded breadth-first tree walk (max 500 nodes)
  -> exact text / description / resource-ID evidence
  -> visible + enabled + clickable target validation
  -> global and per-control cooldown
  -> ACTION_CLICK
  -> local statistic update only after reported success
```

## Components

- `AutoSkipAccessibilityService`: lifecycle, package scope, per-platform delayed scan, YouTube click execution, and TikTok swipe execution.
- `SkipDetector`: bounded Android accessibility-tree traversal and node validation.
- `SkipTextMatcher`: pure Java locale normalization and conservative evidence scoring.
- `CooldownController`: pure Java duplicate-action guard.
- `AppPreferences`: target, enablement, and delay state.
- `StatsRepository`: app-local count and estimated saved time.
- `MainActivity`: service status, settings, disclosure, and statistics.

## Safety invariants

1. Only YouTube and TikTok packages listed in `accessibility_service_config.xml` can deliver events.
2. Runtime preferences narrow that static package list further.
3. Partial phrases such as `Skip in 5` do not match.
4. IDs containing `countdown`, `timer`, or `label` do not match.
5. Evidence must lead to a visible, enabled, clickable node within three ancestors.
6. ID-only evidence cannot borrow clickability from an ancestor.
7. Empty bounds reject the target.
8. Statistics change only when `performAction(ACTION_CLICK)` reports success.
9. No coordinate tap, screenshot, network, account, or root capability exists in the MVP.

## Known limits

- YouTube can expose WebView or custom-rendered controls without useful accessibility nodes.
- Resource IDs and labels can change after YouTube updates.
- A successful `performAction` return means Android accepted the action, not that playback outcome was independently observed.
- Saved time uses a transparent 5-second estimate per successful click. Exact ad duration is unavailable in this architecture.
- TikTok is off by default. It requires an exact localized ad label in the lower-left video region; CTA text alone never triggers a swipe.
- TikTok gesture execution uses `canPerformGestures=true` and one bounded upward stroke.
- Statistics preserve a total count while also recording separate YouTube and TikTok counts for the approved 0.2 UI.

## Future boundaries

OCR fallback must be a separate module with explicit screen-capture consent, bounded sampling, battery controls, and no image persistence by default. Network research must use test accounts and owned devices; it must not be coupled to the stable Accessibility release until traffic classification proves reliable.
