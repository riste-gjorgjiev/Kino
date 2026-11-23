# Week 10: Responsive & Animation Polish

## Goal

Polish the UI with CSS transitions, carousel touch support, a mobile navigation overhaul, lazy-loaded routes, and accessibility improvements — aiming for a Lighthouse mobile score of ≥80.

## Decisions

| Decision | Rationale |
|----------|-----------|
| Bottom tab bar on mobile (≤768px) | Thumb-friendly primary navigation; keeps the top bar for desktop dropdowns. |
| Hide the desktop top nav entirely on mobile | Avoids a cramped, wrapping menu; the bottom bar covers Home, Movies, TV, Search. |
| Lazy-load all routes via `loadComponent` | Splits the bundle per page; initial payload dropped from ~395 kB to ~301 kB. |
| CSS-only fade-in for route changes | No extra animation dependency; respects `prefers-reduced-motion`. |
| Touch swipe + keyboard arrows on carousels | Native scroll already works; explicit handlers make swipe distance and keyboard navigation intentional. |
| `prefers-reduced-motion` media query globally | Accessibility: disables transitions/animations for users who request it. |
| Tap targets ≥44px on mobile | Meets WCAG guidance for touch controls (pager, filters, inputs). |
| `loading="lazy"` on all images | Defers offscreen poster/profile/still loads. |

## Changes

### Navigation

- **Desktop:** unchanged top nav with Movies/TV dropdowns and Search.
- **Mobile (≤768px):** top nav hidden; a fixed **bottom tab bar** appears with four items (Home, Movies, TV, Search), each with an inline SVG icon and label.
  - Respects `env(safe-area-inset-bottom)` for notched devices.
  - `main` content gets `padding-bottom: 72px` so the bar never covers content.
  - Active tab is highlighted in blue.

### Routing

- All routes converted to lazy-loaded `loadComponent` imports.
- Route table: `''`, `movies/:category`, `tv/:category`, `movies/details/:id`, `tv/details/:id`, `person/:id`, `search`, `**`.

### Animations & transitions

- Media cards: `translateY(-4px) scale(1.02)` + stronger shadow on hover, `:active` feedback.
- Home/media-list cards: same hover treatment.
- Cast cards: lift + shadow on hover.
- Keyword chips: background shift + lift on hover.
- Episode cards: lift + shadow on hover.
- Page content: subtle 200ms fade-in.
- Buttons/inputs: background, border, and focus-ring transitions.

### Carousel

- Touch swipe detection (`touchstart`/`touchmove`/`touchend`) with a 50px threshold.
- Keyboard support: `ArrowLeft`/`ArrowRight` when the carousel is focused.
- `tabindex="0"`, `role="region"`, and an accessible `aria-label`.
- `-webkit-overflow-scrolling: touch` and visible focus ring.

### Accessibility & performance

- Global `:focus-visible` outline (already present) plus `prefers-reduced-motion` handling.
- All images lazy-loaded (media cards, details poster, cast, episode stills).
- Added meta description, theme-color, viewport-fit, and mobile web-app tags in `index.html`.
- Focus rings and larger tap targets on filter inputs, season selector, and pagers.

## Verification

| Check | Result |
|-------|--------|
| `cd kino-frontend && npm test -- --watch=false` | 54 tests, 0 failures |
| `cd kino-frontend && npm run build` | Clean; lazy chunks emitted per route |
| Initial bundle size | ~301 kB (down from ~395 kB) |
| `cd kino-backend && ./mvnw test` | 157 tests, 0 failures |
| Bottom tab bar on ≤768px | ✅ |
| Top nav on ≥769px | ✅ |
| Carousel swipe + arrow keys | ✅ |
| Reduced-motion respected | ✅ |
| No horizontal scroll at 320–1440px | ✅ |

## Notes / possible follow-ups

- Bottom tab bar could later highlight the specific Movies/TV sub-category.
- Consider a scroll-position indicator (dots) for carousels on very small screens.
- Lighthouse should be re-run manually in Chrome DevTools against a running build to confirm the ≥80 target; automated CI Lighthouse was not added (DevOps still deferred).
- Server-side rendering / prerendering remains a Phase 5 (W39–W40) item.

---

*End of Week 10 summary.*
