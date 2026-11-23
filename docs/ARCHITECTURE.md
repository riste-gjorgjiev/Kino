# Architecture

A quick reference for how Kino is put together. Read this before making structural changes.

## Overview

```
Angular SPA  ──HTTP──▶  Spring Boot API  ──RestClient──▶  TMDB API
 (kino-frontend)          (kino-backend)                    (external)
```

- The backend is the **only** thing that talks to TMDB. It hides the API key and normalises the data.
- The frontend never sees a raw TMDB payload; it consumes Kino's own API DTOs.

## Backend layering

```
controller  →  service  →  tmdb/TmdbClient  →  TMDB REST API
     │            │
     │            └── dto/tmdb/**   (internal TMDB-shaped models, never returned to clients)
     └── dto/api/**                 (public models returned to the frontend)
```

| Package | Responsibility |
|---------|----------------|
| `controller` | HTTP endpoints, request params, Bean Validation. Thin. |
| `service` | Mapping TMDB → API DTOs, filtering/sorting/pagination, enrichment, caching. `MediaCardMapper` and `util.ImageUrlBuilder` hold shared mapping/image logic. |
| `tmdb.TmdbClient` | One method per TMDB endpoint; builds URLs and deserialises into `dto/tmdb/**`. |
| `dto/api` | **Public** API contract. Safe to serialise to the frontend. |
| `dto/tmdb` | **Internal** TMDB models. Must not leak into responses. |
| `config` | CORS, caching, REST client, request logging. |
| `util` | Small stateless helpers (e.g. `ImageUrlBuilder`). |
| `exception` | `GlobalExceptionHandler` + `ApiErrorDto`. |

### DTO rules

1. `dto/tmdb/**` mirrors TMDB JSON. `dto/api/**` is what the frontend consumes.
2. Never return a `dto/tmdb/**` type from a controller.
3. `MediaCardDto` (list item) and `MediaDetailsDto` (details page) are the two core public models.
4. `MediaType` (enum `MOVIE`/`TV`) is used for typing media; it serialises as `"MOVIE"`/`"TV"`.
5. **Frontend mirroring:** `dto/api/**` is mirrored by hand in `kino-frontend/src/app/core/models/*.ts`. When you change a backend API DTO, update the matching frontend interface in the same change.
6. TV seasons use `SeasonDto` (list), `SeasonDetailsDto` (season + episodes) and `EpisodeDto`; these are public models and must stay separate from the TMDB season/episode DTOs.

### Filtering, sorting, pagination

- `MediaFilterService.filterAndSort(...)` is the single place that applies `yearFrom`/`yearTo` and sorts by `rating`/`date`/`title`.
- `MediaFilterService.toPage(...)` builds the public page using TMDB's `totalPages`/`totalResults`, so paging metadata is not derived from a filtered subset.
- Filtering operates **within a single TMDB page** (20 items); it is not applied across the whole TMDB result set. The UI states this explicitly.

## API surface

| Method | Path | Notes |
|--------|------|-------|
| GET | `/api/movies/{id}` | Movie details (genres, cast, directors, videos, keywords, recommendations) |
| GET | `/api/movies/trending` | `window=day\|week` |
| GET | `/api/movies/popular` | |
| GET | `/api/movies/top-rated` | |
| GET | `/api/movies/upcoming` | |
| GET | `/api/tv/{id}` | TV details (creators, cast, videos, keywords, recommendations) |
| GET | `/api/tv/{id}/seasons` | Season list (TMDB seasons sorted by season number) |
| GET | `/api/tv/{id}/seasons/{seasonNumber}` | Season metadata + ordered episode list |
| GET | `/api/tv/{id}/seasons/{seasonNumber}/episodes` | Episode list only |
| GET | `/api/tv/trending` | `window=day\|week` |
| GET | `/api/tv/popular` | |
| GET | `/api/tv/top-rated` | |
| GET | `/api/tv/on-the-air` | |
| GET | `/api/search/movie` | `query` |
| GET | `/api/search/tv` | `query` |
| GET | `/api/search/all` | `query`, multi-search (people filtered out) |
| GET | `/api/person/{id}` | Person details (bio, filmography) |

All list endpoints accept `page`, `yearFrom`, `yearTo`, `sortBy`, `sortOrder`.

Actuator: `GET /actuator/health`, `GET /actuator/mappings`.

## Frontend structure

```
src/app/
├── core/models/          # hand-written DTO interfaces mirroring dto/api
├── kino-api.service.ts   # single HttpClient wrapper for the backend
├── pages/                # routed pages: home, media-list-page, search, details, person
├── shared/               # reusable components: media-card, media-carousel,
│                         # filter-controls, video-*, keyword-section,
│                         # trailer-modal, season-episode-guide
└── app.routes.ts         # route table
```

- Standalone components only; no `NgModule`.
- Templates use the built-in `@if`/`@for` control flow (not `*ngIf`/`*ngFor`).
- Routes are **lazy-loaded** via `loadComponent` in `app.routes.ts`.
- Route conventions: `/movies/:category`, `/tv/:category`, `/movies/details/:id`, `/tv/details/:id`, `/person/:id`, `/search`.
- Navigation: a top bar with Movies/TV dropdowns on desktop (≥769px); a fixed **bottom tab bar** (Home, Movies, TV, Search) on mobile (≤768px).
- The API base URL comes from `environment.apiUrl`:
  - dev (`environment.ts`): `http://localhost:8080/api`
  - prod (`environment.prod.ts`): `/api` (relative — resolved by nginx in Docker and by `vercel.json` on Vercel)

## Configuration

- Backend config lives in `kino-backend/src/main/resources/application.properties`.
- The TMDB key is read from the `TMDB_API_KEY` env var (`.env` is loaded optionally via `spring.config.import`).
- `kino.cors.allowed-origins` is a comma-separated allow-list, overridable with `KINO_CORS_ALLOWED_ORIGINS` (defaults to local dev plus the deployed Vercel origin; wildcard patterns are intentionally not used).

## Caching

- Caffeine cache names: list endpoints (`trendingMovies`, `popularMovies`, …), search, and TV seasons (`tvSeasons`, `tvSeasonDetails`, `tvSeasonEpisodes`).
- Entries expire 10 minutes after write; cache statistics are recorded.

## Testing

- **Backend:** JUnit 5 + Mockito. Service tests are plain unit tests; controller tests use `@WebMvcTest`; `TmdbClient` is tested against `MockRestServiceServer`. Tests are hermetic — they use `src/test/resources/application.properties` and never need a real `.env`.
- **Frontend:** Vitest. Component/page specs plus service tests.
- **E2E:** Playwright smoke test (`kino-frontend/e2e/`) with mocked API routes, so it needs neither a backend nor a TMDB key. It drives the real SPA through home → details → list → search.

See [RUNNING.md](RUNNING.md) for commands.
