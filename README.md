# 🎬 Kino – Movie & TV Discovery App

**Kino** is a full-stack web application for discovering **Movies** and **TV shows**, inspired by platforms like **TMDB** and **Letterboxd**.
It uses **Spring Boot (Java)** for the backend and **Angular** for the frontend, with all media data powered by the **TMDB API**.

## 🌐 Live Demo

**Frontend:** https://kino-one-eta.vercel.app/
**Backend:** https://kino-wt69.onrender.com

> ⚠️ **Note:** The backend is hosted on Render's free tier, which sleeps after inactivity. The first request may take around a minute to wake up.

## 🛠 Tech Stack

**Backend**
- Java 21
- Spring Boot 4.0.2
- Spring Web (RestClient)
- Jackson (JSON mapping)
- Caffeine (caching)
- Bean Validation
- TMDB API
- JUnit 5 + Mockito

**Frontend**
- Angular 21 (standalone components)
- Angular Router
- HttpClient + RxJS
- Vitest

## ✅ Implemented Features

**Backend**
- TMDB API client abstraction (`TmdbClient`)
- Unified API DTOs — no TMDB leakage to the frontend
- Movie & TV endpoints: trending, popular, top-rated, upcoming (movies), on-the-air (TV)
- Search: movies, TV, and multi (all)
- Media details: overview, genres, runtime/status, directors (movies), creators (TV), cast, trailers/videos, keywords, and merged recommendations + similar titles
- TV seasons: season list, season details and episode list endpoints
- Filtering by year range and sorting by rating/date/title, with pagination
- Server-side image URL resolution
- Caffeine caching and a global exception handler
- Environment-based configuration (`.env`, `application.properties`)

**Frontend**
- Home page with trending movies & TV carousels
- Media list pages (Movies / TV): popular, top-rated, upcoming, on-the-air
- Filter & sort controls with URL synchronisation
- Search page with All / Movies / TV tabs and per-tab pagination
- Details page: backdrop hero, poster, tagline, overview, genres, rating, runtime/status, cast, video gallery with trailer modal, keyword chips, and a "More Like This" carousel
- TV details season selector and episode guide (episode codes, air dates, runtimes, ratings)
- Loading / empty / error states on every page
- Responsive layout

## 📂 Project Structure

```
Kino/
├── kino-backend/                 # Spring Boot API
│   └── src/main/java/mk/ukim/finki/wp/kino/
│       ├── controller/           # REST endpoints
│       ├── service/              # mapping, filtering, enrichment, caching
│       ├── tmdb/TmdbClient.java  # TMDB HTTP client
│       ├── dto/api/              # public API models
│       ├── dto/tmdb/             # internal TMDB models
│       ├── config/               # CORS, cache, RestClient, logging
│       └── exception/            # global error handling
│
├── kino-frontend/                # Angular SPA
│   └── src/app/
│       ├── pages/                # home, media-list-page, search, details
│       ├── shared/               # media-card, media-carousel, filter-controls,
│       │                         # video-*, keyword-section, trailer-modal,
│       │                         # season-episode-guide
│       ├── core/models/          # DTOs mirroring the backend API
│       ├── kino-api.service.ts   # HttpClient wrapper
│       └── app.routes.ts
│
├── docs/                         # roadmap, architecture, weekly history, devops
└── docker-compose.yml
```

## 🔐 Environment Variables

Copy the template and add your TMDB key:

```bash
cp .env.example .env
```

```
TMDB_API_KEY=your_tmdb_api_key
```

Get a key from https://www.themoviedb.org/settings/api. `.env` is gitignored — never commit real keys.

## ▶️ Running the Project

See **[docs/RUNNING.md](docs/RUNNING.md)** for full instructions (local dev, tests, and Docker Compose).

Quick version:

```bash
# Backend
cd kino-backend && ./mvnw spring-boot:run     # http://localhost:8080

# Frontend
cd kino-frontend && npm install && npm start  # http://localhost:4200

# Or everything with Docker
docker compose up -d --build
```

## 🧪 Running Tests

```bash
cd kino-backend && ./mvnw test                # backend
cd kino-frontend && npm test -- --watch=false # frontend
cd kino-frontend && npm run test:e2e          # Playwright smoke test (mocked API)
```

Backend tests are hermetic and do not require a TMDB key. The E2E suite mocks the API, so it does not need the backend or a key either.

## 📚 Documentation

- [docs/README.md](docs/README.md) — documentation index
- [docs/ROADMAP.md](docs/ROADMAP.md) — progression plan and current status
- [docs/ARCHITECTURE.md](docs/ARCHITECTURE.md) — how the code is organised
- [docs/RUNNING.md](docs/RUNNING.md) — run & test guide
- [docs/plans/backlog.md](docs/plans/backlog.md) — known issues & deferred work
- [CHANGELOG.md](CHANGELOG.md) — notable changes

## 📌 Notes

- The project intentionally separates TMDB DTOs from API DTOs to keep the frontend independent of TMDB's data format.
- No authentication is implemented yet — the focus is on clean architecture and API design first.

## 📄 License

[MIT](LICENSE)
