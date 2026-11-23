# Changelog

Notable changes to Kino. Format loosely follows [Keep a Changelog](https://keepachangelog.com/en/1.1.0/); the project is pre-1.0, so entries are grouped by theme rather than strict semver releases. Week-by-week detail lives in [docs/weekly](docs/weekly/).

## [Unreleased]

### Added

- Person/actor detail pages: `GET /api/person/{id}` with biography, profile, dates, and a merged movie+TV filmography (sorted by date, capped at 50).
- Frontend person page (`/person/:id`) with filmography grid; cast cards on details pages now link to it.
- Mobile bottom tab bar (Home, Movies, TV, Search) for ≤768px screens.
- Carousel touch-swipe and keyboard (arrow key) navigation.
- `prefers-reduced-motion` support and global focus-visible styling.
- Lazy-loaded routes via `loadComponent`; per-route bundle chunks.
- TV season and episode support: season list, season details and episode list endpoints (`/api/tv/{id}/seasons`, `/api/tv/{id}/seasons/{n}`, `/api/tv/{id}/seasons/{n}/episodes`).
- TV details season selector and episode guide component.
- Keywords, similar titles and TMDB recommendations on details pages, merged and de-duplicated ("More Like This").
- Trailer/video gallery with a YouTube modal.
- Year-range filtering, sorting by rating/date/title, and pagination on list and search endpoints.
- Backend unit/integration tests (JUnit 5, Mockito, `@WebMvcTest`, `MockRestServiceServer`).
- Frontend unit tests (Vitest) for pages and shared components, including `media-card`, `media-carousel` and the season guide.
- Playwright E2E smoke test with mocked API routes (home → details → list → search).
- `CHANGELOG.md` (this file).

### Changed

- Recommendations + similar cap aligned at **12** items.
- Media cards, cast cards, keyword chips, and episode cards now have consistent hover/active transitions.
- All images lazy-load; the initial JS bundle dropped from ~395 kB to ~301 kB after route splitting.
- Caffeine caches added for TV seasons; cache statistics recorded.
- Filtering/pagination now uses TMDB's `totalPages`/`totalResults`; the UI states that filters apply to the current page.
- CORS origins are externalised via `KINO_CORS_ALLOWED_ORIGINS`; the wildcard Vercel preview pattern was removed.
- Shared `ImageUrlBuilder` and `MediaCardMapper` replace duplicated image/mapping logic across services.
- Documentation restructured; roadmap and backlog updated (Phase 2 / W5–W10 complete, Phase 3 next).

### Fixed

- HTTP error handling and DTO nullability in the frontend (`posterUrl`, `rating`, `date`).
- Hermetic backend tests that no longer depend on a real `.env`.

[Unreleased]: https://github.com/riste-gjorgjiev/Kino/commits/main
