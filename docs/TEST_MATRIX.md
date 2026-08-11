# Device test matrix

## Required environments

- ARM64 physical phone, supported Android release
- x86_64 Android emulator
- Compact portrait and landscape
- Expanded/tablet or resizable emulator window
- English and Russian system/app language
- Light, dark, and Android 12+ dynamic color
- Default and large font scale
- TalkBack enabled for the AutoSkip settings screen

## Playback cases

| Case | Expected result |
|---|---|
| Skippable ad, button exposed | One activation after configured delay |
| Two skippable ads in sequence | One activation per distinct control |
| Unskippable ad | No action |
| Countdown text such as “Skip in 5” | No action before native control becomes available |
| Ordinary video controls | No action |
| Shorts | No action unless exact valid skip control appears |
| Fullscreen portrait/landscape | Same evidence rules; no coordinate dependency |
| YouTube backgrounded | No scan or action in other packages |
| Main switch off | No action |
| YouTube target off | No action in YouTube |
| TikTok target off | No scan or swipe in TikTok |
| TikTok exact `Реклама` marker at lower left | One upward swipe after TikTok delay |
| TikTok exact `Sponsored` marker at lower left | One upward swipe after TikTok delay |
| TikTok ad-related caption outside marker region | No swipe |
| TikTok CTA such as `Shop now` without ad marker | No swipe |
| TikTok repeated content event during gesture | No second swipe inside cooldown |
| Repeated content-change storm | One pending scan; cooldown prevents repeated click |

## Regression capture

For each confirmed YouTube version, record without personal content:

- YouTube version code and Android API level
- locale, orientation, and player mode
- candidate label, content description, resource ID, class, bounds
- detector evidence and score
- click result
- unexpected nodes containing “skip” / “пропустить”

Never store account names, video titles, recommendations, comments, or full accessibility dumps in committed fixtures.
