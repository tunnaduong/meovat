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
