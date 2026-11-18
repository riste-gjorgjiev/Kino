# 🎬 Kino — Week 4 Plan: UI Polish, Cleanup & Stability

> **Document version:** 1.0
> **Last updated:** 2026-08-05
> **Status:** ✅ Completed

---

## 🎯 Week 4 Goal

Close Phase 1 technical debt and UI gaps so Kino is stable, clean, and presentable — then document the transition to Phase 2.

---

## 📋 Step-by-Step Plan

### Step 1 — Fix Broken / Corrupted Backend Tests
**Why:** Several test files have duplicate `@Mock` annotations, duplicate field declarations, and method signatures that don't match the real `TmdbClient` API. These tests likely don't compile.

**Actions:**
1. Fix `MovieServiceTest.java`:
   - Remove duplicate `@Mock` and duplicate `mediaFilterService` declarations.
   - Add `@Mock private TmdbClient tmdbClient;`.
   - Remove `MediaFilterDto` from `tmdbClient` mock calls (real methods don't take filters).
2. Fix `TvServiceTest.java` with the same pattern.
3. Fix `SearchServiceTest.java`:
   - Remove duplicate `@Mock`.
   - Add `@Mock private TmdbClient tmdbClient;`.
   - Fix `searchTvShows` mock to match real signature `(String, int)` not `(String, int, MediaFilterDto)`.
4. Fix `TmdbClientTest.java`:
   - Remove `MediaFilterDto` arguments from all `tmdbClient` calls.
   - Fix `ObjectMapper` import — use `com.fasterxml.jackson.databind.ObjectMapper` instead of `tools.jackson...`.
5. Fix controller tests:
   - `MovieControllerTest`, `TvControllerTest`, `SearchControllerTest` — verify service method signatures match real ones.
6. Run `./mvnw clean test` and make it green.

**Deliverable:** Backend test suite compiles and passes.

---

### Step 2 — Clean Up Low-Hanging Code Debt
**Why:** The cleanup plan identified real issues; most are quick wins.

**Actions:**
1. Remove duplicate CORS config:
   - Verify `WebConfig.java` still exists; if yes, delete it and keep `CorsConfig.java`.
2. Remove duplicate `spring-boot-starter-cache` from `pom.xml` if still present.
3. Delete `opencode.json.bak` if it exists.
4. Remove unused imports:
   - `ActivatedRoute` in `home.ts`.
   - Any other unused imports found by the IDE/build.
5. Remove debug `console.log` statements from frontend if any remain.
6. Verify app title is `"Kino"` in `index.html` and `app.ts` (already done).
7. Consolidate `.gitignore` files (root vs frontend).

**Deliverable:** Cleaner codebase with fewer warnings and no duplicate config.

---

### Step 3 — Fix Frontend Routing Bug
**Why:** `app.routes.ts` has a suspicious route:

```ts
{path: 'tv/details/:category', component: MediaListPage},
```

This looks like a mistake. The media-list page expects `:category` (popular, top-rated, on-the-air), but the path says `details`. Also, cards link to `/tv/details/:id`, which doesn't match any route. TV details currently work via `/tv/:id`, but the card routerLink is wrong.

**Actions:**
1. Decide the canonical TV detail route:
   - Option A: `/tv/:id` (current working route) → update card links.
   - Option B: `/tv/details/:id` → add route and update `Details` component to detect TV by path.
2. Remove or fix the `tv/details/:category` route.
3. Update all `routerLink` expressions to use the chosen route consistently.

**Deliverable:** Clicking a TV show card navigates to the correct details page.

---

### Step 4 — Add Loading, Empty & Error States to All Pages
**Why:** Home, search, and details have inconsistent state handling. Media-list has no loading indicator.

**Actions:**
1. **Home page** (`home.html`):
   - Keep existing loading/error blocks.
   - Add skeleton/placeholder cards while loading (optional but nice).
2. **Media-list page** (`media-list-page.html`):
   - Add `@if (loading) { <p>Loading…</p> }`.
   - Keep empty state.
   - Add error state with retry button.
3. **Search page** (`search.html`):
   - Improve empty search state.
   - Add error retry.
4. **Details page** (`details.html`):
   - Already has loading/error; verify it shows correctly when `backdropUrl` is missing (currently `url(null)` may look broken).

**Deliverable:** Every page shows clear loading, empty, and error states.

---

### Step 5 — Improve Responsive Layout & Visual Polish
**Why:** The app works but looks very basic on mobile and has inconsistent spacing.

**Actions:**
1. Add global CSS reset/variables in `styles.css`:
   - Box-sizing, font stack, base colors, spacing scale.
2. Make navigation mobile-friendly:
   - Collapse dropdowns or use a hamburger menu on small screens.
3. Improve grid responsiveness:
   - Cards currently use `minmax(160px, 1fr)`; consider smaller min on mobile.
4. Add consistent page padding/margins via a `.container` class.
5. Add hover/focus states for accessibility.
6. Ensure images have `alt` text and fallback styling when missing.

**Deliverable:** App looks polished on desktop and usable on mobile.

---

### Step 6 — Add Home-Page Horizontal Carousels
**Why:** The expansion plan explicitly lists "front-page horizontal carousels" for Week 4.

**Actions:**
1. Convert home page rows into reusable carousel sections:
   - Create `MediaCarouselComponent` in `shared/media-carousel/`.
   - Accept `@Input() title` and `@Input() items: MediaCardDto[]`.
   - Render horizontal scrollable row with previous/next buttons.
2. Use the carousel for:
   - Trending Movies
   - Trending TV
3. Optionally add more rows:
   - Popular Movies
   - Popular TV
   - Top Rated Movies

**Deliverable:** Home page shows attractive horizontal carousels.

---

### Step 7 — Add a Reusable Media Card Component
**Why:** Card markup is duplicated across home, media-list, search, and soon carousel.

**Actions:**
1. Create `MediaCardComponent` in `shared/media-card/`.
2. Inputs: `item: MediaCardDto`.
3. Handles:
   - Poster fallback.
   - Rating display.
   - Date display.
   - Router link to correct detail route.
4. Replace inline card markup in:
   - `home.html`
   - `media-list-page.html`
   - `search.html`
   - new `media-carousel.html`

**Deliverable:** Single source of truth for media cards.

---

### Step 8 — Verify & Document the Week 4 Release
**Actions:**
1. Run full verification:
   ```bash
   cd kino-backend && ./mvnw clean test
   cd ../kino-frontend && npm install && ng build
   docker compose up -d --build
   ```
2. Smoke-test:
   - Home loads with carousels.
   - Movies/TV list pages load and filter.
   - Search works.
   - Details pages work for both movies and TV.
   - Mobile layout is acceptable.
3. Write `docs/weekly/week4-summary.md` documenting:
   - What was done.
   - Known issues.
   - Phase 2 readiness.

**Deliverable:** Week 4 summary document and a demo-ready MVP.

---

## 🚪 Gate to Phase 2 (Week 5)

Before starting trailers/recommendations, these should be true:

| Gate | Status Target |
|------|---------------|
| Backend tests pass | ✅ Green |
| Frontend builds | ✅ No errors |
| Docker compose works | ✅ |
| TV details navigation works | ✅ |
| All pages have loading/error/empty states | ✅ |
| Home has carousels | ✅ |
| Code cleanup items done | ✅ |

---

## ⏱ Suggested Order of Execution

1. Fix backend tests (Step 1) — highest priority, unblocks everything.
2. Cleanup (Step 2) — quick wins.
3. Fix routing (Step 3) — fixes real user-facing bug.
4. Add states (Step 4).
5. Create shared components (Step 7).
6. Build carousels (Step 6) using shared components.
7. Responsive polish (Step 5).
8. Verify and document (Step 8).

---

*End of week 4 plan.*
