# Week 5 Plan: Trailers & Videos

## Decisions
- **Stack:** Angular + CSS (no Tailwind, no React migration)
- **Video filtering/sorting:** Backend (Java Spring Boot)
- **Max videos returned:** 12
- **Modal:** Native Angular component

## Backend Tasks
- [x] Create `TmdbVideoDto` (TMDB response mapping)
- [x] Create `VideoDto` (API response shape)
- [x] Add `getMovieVideos(long id)` and `getTvVideos(long id)` to `TmdbClient`
- [x] Add `videos` field to `MediaDetailsDto`
- [x] Update `MediaDetailsService` to fetch videos, filter YouTube + allowed types, sort by priority, cap at 12
- [x] Update `TestDataFactory` with video test data
- [x] Add/update backend unit tests

## Frontend Tasks
- [x] Create `VideoDto` model in `src/app/core/models/video-dto.ts`
- [x] Create `VideoCardComponent`
- [x] Create `VideoSectionComponent`
- [x] Create `TrailerModalComponent`
- [x] Integrate components into `Details` page
- [x] Add responsive CSS for video gallery and modal
- [x] Add frontend unit tests

## Verification
- [x] Backend tests pass
- [x] Frontend tests pass
- [x] Manual responsive check: 320px, 768px, 1440px+
