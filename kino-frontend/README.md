# kino-frontend

The Angular 21 single-page application for **Kino**.

For project overview, architecture, and full run/test instructions, see the repository root:

- [README](../README.md)
- [docs/ARCHITECTURE.md](../docs/ARCHITECTURE.md)
- [docs/RUNNING.md](../docs/RUNNING.md)

## Quick start

```bash
npm install
npm start     # http://localhost:4200
npm test      # Vitest
npm run build # production build
```

The dev build talks to the backend at `http://localhost:8080/api` (see `src/environments/environment.ts`). The production build uses the relative `/api` path, resolved by nginx (Docker) or `vercel.json`.
