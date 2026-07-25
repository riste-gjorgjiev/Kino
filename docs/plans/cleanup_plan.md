# 🧹 Kino Codebase Cleanup Plan

> **Document version:** 1.0  
> **Last updated:** 2026-07-15  
> **Status:** Ready for execution  

This plan lists duplicate, redundant, and dead files/code identified during a full directory scan, organized by priority and execution order. Each item includes the affected path(s), the problem, the recommended action, and a verification step.

---

## How to Use This Plan

1. Work through items in priority order (P0 → P1 → P2).
2. Make one logical change per commit so rollbacks are easy.
3. Run the verification command after each group of changes.
4. Do **not** delete test stubs unless you are replacing them with real tests.

---

## P0 — Critical Cleanup (Do First)

These items cause real runtime issues, build warnings, or confusion and should be fixed immediately.

### 1. Remove duplicate CORS configuration
- **Files:**
  - `kino-backend/src/main/java/mk/ukim/finki/wp/kino/config/CorsConfig.java`
  - `kino-backend/src/main/java/mk/ukim/finki/wp/kino/config/WebConfig.java`
- **Problem:** Both classes register a CORS mapping for `/api/**` pointing to `http://localhost:4200`. Spring Boot applies both, creating overlapping/duplicate CORS filters.
- **Action:**
  1. Decide on the canonical implementation. `CorsConfig.java` is the better name and already supports more HTTP methods.
  2. Move any missing behavior from `WebConfig.java` into `CorsConfig.java` if needed (e.g., ensure `OPTIONS` is allowed).
  3. Delete `WebConfig.java`.
- **Verification:** Start the backend and confirm frontend requests still succeed with no CORS errors in the browser console.

### 2. Remove duplicate Maven dependency
- **File:** `kino-backend/pom.xml`
- **Problem:** `spring-boot-starter-cache` is declared twice (first block around line 34 and second block around line 61).
- **Action:** Delete the second occurrence (lines 61–64) and keep the first.
- **Verification:** Run `./mvnw clean compile` from `kino-backend/` and confirm no warnings about duplicate dependencies.

### 3. Delete stale backup configuration file
- **File:** `opencode.json.bak`
- **Problem:** Backup file referencing an old model name (`qwen-3.7-plus`). The active configuration lives under `.opencode/opencode.json`.
- **Action:** Delete `opencode.json.bak`.
- **Verification:** Confirm the file no longer appears in the repository root.

---

## P1 — Important Cleanup (Do Next)

These items improve code quality, reduce bundle noise, and remove developer-facing inconsistencies.

### 4. Remove unused imports
- **Files:**
  - `kino-frontend/src/app/app.config.ts` — `provideZonelessChangeDetection` is imported but never used.
  - `kino-frontend/src/app/pages/home/home.ts` — `combineLatest` is imported from `rxjs/internal/operators/combineLatest` but never used.
- **Action:** Remove the unused import lines.
- **Verification:** Run `ng build` from `kino-frontend/` and confirm no TypeScript/ESLint warnings about unused imports.

### 5. Remove debug `console.log` statements
- **Files:**
  - `kino-frontend/src/app/pages/media-list-page/media-list-page.ts` — line 44: `console.log('SUBSCRIBE', req)`
  - `kino-frontend/src/app/pages/details/details.ts` — line 25: `console.log('DETAILS INIT', this.router.url);`
- **Problem:** Debug logs leak into production builds and clutter the browser console.
- **Action:** Delete both `console.log` lines. Keep the `console.error(err)` in `details.ts` because it handles real errors.
- **Verification:** Search the frontend source for `console.log` and confirm only intentional logs remain:
  ```bash
  grep -R "console.log" kino-frontend/src
  ```

### 6. Fix application titles
- **Files:**
  - `kino-frontend/src/index.html` — `<title>KinoFrontend</title>`
  - `kino-frontend/src/app/app.ts` — `title = signal('kino-frontend');`
- **Problem:** Both still use the Angular CLI default project name instead of the product name.
- **Action:** Update both to `"Kino"`.
- **Verification:** Run the frontend and confirm the browser tab shows **Kino**.

---

## P2 — Review & Optional Cleanup

These items are not strictly wrong but should be reviewed and either fixed or documented.

### 7. Decide the fate of `styles.css`
- **File:** `kino-frontend/src/styles.css`
- **Problem:** Contains only the default Angular comment, no actual global styles.
- **Action options:**
  - **A.** Add real global styles (reset, CSS variables, typography) and keep the file.
  - **B.** Delete the file and remove its reference from `angular.json` if it is unused.
- **Verification:** After option B, run `ng build` and confirm no missing stylesheet errors.

### 8. Consolidate `.gitignore` entries
- **Files:**
  - `.gitignore`
  - `kino-frontend/.gitignore`
- **Problem:** Overlapping rules (`.idea/`, `.project`, `.classpath`, `.vscode/`, `node_modules`, `dist/`) are defined in both files.
- **Action:**
  1. Keep backend-oriented ignores in the root `.gitignore`.
  2. Keep only frontend-specific entries in `kino-frontend/.gitignore` (e.g., `/dist`, `/tmp`, `/out-tsc`, `/.angular/cache`, `/coverage`).
  3. Remove duplicates from the frontend file that are already covered by the root file.
- **Verification:** Run `git status` and confirm no untracked build artifacts appear.

### 9. Address empty test stubs
- **Files:**
  - `kino-backend/src/test/java/mk/ukim/finki/wp/kino/KinoApplicationTests.java`
  - `kino-frontend/src/app/pages/**/*.spec.ts`
  - `kino-frontend/src/app/app.spec.ts`
- **Problem:** All tests are minimal stubs. They do not assert meaningful behavior.
- **Action options:**
  - **A.** Keep stubs as placeholders and expand them incrementally (recommended if tests are coming in Phase 1 of the expansion plan).
  - **B.** Delete stubs now and add real tests later.
- **Recommendation:** Keep the stubs for now but do not consider the project "tested" until they are replaced or extended.

### 10. Document backend/frontend DTO mirroring
- **Files:**
  - `kino-backend/src/main/java/mk/ukim/finki/wp/kino/dto/api/*.java`
  - `kino-frontend/src/app/core/models/*.ts`
- **Problem:** DTOs are intentionally mirrored across backend and frontend, which is correct but easy to forget during maintenance.
- **Action:** Add a short `ARCHITECTURE.md` note or inline comment explaining that changes to backend API DTOs must be reflected in frontend models.
- **Verification:** No build verification needed; this is a documentation task.

---

## Cleanup Execution Checklist

- [ ] P0.1 — Remove duplicate CORS config (`WebConfig.java` deleted)
- [ ] P0.2 — Remove duplicate `spring-boot-starter-cache` in `pom.xml`
- [ ] P0.3 — Delete `opencode.json.bak`
- [ ] P1.4 — Remove unused imports in `app.config.ts` and `home.ts`
- [ ] P1.5 — Remove debug `console.log` statements
- [ ] P1.6 — Update titles to "Kino"
- [ ] P2.7 — Decide and act on `styles.css`
- [ ] P2.8 — Consolidate `.gitignore` files
- [ ] P2.9 — Decide policy on empty test stubs
- [ ] P2.10 — Document DTO mirroring convention

---

## Final Verification Commands

After completing the cleanup, run these commands to ensure the project still builds and behaves correctly:

```bash
# Backend
cd kino-backend
./mvnw clean test compile

# Frontend
cd ../kino-frontend
npm install
ng build
```

Then start both services and smoke-test:
- Home page loads trending movies and TV.
- Media list pages load without CORS errors.
- Details page loads without console debug logs.
- Browser tab title shows **Kino**.

---

*End of cleanup plan.*
