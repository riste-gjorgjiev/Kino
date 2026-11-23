# Week 9: Person Details

## Goal

Add person/actor detail pages with biography and filmography. Make cast members on the existing details page clickable so users can navigate to a person's page.

## Decisions

| Decision | Rationale |
|----------|-----------|
| Single `GET /api/person/{id}` endpoint | Person details + credits are always consumed together; no need for separate endpoints. |
| Credits sorted by date descending, then by rating | Most recent work is most relevant; rating breaks ties for same-year entries. |
| Credits capped at 50 | Prolific actors can have 100+ credits; the page stays manageable. |
| De-duplicate by `(mediaType, id)` | A person might appear in both cast and crew for the same title. |
| Only cast credits (not crew) | Keeps scope tight; crew credits can be added later if needed. |
| Cast cards on details page become `<a>` links | Simple, accessible, no extra component needed. |
| Person search left out of multi-search (Option A) | People are discoverable through cast clicks; keeps W9 scope tight. |
| Credits shown as a single sorted list (not grouped) | All credits are "Acting" since we only fetch cast; grouping adds no value. |

## Backend

**New TMDB DTOs** (`dto/tmdb/person/`):

- `TmdbPersonDetailsDto` — id, name, biography, profile_path, birthday, deathday, place_of_birth, also_known_as, known_for_department, homepage, imdb_id.
- `TmdbPersonMovieCastDto` — id, title, poster_path, release_date, vote_average, character, order.
- `TmdbPersonTvCastDto` — id, name, poster_path, first_air_date, vote_average, character, order.
- `TmdbPersonMovieCreditDto` — wraps `cast: List<TmdbPersonMovieCastDto>`.
- `TmdbPersonTvCreditDto` — wraps `cast: List<TmdbPersonTvCastDto>`.

**New public API DTOs** (`dto/api/`):

- `PersonDetailsDto` — id, name, biography, profileUrl, birthday, deathday, placeOfBirth, alsoKnownAs, knownForDepartment, credits.
- `PersonCreditDto` — id, mediaType, title, posterUrl, rating, date, character.

**TmdbClient additions:**

- `getPersonDetails(long id)` → `/person/{id}`
- `getPersonMovieCredits(long id)` → `/person/{id}/movie_credits`
- `getPersonTvCredits(long id)` → `/person/{id}/tv_credits`

**New service and controller:**

- `PersonService` — fetches person details + both credit types, merges movie and TV credits, sorts by date desc then rating desc, deduplicates, caps at 50.
- `PersonController` — `GET /api/person/{id}` with `@Positive` validation.

## Frontend

**New models** (`core/models/`):

- `PersonDetailsDto` — mirrors backend DTO.
- `PersonCreditDto` — mirrors backend DTO.

**KinoApiService:**

- `personDetails(id: number)` → `GET /api/person/{id}`.

**New page** (`pages/person/`):

- `Person` component with profile image, name, known-for department, birthday/deathday/age, place of birth, biography, also-known-as, and a filmography grid.
- Each credit card links to the movie or TV details page based on `mediaType`.
- Age calculation accounts for deathday (shows "Died at" instead of "Age").
- Loading, error, and retry states.

**Route:**

- `person/:id` → `Person` component.

**Details page changes:**

- Cast cards are now `<a>` elements with `routerLink="/person/:id"`.
- Added hover animation (lift + shadow).
- `RouterLink` added to the Details component imports.

## Tests

**Backend (16 new tests):**

- `PersonServiceTest` (9 tests) — mapping, sorting, dedup, cap at 50, null/blank handling, safe-call for failed credit fetches, rating tiebreaker.
- `PersonControllerTest` (4 tests) — 200 response, 400 for invalid id, empty credits list.
- `TmdbClientTest` (3 new tests) — mock server for the 3 new TMDB endpoints.
- `TestDataFactory` — helpers for person details, movie/TV credit fixtures.

**Frontend (11 new tests):**

- `Person` component — loading, error handling, age calculation, date formatting, credit year extraction, link generation for movie/TV credits.

## Verification

| Check | Result |
|-------|--------|
| `cd kino-backend && ./mvnw test` | 157 tests, 0 failures (was 141) |
| `cd kino-frontend && npm test -- --watch=false` | 54 tests, 0 failures (was 43) |
| Backend builds cleanly | ✅ |
| Frontend builds cleanly | ✅ |
| Click cast member → navigates to `/person/:id` | ✅ |
| Person page shows bio, profile image, birthday, place of birth | ✅ |
| Filmography lists movies and TV shows, sorted by date descending | ✅ |
| Dead person shows death date and "Died at" label | ✅ |
| Person with no bio shows gracefully | ✅ |
| Invalid person ID → error state with retry | ✅ |
| Responsive at 320px, 768px, 1440px+ | ✅ |

## Notes / possible follow-ups

- Person search in multi-search was deferred (Option A chosen).
- Crew credits (directing, writing, etc.) not fetched yet.
- No deep links to specific credits on the person page.
- No IMDB/homepage links rendered yet (data is available from TMDB).

---

*End of Week 9 summary.*
