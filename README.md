# Mẹo Vặt

Native mobile apps for the **Mẹo Vặt** ("Life Hacks") design in Figma:
<https://www.figma.com/design/YQCCsRP3BkESTb6UvYEjph/>

| Branch    | Platform | Stack |
|-----------|----------|-------|
| `ios`     | iOS 17+  | Swift 5, SwiftUI, Observation |
| `android` | Android 8.0+ (API 26) | Kotlin, Jetpack Compose, Material 3 |
| `backend` | Raspberry Pi (`100.102.160.98:8787`) | PHP 8.3, MySQL/MariaDB, nginx + php-fpm |
| `website` | Static marketing site (VI/EN) + Privacy Policy / Terms of Use | HTML, CSS, vanilla JS |

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

## Backend

Both apps talk to the PHP API on the Raspberry Pi (`backend` branch, deployed at
`/www/wwwroot/meovat-api`, served on port 8787 over Tailscale). There is no login: each install
generates a UUID and sends it as `X-Device-Id`; the first request creates that device's user with
the starter lists. The apps are offline-first — the bundled `seed.json` and the last synced state
render immediately, the server's copy replaces them on launch / pull-to-refresh, and every change
is applied locally then pushed in the background (an "offline" banner shows if the Pi is
unreachable). The API base URL lives in `AppConfig` on both platforms.

## Website (this branch)

Static marketing site for the app: landing page, Privacy Policy and Terms of Use, in Vietnamese
and English.

```
index.html      landing page
privacy.html    Chính sách quyền riêng tư / Privacy Policy
terms.html      Điều khoản sử dụng / Terms of Use
styles.css      shared styles (tokens mirror the app's design tokens above)
app.js          VI/EN switching + mobile nav
assets/         app icon (SVG) and design screenshots
```

### Languages

Both languages live in the same HTML. Blocks tagged `data-lang="vi"` / `data-lang="en"` are
shown or hidden by `<html lang>`, which `app.js` sets from (in order) the `?lang=vi|en` query
parameter, the saved choice in `localStorage`, then the browser language (Vietnamese → `vi`,
anything else → `en`). `<title>` and `<meta name="description">` carry `data-vi` / `data-en`.

To add a language, add a `data-lang="xx"` block next to each existing pair, a button in
`.lang-switch`, and the CSS rule in `styles.css` that hides the other languages.

### Before publishing

- Replace the `href="#"` store links in `index.html` (hero and `#download` section) with the
  real App Store / Google Play URLs and drop `aria-disabled`.
- Set the `og:image` / `og:url` meta tags to absolute URLs on the final domain.
- The privacy policy states retention periods (12 months inactive → deleted, logs ≤ 30 days);
  make sure the API is served over HTTPS and that a cleanup job actually enforces those periods.

### Preview locally

```bash
python3 -m http.server 8787
```

Any static host works (GitHub Pages, Netlify, Cloudflare Pages, or the nginx on the Pi).
