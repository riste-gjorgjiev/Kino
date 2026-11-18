# Backlog

Known issues, deferred work and follow-ups. Not a commitment — just so nothing is forgotten.

## Deferred

- ⏸️ **DevOps / KIII deployment.** Dockerfile hardening, GitHub Actions CI/CD and the Kubernetes manifests currently live on a separate branch (`feature/kiii-deployment`) that is **not** merged into `main`. The plan is to clean it up and re-apply it deliberately later. Until then, [devops/kiii-guide.md](../devops/kiii-guide.md) and [devops/kiii-report.md](../devops/kiii-report.md) describe a target state that is not yet in the repository. Do not submit the report as-is without first landing the branch.
  - When re-applying, standardise on a single Kubernetes namespace (`kino-kiii`).
  - Verify the frontend build arg for the API URL before merging (the branch changed `environment.prod.ts` to `/api`; `main` now also does).

## Known issues

- **Filtering/pagination semantics.** `MediaFilterService` filters and sorts only the current TMDB page (20 items), and `page` is always passed as `1` into the filter call. `totalPages` is therefore computed from a single page's subset. Works, but is misleading for large result sets. Consider fetching multiple TMDB pages or documenting the limitation in the UI.
- **Recommendations cap mismatch.** The details page merges TMDB *recommendations* + *similar* and caps at **8** items, while the Week 6 plan says **12**. Pick one and align the plan.
- **CORS allow-list.** `kino.cors.allowed-origins` includes a wildcard Vercel preview pattern; tighten before production.

## Code cleanups (small, optional)

- `fullImageUrl(...)` is duplicated in `MovieService`, `TvService`, `SearchService` and `MediaDetailsService`. Extract a shared image-URL helper (or a small mapper component).
- `MovieService`/`TvService` still each have a private `toMediaCard` mapping method; a shared `MediaCardMapper` would remove the last of the duplication.
- `SearchService.toMultiMediaCard` re-implements movie/TV mapping already present in the other two services.

## Testing

- Add frontend tests for `media-carousel` and `media-card` (currently untested).
- Add an end-to-end smoke test (Playwright/Cypress) covering home → list → details → search.

## Tooling / repo

- Add CI to `main` (the workflow exists only on the deferred branch).
- Consider a `CHANGELOG.md` or GitHub Releases instead of weekly plan files once the project is feature-complete.
- `LICENSE` added; confirm the chosen license is the intended one.

## Docs

- `docs/weekly/week6-keywords-recommendations.md` is a *plan* with no completion summary; add a short "what actually shipped" section (feature landed in commit `c035472`).
- Keep [ROADMAP.md](../ROADMAP.md) status markers up to date as phases complete.
