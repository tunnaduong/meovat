# Mẹo Vặt website

Static marketing site for the Mẹo Vặt app: landing page, Privacy Policy and Terms of Use, in
Vietnamese and English.

```
index.html      landing page
privacy.html    Chính sách quyền riêng tư / Privacy Policy
terms.html      Điều khoản sử dụng / Terms of Use
styles.css      shared styles (tokens mirror the app's design tokens)
app.js          VI/EN switching + mobile nav
assets/         app icon (SVG) and design screenshots
```

## Languages

Both languages live in the same HTML. Blocks tagged `data-lang="vi"` / `data-lang="en"` are
shown or hidden by `<html lang>`, which `app.js` sets from (in order) the `?lang=vi|en` query
parameter, the saved choice in `localStorage`, then the browser language (Vietnamese → `vi`,
anything else → `en`). `<title>` and `<meta name="description">` carry `data-vi` / `data-en`.

To add a language, add a `data-lang="xx"` block next to each existing pair, a button in
`.lang-switch`, and the CSS rule in `styles.css` that hides the other languages.

## Before publishing

- Replace the `href="#"` store links in `index.html` (`#download` section and hero) with the
  real App Store / Google Play URLs and drop `aria-disabled`.
- Check the contact email in the footer, `privacy.html` and `terms.html`.
- Set the `og:image` / `og:url` meta tags to absolute URLs on the final domain.
- The privacy policy states retention periods (12 months inactive → deleted, logs ≤ 30 days) and
  that data is protected in transit; make sure the API is served over HTTPS and that a cleanup
  job actually enforces those periods.

## Preview locally

```bash
python3 -m http.server 8080 --directory website
```

Any static host works (GitHub Pages, Netlify, Cloudflare Pages, or the nginx on the Pi).
