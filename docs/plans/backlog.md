# Backlog

Known issues, deferred work and follow-ups. Not a commitment — just so nothing is forgotten.

## Deferred

- ⏸️ **DevOps / KIII deployment.** Dockerfile hardening, GitHub Actions CI/CD and the Kubernetes manifests currently live on a separate branch (`feature/kiii-deployment`) that is **not** merged into `main`. The plan is to clean it up and re-apply it deliberately later. Until then, [devops/kiii-guide.md](../devops/kiii-guide.md) and [devops/kiii-report.md](../devops/kiii-report.md) describe a target state that is not yet in the repository. Do not submit the report as-is without first landing the branch.
  - When re-applying, standardise on a single Kubernetes namespace (`kino-kiii`).
  - Verify the frontend build arg for the API URL before merging (the branch changed `environment.prod.ts` to `/api`; `main` now also does).
- ⏸️ **CI on `main`.** The GitHub Actions workflow exists only on the deferred branch. Add a build/test workflow to `main` when the DevOps branch is re-applied.

## Known limitations

- **Filtering scope.** `MediaFilterService` filters and sorts only the current TMDB page (20 items); it does not fetch additional pages to fill a filtered result set. `totalPages`/`totalResults` are passed through from TMDB, so paging metadata stays correct. The UI states this explicitly: “Filters and sorting apply to the current page of results.” Fetching multiple pages per request remains a possible future improvement.

## Tooling / repo

- None open. `LICENSE` is MIT (confirmed) and [CHANGELOG.md](../../CHANGELOG.md) now tracks notable changes.

## Docs

- None open. [ROADMAP.md](../ROADMAP.md) status markers are current; Week 6’s completion note reflects the shipped cap of 12; Week 7–8 is documented in [weekly/week7-8-tv-seasons-episodes.md](../weekly/week7-8-tv-seasons-episodes.md).
