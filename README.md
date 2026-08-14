# 🎬 Kino – Movie & TV Discovery App

**Kino** is a full-stack web application for discovering **Movies** and **TV shows**, inspired by platforms like **TMDB** and **Letterboxd**.
It uses **Spring Boot (Java)** for the backend and **Angular** for the frontend, with all media data powered by the **TMDB API**.

## 🌐 Live Demo

**Frontend:** https://kino-one-eta.vercel.app/

**Backend:** https://kino-wt69.onrender.com

> ⚠️ **Note:** The backend is hosted on Render's free tier, which goes to sleep after inactivity. The first request may take around a minute to load as the service wakes up. Please be patient if the initial load seems slow.

## 🛠 Tech Stack
Backend

- **Java 21**
- **Spring Boot 4.0.2**
- **Spring Web (RestClient)**
- **Jackson 3.x (JSON mapping)**
- **Caffeine (caching)**
- **TMDB API**
- **JUnit 5 + Mockito (testing)**

Frontend
- **Angular 21 (standalone components)**
- **Angular Router**
- **HttpClient**
- **RxJS**
- **Vitest (testing)**

---

## ✅ Implemented Features
**Backend**

- TMDB API client abstraction (TmdbClient)
- Unified API DTOs (clean API, no TMDB leakage)
- Movie & TV endpoints:
  - Trending movies & TV
  - Popular movies & TV
  - Upcoming movies
  - On-the-air TV shows
  - Top-rated movies & TV

- Search:
  - Movies search
  - TV search
  - Multi (All) search

- Media details endpoints:
  - Movie details (overview, genres, runtime, directors, cast, etc.)
  - TV show details (overview, genres, creators, cast, etc.)
- Environment-based configuration (.env, application.properties)
- Image URL resolution handled server-side

**Frontend**
- Home page with trending movies & TV
- Media list pages (Movies / TV):
  - Popular
  - Top Rated
  - Upcoming (movies)
  - On TV (tv shows)
- Search page:
  - All / Movies / TV tabs
  - Pagination per tab
- Media details page:
  - Backdrop hero section
  - Poster, title, tagline, overview
  - Genres, rating, runtime/status
  - Directors (movies)
  - Creators (TV)
  - Horizontally scrollable cast list
- SPA navigation with Angular Router
- Shared API service (KinoApiService)
- Strong typing with frontend DTOs matching backend responses

```
📂 Project Structure
Kino/
├── kino-backend/
│   ├── controller/
│   ├── service/
│   ├── tmdb/
│   ├── dto/
│   │   ├── api/
│   │   └── tmdb/
│   └── KinoApplication.java
│
├── kino-frontend/
│   ├── src/app/
│   │   ├── pages/
│   │   │   ├── home/
│   │   │   ├── media-list-page/
│   │   │   ├── details/
│   │   │   └── search/
│   │   ├── core/models/
│   │   ├── kino-api.service.ts
│   │   └── app.routes.ts
│   └── angular.json
```
## 🔐 Environment Variables
Create a .env file (backend):
```
TMDB_API_KEY=your_tmdb_api_key
```

Example application.properties:
```
tmdb.base-url=https://api.themoviedb.org/3
tmdb.image-base-url=https://image.tmdb.org/t/p/w500
```

## ▶️ Running the Project
**Backend**
```
cd kino-backend
./mvnw spring-boot:run
```
Runs on: http://localhost:8080

**Frontend**
```
cd kino-frontend
npm install
ng serve
```
Runs on: http://localhost:4200

## 🧪 Running Tests
**Backend**
```
cd kino-backend
./mvnw test
```

**Frontend**
```
cd kino-frontend
npm test
```

## 📌 Notes
- The project intentionally separates TMDB DTOs from API DTOs to keep the frontend independent from TMDB’s data format.
- No authentication is implemented yet — the focus is on clean architecture and API design first.
