# Weeks 7–8: TV Seasons & Episodes

## Goal

Extend TV support from a single details payload to a browsable season/episode guide: the backend exposes season metadata and ordered episode lists, and the TV details page renders a season selector plus an episode list.

## Decisions

| Decision | Rationale |
|----------|-----------|
| Reuse `GET /api/tv/{id}` for the season list | The TV details payload already contains TMDB's `seasons` array, so no extra TMDB call is needed. |
| Add `GET /api/tv/{id}/seasons/{n}` and `.../episodes` | Season metadata and episodes are available from one TMDB call; the second endpoint is a thin convenience over the same cached result. |
| Put mapping in a dedicated `TvSeasonService` | Keeps `MediaDetailsService` focused on the details page and makes season caching/validation independently testable. |
| Sort seasons and episodes by number, drop malformed entries | TMDB ordering is usually correct but not guaranteed; the UI relies on stable ordering. |
| Treat season `0` as “Specials” | Matches TMDB conventions and avoids “Season 0” in the selector. |
| Cache at the service layer (`tvSeasons`, `tvSeasonDetails`, `tvSeasonEpisodes`) | Repeated season switching on the details page should not re-hit TMDB. |
| Auto-select the first non-special season, then load episodes | Users land on useful content; specials remain selectable. |
| Keep the guide as a self-contained component | `SeasonEpisodeGuideComponent` owns its loading/error/selection state; the details page only passes `tvId`. |

## Backend

**New public DTOs** (`dto/api/`):

- `SeasonDto` — id, season number, name, overview, poster URL, air date, episode count.
- `SeasonDetailsDto` — season metadata plus `List<EpisodeDto>`.
- `EpisodeDto` — id, season/episode numbers, name, overview, still URL, air date, runtime, rating, vote count.

**New internal DTOs** (`dto/tmdb/`):

- `TmdbSeasonDetailsDto`, `TmdbSeasonDto`, `TmdbEpisodeDto`.

**TMDB client:**

- `getTvSeasonDetails(tvId, seasonNumber)` → `/tv/{id}/season/{seasonNumber}`.

**Service and controller:**

- `TvSeasonService` maps TMDB → API DTOs, sorts by season/episode number, normalises blank strings to `null`, and resolves image URLs through the shared `ImageUrlBuilder`.
- `TvController` adds:
  - `GET /api/tv/{id}/seasons`
  - `GET /api/tv/{id}/seasons/{seasonNumber}`
  - `GET /api/tv/{id}/seasons/{seasonNumber}/episodes`
- `id` must be positive; `seasonNumber` must be `>= 0` (season `0` = specials).

## Frontend

- New models: `SeasonDto`, `SeasonDetailsDto`, `EpisodeDto`.
- `KinoApiService`: `tvSeasons`, `tvSeasonDetails`, `tvSeasonEpisodes`.
- New `SeasonEpisodeGuideComponent`:
  - Loads seasons on init and selects the first non-special season.
  - Season `<select>` labelled with episode counts; specials shown as “Specials”.
  - Episode list with still image (or placeholder), `S01E02` code, title, air date, runtime, rating and overview.
  - Loading, empty and error states, including retry.
- Integrated into the TV details page only (`@if (data?.mediaType === 'TV')`).
- Empty/missing stills degrade to a “No image” tile.

## Related fixes shipped alongside

- Recommendations + similar are now capped at **12** (previously 8), matching the Week 6 plan.
- `ImageUrlBuilder` and `MediaCardMapper` replace duplicated image/mapping logic across services.
- `MediaFilterService` now uses TMDB `totalPages`/`totalResults`; the UI notes that filtering applies to the current page.
- CORS wildcard removed; origins come from `KINO_CORS_ALLOWED_ORIGINS`.
- Frontend unit tests added for `media-card`, `media-carousel` and the season guide.
- Playwright smoke test added with mocked API routes.

## Verification

| Check | Result |
|-------|--------|
| `cd kino-backend && ./mvnw test` | 141 tests, 0 failures |
| `cd kino-frontend && npm test -- --watch=false` | 43 tests, 0 failures |
| `cd kino-frontend && npm run test:e2e` (system Chromium) | 1 smoke test passed |

Manual checks: TV details page shows the selector, switching seasons loads episodes, season `0` is labelled “Specials”, and failed season loads offer retry.

## Notes / possible follow-ups

- Season/episode deep links (e.g. `?season=2`) were not added.
- No aggregate cast or per-episode credits yet.
- Episode pagination is unnecessary at TMDB season sizes but could matter for very long seasons.

---

*End of Week 7–8 summary.*
