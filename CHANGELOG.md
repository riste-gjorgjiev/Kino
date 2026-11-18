# Changelog

Notable changes to Kino. Format loosely follows [Keep a Changelog](https://keepachangelog.com/en/1.1.0/); the project is pre-1.0, so entries are grouped by theme rather than strict semver releases. Week-by-week detail lives in [docs/weekly](docs/weekly/).

## [Unreleased]

### Added

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
- Caffeine caches added for TV seasons; cache statistics recorded.
- Filtering/pagination now uses TMDB's `totalPages`/`totalResults`; the UI states that filters apply to the current page.
- CORS origins are externalised via `KINO_CORS_ALLOWED_ORIGINS`; the wildcard Vercel preview pattern was removed.
- Shared `ImageUrlBuilder` and `MediaCardMapper` replace duplicated image/mapping logic across services.
- Documentation restructured; roadmap and backlog updated (W7–W8 complete, W9 next).

### Fixed

- HTTP error handling and DTO nullability in the frontend (`posterUrl`, `rating`, `date`).
- Hermetic backend tests that no longer depend on a real `.env`.

[Unreleased]: https://github.com/riste-gjorgjiev/Kino/commits/main
