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
| `service` | Mapping TMDB → API DTOs, filtering/sorting/pagination, enrichment, caching. |
| `tmdb.TmdbClient` | One method per TMDB endpoint; builds URLs and deserialises into `dto/tmdb/**`. |
| `dto/api` | **Public** API contract. Safe to serialise to the frontend. |
| `dto/tmdb` | **Internal** TMDB models. Must not leak into responses. |
| `config` | CORS, caching, REST client, request logging. |
| `exception` | `GlobalExceptionHandler` + `ApiErrorDto`. |

### DTO rules

1. `dto/tmdb/**` mirrors TMDB JSON. `dto/api/**` is what the frontend consumes.
2. Never return a `dto/tmdb/**` type from a controller.
3. `MediaCardDto` (list item) and `MediaDetailsDto` (details page) are the two core public models.
4. `MediaType` (enum `MOVIE`/`TV`) is used for typing media; it serialises as `"MOVIE"`/`"TV"`.
5. **Frontend mirroring:** `dto/api/**` is mirrored by hand in `kino-frontend/src/app/core/models/*.ts`. When you change a backend API DTO, update the matching frontend interface in the same change.

### Filtering, sorting, pagination

- `MediaFilterService` is the single place that applies `yearFrom`/`yearTo`, sorts by `rating`/`date`/`title`, and slices a page.
- `MediaFilterService.paginate(items, filter, page, pageSize)` also computes `totalPages`/`totalResults`, so services don't repeat that bookkeeping.
- Filtering currently operates **within a single TMDB page** (20 items); it is not applied across the whole TMDB result set. See [backlog](plans/backlog.md).

## API surface

| Method | Path | Notes |
|--------|------|-------|
| GET | `/api/movies/{id}` | Movie details (genres, cast, directors, videos, keywords, recommendations) |
| GET | `/api/movies/trending` | `window=day\|week` |
| GET | `/api/movies/popular` | |
| GET | `/api/movies/top-rated` | |
| GET | `/api/movies/upcoming` | |
| GET | `/api/tv/{id}` | TV details (creators, cast, videos, keywords, recommendations) |
| GET | `/api/tv/trending` | `window=day\|week` |
| GET | `/api/tv/popular` | |
| GET | `/api/tv/top-rated` | |
| GET | `/api/tv/on-the-air` | |
| GET | `/api/search/movie` | `query` |
| GET | `/api/search/tv` | `query` |
| GET | `/api/search/all` | `query`, multi-search (people filtered out) |

All list endpoints accept `page`, `yearFrom`, `yearTo`, `sortBy`, `sortOrder`.

Actuator: `GET /actuator/health`, `GET /actuator/mappings`.

## Frontend structure

```
src/app/
├── core/models/          # hand-written DTO interfaces mirroring dto/api
├── kino-api.service.ts   # single HttpClient wrapper for the backend
├── pages/                # routed pages: home, media-list-page, search, details
├── shared/               # reusable components: media-card, media-carousel,
│                         # filter-controls, video-*, keyword-section, trailer-modal
└── app.routes.ts         # route table
```

- Standalone components only; no `NgModule`.
- Templates use the built-in `@if`/`@for` control flow (not `*ngIf`/`*ngFor`).
- Route conventions: `/movies/:category`, `/tv/:category`, `/movies/details/:id`, `/tv/details/:id`, `/search`.
- The API base URL comes from `environment.apiUrl`:
  - dev (`environment.ts`): `http://localhost:8080/api`
  - prod (`environment.prod.ts`): `/api` (relative — resolved by nginx in Docker and by `vercel.json` on Vercel)

## Configuration

- Backend config lives in `kino-backend/src/main/resources/application.properties`.
- The TMDB key is read from the `TMDB_API_KEY` env var (`.env` is loaded optionally via `spring.config.import`).
- `kino.cors.allowed-origins` is a comma-separated allow-list.

## Testing

- **Backend:** JUnit 5 + Mockito. Service tests are plain unit tests; controller tests use `@WebMvcTest`; `TmdbClient` is tested against `MockRestServiceServer`. Tests are hermetic — they use `src/test/resources/application.properties` and never need a real `.env`.
- **Frontend:** Vitest. Component/page specs plus service tests.

See [RUNNING.md](RUNNING.md) for commands.
