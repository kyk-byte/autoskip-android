# Research roadmap

## Stage 1: Accessibility release

Exit criteria:

- zero unrelated clicks across the manual matrix;
- captured-tree regression tests for at least RU and EN;
- ARM64 phone and x86_64 emulator verification;
- clear disclosure and revocation path;
- measured CPU impact during normal playback.

## Stage 2: Screen-recognition fallback

Separate feature flag and architecture. Use `MediaProjection` only after explicit Android consent. Prefer bounded regions, low sampling rate, and on-device OCR. Re-run the same evidence and cooldown policy before dispatching an accessibility gesture. No screenshot persistence by default.

## Stage 3: Network research

Use an isolated test device and document only traffic owned by the tester. Determine whether advertising and content can be classified without decrypting or breaking normal playback. A shared host or delivery path is not blockable evidence. DNS-only filtering is not assumed effective.

## Stage 4: Client-side research

Keep APK analysis outside the release app and repository boundary. Track version-specific findings, licensing, signature/update consequences, integrity checks, and maintenance cost before deciding whether any prototype is justified.

No later stage may weaken Stage 1's invariant: uncertain evidence means no action.

