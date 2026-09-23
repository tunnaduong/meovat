# Mẹo Vặt

Native mobile apps for the **Mẹo Vặt** ("Life Hacks") design in Figma:
<https://www.figma.com/design/YQCCsRP3BkESTb6UvYEjph/>

| Branch    | Platform | Stack |
|-----------|----------|-------|
| `ios`     | iOS 17+  | Swift 5, SwiftUI, Observation |
| `android` | Android 8.0+ (API 26) | Kotlin, Jetpack Compose, Material 3 |

`main` holds only the shared documentation and design references (`design/screens`).

## Screens

- **Chủ đề (Home)** – category list → tip list (search, filter chips) → tip info (prep checklist) → step-by-step guide → "save to list" sheet
- **Đã lưu (Saved)** – saved lists → list contents; create / edit list sheets; sort sheet
- **Cài đặt (Settings)** – notifications toggle, language picker, about

## Design tokens

| Token | Value |
|-------|-------|
| Primary | `#F36704` |
| Primary subtle | `#FFE1CC` |
| Heading | `#1D2939` |
| Caption | `#667085` |
| Disabled | `#98A2B3` |
| Border | `#D0D5DD` |
| Canvas secondary | `#F2F4F7` |
| Radius | 12 (cards), 999 (pills) |
| Fonts | Be Vietnam Pro (body), Inter SemiBold (48pt titles) |

## App icon

`design/app-icon.svg` is the master (light bulb + check on the brand orange). It is rasterised to
`MeoVat/Assets.xcassets/AppIcon.appiconset/AppIcon.png` on `ios` and re-drawn as an adaptive icon
(`res/drawable/ic_launcher_foreground.xml` + `ic_launcher_background.xml`) on `android`.

## Build & run

```bash
# iOS (Xcode 16+, iOS 17 simulator)
git checkout ios
open MeoVat.xcodeproj        # or: xcodebuild -scheme MeoVat -destination 'platform=iOS Simulator,name=iPhone 17' build

# Android (JDK 17, Android SDK 36)
git checkout android
./gradlew :app:installDebug  # needs a running emulator / device
```

Both apps bundle the same `seed.json` content (7 categories, 24 tips, 3 starter lists) and persist
user state (saved lists, checklist ticks, settings) locally — there is no backend.
