🎬 Kino – Movie & TV Discovery App

Kino is a full-stack web application for discovering movies and TV shows, inspired by platforms like TMDB and Letterboxd.
It uses Spring Boot (Java) for the backend and Angular for the frontend, with all media data powered by the TMDB API.

🛠 Tech Stack
Backend

Java 21

Spring Boot

Spring Web (RestClient)

Jackson (JSON mapping)

TMDB API

Frontend

Angular (standalone components)

Angular Router

HttpClient

RxJS

✅ Implemented Features
Backend

TMDB API client abstraction (TmdbClient)

Unified API DTOs (clean API, no TMDB leakage)

Movie & TV endpoints:

Trending movies & TV

Popular movies & TV

Upcoming movies

On-the-air TV shows

Top-rated movies & TV

Search:

Movies search

TV search

Multi (All) search

Media details endpoints:

Movie details (overview, genres, runtime, directors, cast, etc.)

TV show details (overview, genres, creators, cast, etc.)

Clean API design using kebab-case URLs

Environment-based configuration (.env, application.properties)

Image URL resolution handled server-side

Frontend

Home page with trending movies & TV

Media list pages (Movies / TV):

Popular

Top Rated

Upcoming (movies)

On TV (tv shows)

Search page:

All / Movies / TV tabs

Pagination per tab

Media details page:

Backdrop hero section

Poster, title, tagline, overview

Genres, rating, runtime/status

Directors (movies)

Creators (TV)

Horizontally scrollable cast list

SPA navigation with Angular Router

Shared API service (KinoApiService)

Strong typing with frontend DTOs matching backend responses

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

🔐 Environment Variables

Create a .env file (backend):

TMDB_API_KEY=your_tmdb_api_key


Example application.properties:

tmdb.base-url=https://api.themoviedb.org/3
tmdb.image-base-url=https://image.tmdb.org/t/p/w500

▶️ Running the Project
Backend
cd kino-backend
./mvnw spring-boot:run


Runs on: http://localhost:8080

Frontend
cd kino-frontend
npm install
ng serve


Runs on: http://localhost:4200

🚧 Planned Features
Short-term

Sorting:

Popularity (asc/desc)

Rating (asc/desc)

Release / First Air Date

Title (A–Z / Z–A)

Filtering:

By genres

By release date range

Front page horizontal carousels

Improved loading skeletons

Mid-term

Trailer playback (TMDB videos)

Keywords & additional metadata

Seasons & episodes view for TV shows

Responsive polish & animations

Long-term

User accounts

Watchlist / favorites

Ratings & reviews

Recommendations

📌 Notes

The project intentionally separates TMDB DTOs from API DTOs to keep the frontend independent from TMDB’s data format.

No authentication is implemented yet — the focus is on clean architecture and API design first.