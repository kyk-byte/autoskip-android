# TikTok ad detection evidence

`tiktok-ad-russian.png` is a user-supplied TikTok advertisement example captured on 2026-08-11.

Confirmed signal:

- Exact visible label: `Реклама`.
- Position: a compact marker in the lower-left region of the active TikTok video.
- Expected action: one upward feed swipe after the configured TikTok delay.

Version 0.2 requires all conditions: an exact supported ad label, compact marker-sized bounds, and placement in the lower-left marker region. Call-to-action text such as `Купить`, `Подробнее`, or `Shop now` is not sufficient. This conservative rule reduces the chance of skipping an ordinary video containing advertising-related words in its caption.

The screenshot is detection evidence, not a pixel template. TikTok may change its accessibility tree; captured node-tree fixtures remain required before treating the detector as stable.
