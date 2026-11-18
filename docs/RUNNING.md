# Running & Testing Kino

The single source of truth for running the app. Other docs link here instead of repeating these commands.

## Prerequisites

- Java 21
- Node.js 22 + npm
- Docker + Docker Compose (optional, for the containerised stack)
- A TMDB API key — https://www.themoviedb.org/settings/api

## Environment setup

Create a `.env` file in the **repository root** (used by Docker Compose) and one in `kino-backend/` (used by Spring Boot locally):

```bash
cp .env.example .env
# then edit .env and set TMDB_API_KEY=your_real_key
```

`.env` is gitignored — never commit real keys. `.env.example` is the tracked template.

Allowed frontend origins are configured with `KINO_CORS_ALLOWED_ORIGINS` (comma-separated). The default in `application.properties` allows `http://localhost:4200` and the deployed Vercel origin; set the variable to add or replace origins without touching code.

## Local development

Backend:

```bash
cd kino-backend
./mvnw spring-boot:run
# http://localhost:8080  (API under /api, health at /actuator/health)
```

Frontend (separate terminal):

```bash
cd kino-frontend
npm install
npm start
# http://localhost:4200
```

The dev frontend calls `http://localhost:8080/api` directly (see `src/environments/environment.ts`).

## Tests

Backend (hermetic — no `.env` required):

```bash
cd kino-backend
./mvnw test
```

Frontend:

```bash
cd kino-frontend
npm test -- --watch=false
```

End-to-end (Playwright, mocked API — no backend or TMDB key needed):

```bash
cd kino-frontend
npm run test:e2e
```

The config starts `ng serve` on `127.0.0.1:4200` automatically. If Playwright's bundled browser is unavailable, point it at a system Chromium:

```bash
PLAYWRIGHT_CHROMIUM_PATH=/usr/bin/chromium npm run test:e2e
```

Production build:

```bash
cd kino-frontend
npm run build
```

## Docker Compose

```bash
docker compose up -d --build
docker compose ps
docker compose logs -f
docker compose down
```

- Frontend: http://localhost:4200 (nginx serves the SPA and proxies `/api/` to the backend container)
- Backend: http://localhost:8080

The production frontend build uses the relative `/api` URL, which the nginx config proxies to `backend:8080`.

## Deployed environments

- Frontend: https://kino-one-eta.vercel.app/ (Vercel proxies `/api` to the backend via `vercel.json`)
- Backend: https://kino-wt69.onrender.com (Render free tier — first request may take ~1 minute to wake)

## Troubleshooting

| Symptom | Cause / fix |
|---------|-------------|
| `401`/`Invalid API key` from TMDB | `TMDB_API_KEY` missing or wrong in `.env` |
| `docker compose up` asks for `TMDB_API_KEY` | Create the root `.env` (see above) |
| Frontend can't reach the API locally | Backend not running on :8080, or CORS origin missing from `kino.cors.allowed-origins` |
| Backend tests fail on context startup | Should not happen — tests are hermetic. If it does, check `kino-backend/src/test/resources/application.properties` |
