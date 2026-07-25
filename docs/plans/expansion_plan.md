# 🎬 Kino Movie Tracker — Expansion Plan

> **Document version:** 1.0  
> **Last updated:** 2026-07-15  
> **Status:** Draft for review  

---

## 1. Executive Summary

**Kino** is a full-stack movie and TV discovery application built with **Spring Boot (Java 21)** and **Angular 21**. It currently provides a clean, TMDB-powered browsing experience with trending, popular, top-rated, and upcoming content, plus search and detail pages.

This plan outlines a pragmatic, incremental roadmap to evolve Kino from a functional MVP into a feature-rich, production-ready platform. The roadmap is divided into **short-term**, **mid-term**, and **long-term** phases, with realistic deadlines assuming a **small part-time team (1–2 developers)** working roughly 10–15 hours per week.

---

## 2. Current State Assessment

### 2.1 Strengths
- Clean layered architecture (controller → service → TMDB client).
- Proper separation between internal TMDB DTOs and public API DTOs.
- Server-side Caffeine caching for TMDB responses.
- Modern Angular standalone components and SPA routing.
- Docker support for both backend and frontend.
- Good code organization and consistent naming.

### 2.2 Weaknesses & Technical Debt
- **No automated tests** beyond basic context/component creation tests.
- **No input validation** on REST endpoints.
- **Hardcoded CORS** origin (`http://localhost:4200`).
- **No retry/circuit-breaker logic** for TMDB API failures.
- **No rate limiting** or quota protection for TMDB calls.
- **No structured logging** or application monitoring.
- **No API documentation** (Swagger/OpenAPI).
- **No database** — all data is transient and fetched from TMDB.
- **No authentication/authorization** infrastructure.

### 2.3 External Dependencies
- **TMDB API v3** is the sole data source.
- Requires a personal TMDB API key.

---

## 3. Strategic Pillars

All future work should align with these pillars:

1. **User Experience First** — every feature must improve discovery, navigation, or personalization.
2. **Architecture Before Features** — invest in testing, observability, and security before adding heavy social features.
3. **Incremental Delivery** — ship small, vertical slices that are testable and deployable.
4. **Data Independence** — reduce direct TMDB leakage and prepare for a future local database.
5. **Performance & Reliability** — cache smarter, fail gracefully, and monitor health.

---

## 4. Roadmap

### Phase 1 — Foundation & Polish (Weeks 1–4)
**Goal:** Stabilize the codebase, close technical debt, and add the quick-win features already listed in the README.

| Week | Focus | Deliverables |
|------|-------|--------------|
| **W1** | Testing & Quality | Add unit tests for `MovieService`, `TvService`, `SearchService`, and `TmdbClient`. Add controller integration tests with `@WebMvcTest`. Target: ≥60% backend coverage. |
| **W2** | Validation & Configuration | Add Bean Validation on endpoints; externalize CORS origins to `application.properties`; add global logging with SLF4J. |
| **W3** | Sorting & Filtering | Backend: add `sortBy`, `genreIds`, `yearFrom`, `yearTo` query params to list endpoints. Frontend: add sort/filter controls on media list and search pages. |
| **W4** | UI Polish | Loading skeletons, empty/error states, responsive grid improvements, front-page horizontal carousels. |

**Milestone:** A more robust, tested, and polished MVP ready for public demo.

**Deadline:** End of Week 4 (approximately **1 month** from start).

---

### Phase 2 — Richer Media Experience (Weeks 5–10)
**Goal:** Deepen content discovery with trailers, additional metadata, and TV season support.

| Week | Focus | Deliverables |
|------|-------|--------------|
| **W5** | Trailers & Videos | Integrate TMDB `/videos` endpoint; expose video list in `MediaDetailsDto`; add YouTube embed modal on details page. |
| **W6** | Keywords & Recommendations | Add keywords, similar titles, and TMDB recommendations to details page. |
| **W7–W8** | TV Seasons & Episodes | New endpoints: season list, season details, episode list; frontend season selector and episode guide. |
| **W9** | Person Details | Add person/actor detail pages with filmography. |
| **W10** | Responsive & Animation Polish | CSS transitions, carousel touch support, mobile navigation overhaul. |

**Milestone:** Kino becomes a credible TMDB-style discovery experience.

**Deadline:** End of Week 10 (approximately **2.5 months** from start).

---

### Phase 3 — User Accounts & Personalization (Weeks 11–20)
**Goal:** Introduce persistence, authentication, and user-owned data.

| Week | Focus | Deliverables |
|------|-------|--------------|
| **W11–W12** | Database & Auth Infrastructure | Add PostgreSQL; configure Spring Data JPA; implement JWT-based login/registration. |
| **W13–W14** | Watchlist | Users can add/remove movies and TV shows to a personal watchlist; persisted in DB. |
| **W15–W16** | Favorites & Ratings | Favorite titles; 1–10 star ratings stored locally. |
| **W17–W18** | Reviews | Users can write text reviews; review list on detail pages. |
| **W19–W20** | User Profile & Activity | Profile page showing watchlist, favorites, ratings, and recent reviews. |

**Milestone:** Kino transitions from a discovery app to a personal tracking app.

**Deadline:** End of Week 20 (approximately **5 months** from start).

---

### Phase 4 — Social & Discovery Intelligence (Weeks 21–30)
**Goal:** Add social features and smarter recommendations.

| Week | Focus | Deliverables |
|------|-------|--------------|
| **W21–W23** | Follow System | Users can follow other users; public profiles. |
| **W24–W25** | Activity Feed | Feed of friends' ratings, reviews, and watchlist additions. |
| **W26–W27** | Recommendation Engine v1 | Simple collaborative/content-based recommendations from local ratings + TMDB genres. |
| **W28–W29** | Lists & Collections | Users can create custom lists (e.g., “Best Horror 2026”). |
| **W30** | Notifications | In-app notifications for follows and list collaborations. |

**Milestone:** A social movie-tracking platform comparable to a lightweight Letterboxd.

**Deadline:** End of Week 30 (approximately **7.5 months** from start).

---

### Phase 5 — Production Hardening & Scale (Weeks 31–40)
**Goal:** Make Kino deployable, observable, and scalable.

| Week | Focus | Deliverables |
|------|-------|--------------|
| **W31–W32** | DevOps & CI/CD | GitHub Actions pipeline for build, test, and Docker image publishing. |
| **W33–W34** | Observability | Structured logging with correlation IDs; metrics with Micrometer + Prometheus; health dashboards. |
| **W35–W36** | Caching & Resilience | Redis-backed distributed cache; Resilience4j retry/circuit breaker/rate limiter for TMDB. |
| **W37–W38** | Security Hardening | HTTPS enforcement, input sanitization, CSRF protection, secrets management. |
| **W39–W40** | Performance & SEO | Server-side rendering (Angular SSR) or prerendering for public pages; image optimization; lazy loading audit. |

**Milestone:** Production-ready deployment with monitoring, security, and performance guarantees.

**Deadline:** End of Week 40 (approximately **10 months** from start).

---

## 5. Technical Architecture Recommendations

### 5.1 Backend
- Keep the existing **controller → service → TmdbClient** layering.
- Introduce a **local database** (PostgreSQL) in Phase 3 for users, watchlists, ratings, and reviews.
- Use **Spring Data JPA** for persistence; keep TMDB DTOs separate from entity models.
- Add **MapStruct** or manual mappers to reduce mapping boilerplate as entities grow.
- Replace Caffeine with **Redis** once user sessions and distributed caching are needed.
- Adopt **Resilience4j** for retry, circuit breaker, and rate limiting around `TmdbClient`.

### 5.2 Frontend
- Continue with **standalone components** and lazy-loaded routes.
- Introduce a lightweight **state management** solution (e.g., Angular Signals or NgRx) when user data grows.
- Create a shared **UI component library** (cards, carousels, skeletons, modals) to avoid duplication.
- Add **Angular SSR** or prerendering for SEO-critical public pages.

### 5.3 DevOps
- Use **Docker Compose** for local development with PostgreSQL and Redis.
- Add **GitHub Actions** for CI/CD.
- Deploy backend to **Railway, Render, Fly.io, or AWS ECS**; frontend to **Vercel/Netlify** or served via Nginx in the same Docker stack.

---

## 6. Risk Management

| Risk | Impact | Mitigation |
|------|--------|------------|
| TMDB API quota or breaking changes | High | Abstract TMDB client; add caching, retries, and graceful degradation. |
| Scope creep on social features | High | Strictly gate Phase 4 until Phase 3 is complete and stable. |
| Lack of tests slows refactoring | Medium | Make testing the first priority in Phase 1. |
| Authentication/security vulnerabilities | High | Use proven libraries (Spring Security, JWT); conduct a security review before public launch. |
| Part-time availability | Medium | Keep phases small and vertical; each phase should produce a shippable increment. |

---

## 7. Success Metrics

| Phase | Key Metrics |
|-------|-------------|
| Phase 1 | Backend test coverage ≥60%; zero hardcoded dev URLs; all endpoints validated. |
| Phase 2 | Trailer/season features shipped; Lighthouse score ≥80 on mobile. |
| Phase 3 | User registration/login working; watchlist/favorite/rating features complete. |
| Phase 4 | Social graph functional; recommendation endpoint returning results. |
| Phase 5 | CI/CD green; <200ms p95 API latency; 99% uptime target. |

---

## 8. Immediate Next Steps (This Week)

1. Create a `tests/` backlog and add the first service-level unit tests.
2. Externalize CORS configuration.
3. Add SLF4J logging to `TmdbClient` and services.
4. Open a design discussion for the sorting/filtering API contract.
5. Set up a `docker-compose.yml` with backend + frontend + PostgreSQL (for future use).

---

## 9. Notes

- Deadlines assume part-time effort. Adjust by compressing or extending phases based on actual capacity.
- The roadmap intentionally defers heavy social features until a solid auth/persistence foundation exists.
- Regular retrospectives at the end of each phase are recommended to re-prioritize based on user feedback.

---

*End of expansion plan.*