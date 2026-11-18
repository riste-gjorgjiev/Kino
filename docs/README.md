# Kino Documentation

This folder holds all planning, architecture and process documentation for **Kino**, a full-stack movie & TV discovery app (Spring Boot + Angular, powered by the TMDB API).

## Reading order

1. **[ROADMAP.md](ROADMAP.md)** — the progression plan: current state, phases, and what comes next. Start here.
2. **[ARCHITECTURE.md](ARCHITECTURE.md)** — how the code is organised: backend layering, DTO rules, frontend structure, API surface.
3. **[RUNNING.md](RUNNING.md)** — the single source of truth for running and testing the app (local + Docker).
4. **[plans/backlog.md](plans/backlog.md)** — known issues and deferred cleanups.
5. **[weekly/](weekly/)** — week-by-week history (plans and completion summaries), in chronological order.
6. **[devops/](devops/)** — the KIII deployment guide and report.
7. **[../CHANGELOG.md](../CHANGELOG.md)** — notable changes, newest first.

## Status legend

Used throughout the docs:

| Marker | Meaning |
|--------|---------|
| ✅ Done | Implemented and verified |
| 🚧 In progress | Partially implemented |
| ⬜ Not started | Planned only |
| ⏸️ Deferred | Intentionally postponed |

## Weekly index

| Week | Plan | Summary |
|------|------|---------|
| 3 | — | [Sorting & filtering](weekly/week3-sorting-filtering.md) |
| 4 | [Plan](weekly/week4-plan.md) | [Summary](weekly/week4-summary.md) |
| 5 | [Trailers & videos](weekly/week5-trailers.md) | (checklist completed in the plan) |
| 6 | [Keywords & recommendations](weekly/week6-keywords-recommendations.md) | (completion note at end of the plan) |
| 7–8 | [TV seasons & episodes](weekly/week7-8-tv-seasons-episodes.md) | (summary in the same document) |

> Note: `docs/` is tracked in git. It is **not** ignored, and the DevOps guide/report are part of the public repository.

## DevOps docs

| Document | Purpose |
|----------|---------|
| [kiii-guide.md](devops/kiii-guide.md) | Canonical step-by-step deployment implementation guide |
| [kiii-report.md](devops/kiii-report.md) | Course submission report text |

> ⏸️ **Deferred:** the DevOps/KIII work (Docker hardening, GitHub Actions, Kubernetes manifests) is not currently merged into `main`. It lives on a separate branch and will be cleaned up and re-applied later. See [plans/backlog.md](plans/backlog.md).
