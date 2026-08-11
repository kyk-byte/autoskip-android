# Approved AutoSkip UI reference

`approved-autoskip-ui.png` is the user-approved visual target for the Android frontend. It is the only authoritative composition and styling reference for the redesign.

## Fidelity contract

- Reproduce the reference at its portrait breakpoint before adapting it to other Android sizes.
- Preserve the near-black surface, white primary text, muted gray secondary text, vivid red accent, thin red outlines, diagonal red background lines, rounded panels, and restrained soft depth.
- Preserve the information order: AutoSkip brand and tagline; YouTube and TikTok switches; per-platform skipped-ad counters; separate YouTube and TikTok cooldown controls; final Start AutoSkip action.
- Preserve the large platform marks, red OFF/ON switch treatment, numeric cooldown value, 0–1 range labels, and red slider treatment.
- Functional implementation must conform to this composition. Existing Material UI is not visual authority.
- Do not introduce new navigation, cards, colors, typography treatments, gradients, or decorative motifs unless the user approves a correction.
- Text may be localized between English and Russian without changing hierarchy or component geometry. Genuine copy defects may be corrected while preserving placement and tone.
- Android accessibility, touch-target, system inset, font-scaling, and responsive requirements remain mandatory. Any resulting visual adjustment must be the smallest possible deviation and documented.

## Source integrity

- Original attachment copied without visual editing.
- Approved by the user on 2026-08-11.
- Implementation status: reference committed; frontend reproduction pending.

`DESIGN.md` will be generated from the finished, reference-matched implementation. Until then, this file and the PNG form the binding design contract.
