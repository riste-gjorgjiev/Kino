# Kino Movie Tracker — DevOps Deployment Report

**Course:** KIII — Cloud Application Architecture and DevOps  
**Project:** Kino Movie Tracker  
**GitHub Repository:** https://github.com/riste-gjorgjiev/Kino  
**Branch:** `feature/kiii-deployment`  
**Author:** Riste Gjorgjiev  
**Date:** September 2026

---

> ⚠️ **Draft status:** the DevOps artefacts described in this report (Dockerfiles hardening, GitHub Actions workflow, `k8s/` manifests) currently live on the `feature/kiii-deployment` branch and are **not merged into `main`**. Before submitting, either land that branch or update this report to match what is actually in the repository. See [../plans/backlog.md](../plans/backlog.md).

---

## 1. Introduction

### 1.1 What Kino Is

Kino Movie Tracker is a full-stack web application designed for discovering and exploring movies and television shows. Inspired by popular platforms such as TMDB and Letterboxd, Kino provides users with an interactive interface to browse trending, popular, upcoming, and top-rated media content, as well as to search for specific titles and view detailed information about each entry. The application aggregates all media metadata — including posters, backdrops, cast, crew, genres, ratings, and runtime information — from The Movie Database (TMDB) public API, presenting it through a clean and responsive single-page application (SPA) frontend.

The project was developed as part of a university software engineering curriculum, with a focus on building a clean, layered architecture, implementing a RESTful backend API, and delivering a modern Angular frontend. The work documented in this report extends the original application by introducing a complete containerization and deployment pipeline, transforming the project from a locally runnable prototype into a cloud-ready, reproducible system.

### 1.2 Technology Stack

The Kino Movie Tracker is built on a contemporary, widely adopted enterprise technology stack:

- **Backend:** Spring Boot 4.0.2, Java 21, Maven 3.9.9
- **Frontend:** Angular 21, TypeScript, RxJS, standalone components, Vitest for unit testing
- **Data Source:** TMDB REST API
- **Database (infrastructure-only):** PostgreSQL 16
- **Containerization:** Docker with multi-stage builds
- **Container Orchestration:** Docker Compose for local development
- **CI/CD:** GitHub Actions
- **Container Registry:** DockerHub
- **Container Orchestration (cloud):** Kubernetes with NGINX Ingress
- **Reverse Proxy / Static File Server:** Nginx

### 1.3 Goal of the DevOps Work

The primary objective of this DevOps phase was to prepare the Kino Movie Tracker for reliable, repeatable, and scalable deployment. The specific goals included:

1. **Containerizing both the backend and frontend** using Docker so that the application can run consistently across different environments without requiring local installations of Java, Node.js, or Angular CLI.
2. **Orchestrating multi-service local deployments** with Docker Compose, integrating the Spring Boot backend, Angular frontend, and PostgreSQL database as a single stack.
3. **Implementing a continuous integration and continuous deployment (CI/CD) pipeline** using GitHub Actions, which automatically runs tests, builds Docker images, and pushes them to DockerHub.
4. **Authoring Kubernetes manifests** that declare how the application should be deployed in a production-like cluster, including namespace isolation, ConfigMaps, Secrets, Deployments, Services, Ingress routing, and persistent PostgreSQL storage via a StatefulSet.
5. **Applying hardening best practices**, such as non-root container execution, health checks, and resource limits.

### 1.4 Branch Strategy

All DevOps-related changes were committed to a dedicated feature branch named `feature/kiii-deployment`. This approach isolates infrastructure modifications from the stable `main` branch, allows for safe experimentation, and follows a standard GitHub Flow workflow. Once the feature is fully verified, the branch can be merged into `main` through a pull request.

### 1.5 Public Repository Link

The complete source code, including application code, Dockerfiles, Docker Compose configuration, GitHub Actions workflow, and Kubernetes manifests, is publicly available at:

- **GitHub:** https://github.com/riste-gjorgjiev/Kino

---

## 2. Architecture and Components

### 2.1 High-Level Overview

Kino Movie Tracker follows a classic three-tier architecture, adapted for containerized deployment:

```
┌─────────────────────────────────────────────────────────────┐
│                        User / Browser                       │
└───────────────────────────┬─────────────────────────────────┘
                            │
                            ▼ HTTP (port 80/443)
┌─────────────────────────────────────────────────────────────┐
│                 Ingress Controller (NGINX)                  │
│            Routes / to frontend, /api/* to backend          │
└───────────────────────────┬─────────────────────────────────┘
                            │
            ┌───────────────┴───────────────┐
            ▼                               ▼
┌─────────────────────┐         ┌─────────────────────┐
│   Frontend Service  │         │   Backend Service   │
│   (Angular + Nginx) │         │   (Spring Boot)     │
└─────────┬───────────┘         └─────────┬───────────┘
          │                               │
          │    ┌─────────────────────┐    │
          └───▶│   TMDB REST API     │◀───┘
               │   (External Data)   │
               └─────────────────────┘
                            │
                            ▼
               ┌─────────────────────┐
               │   PostgreSQL 16     │
               │ (Infrastructure DB) │
               └─────────────────────┘
```

### 2.2 Backend — Spring Boot REST API

The backend is implemented as a Spring Boot 4.0.2 application using Java 21. It exposes a set of RESTful endpoints under `/api/` that abstract interactions with the TMDB API. The backend is responsible for:

- Fetching movie and TV show metadata from TMDB.
- Mapping TMDB-specific data structures into clean, application-specific Data Transfer Objects (DTOs) so that the frontend is decoupled from TMDB's schema.
- Implementing server-side caching with Caffeine to reduce external API calls and improve response times.
- Providing endpoints for trending, popular, upcoming, top-rated, on-the-air, search, and detail operations.
- Exposing Spring Boot Actuator endpoints, including `/actuator/health`, which are used for Docker and Kubernetes health probes.

The backend is packaged as a self-contained executable JAR file and exposes port `8080`.

### 2.3 Frontend — Angular Single-Page Application

The frontend is an Angular 21 application built with standalone components. It communicates with the backend over HTTP using Angular's `HttpClient` and RxJS observables. The user interface includes:

- A home page showcasing trending movies and TV shows.
- Dedicated media list pages for movies and TV shows, organized into categories such as Popular, Top Rated, Upcoming, and On TV.
- A search page with tabs for All, Movies, and TV results.
- A details page for individual titles, displaying backdrops, posters, genres, ratings, runtime, cast, and crew.

For production deployment, the Angular application is built with Ahead-of-Time (AOT) compilation and served as static files by Nginx, which also handles client-side routing through the `try_files` directive.

### 2.4 PostgreSQL Database

PostgreSQL 16 is included in the deployment stack as infrastructure, provisioned via Docker Compose and Kubernetes. During this phase of the project, the application does not actively read from or write to the database; all dynamic data is still sourced from the TMDB API. However, PostgreSQL is intentionally deployed so that:

- Future features such as user accounts, watchlists, ratings, and reviews can be persisted.
- The infrastructure already satisfies common production requirements.
- The deployment exercises real-world patterns for stateful services, including persistent volumes and Secrets management.

### 2.5 Inter-Service Communication

Inside Docker Compose, service names are resolved as DNS hostnames. The frontend Nginx configuration proxies requests to `http://backend:8080/api/`, while the backend constructs its TMDB requests to external TMDB servers. In Kubernetes, Services named `frontend`, `backend`, and `postgres` provide stable internal DNS names, and the Ingress resource routes external traffic to the appropriate Service based on URL path.

---

## 3. Dockerization

### 3.1 Rationale for Multi-Stage Dockerfiles

Both the backend and frontend use multi-stage Dockerfiles. A multi-stage build separates the build environment, which contains compilers, build tools, and development dependencies, from the final runtime environment. This approach provides several advantages:

- **Smaller image size:** Only compiled artifacts (JAR files or static HTML/JS) are copied to the final image.
- **Reduced attack surface:** Build tools and source code are not present in the runtime image.
- **Faster deployments:** Smaller images pull more quickly from DockerHub.
- **Reproducibility:** The build process is fully defined in code and runs identically on any Docker host.

### 3.2 Backend Dockerfile

The backend Dockerfile uses a Maven-based build stage and an Eclipse Temurin JRE runtime stage.

```dockerfile
# Stage 1: Build
FROM maven:3.9.9-eclipse-temurin-21 AS build
WORKDIR /app

# Cache Maven dependencies
COPY pom.xml .
COPY .mvn/ .mvn/
COPY mvnw .
RUN chmod +x mvnw
RUN ./mvnw -q dependency:go-offline

# Build the application
COPY src ./src
RUN ./mvnw -q -Dmaven.test.skip=true package

# Stage 2: Runtime
FROM eclipse-temurin:21-jre
WORKDIR /app

# Create a non-root user
RUN groupadd -r kino && useradd -r -g kino kino

COPY --from=build /app/target/*.jar app.jar
RUN chown -R kino:kino /app
USER kino

EXPOSE 8080
ENV JAVA_OPTS=""
HEALTHCHECK --interval=30s --timeout=5s --start-period=30s --retries=3 \
  CMD curl -f http://localhost:8080/actuator/health || exit 1

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
```

**Key design decisions in the backend Dockerfile:**

- `maven:3.9.9-eclipse-temurin-21` provides Java 21 and Maven 3.9.9, matching the project's requirements.
- `dependency:go-offline` caches Maven dependencies in a dedicated layer, accelerating rebuilds when only source code changes.
- Tests are skipped during the Docker build because the CI pipeline already runs them in a separate job.
- The runtime stage uses the lighter `eclipse-temurin:21-jre` image, which excludes the JDK.
- A dedicated `kino` user and group are created, and the final process runs as that user.
- A `HEALTHCHECK` periodically verifies that `/actuator/health` returns a successful response.

### 3.3 Frontend Dockerfile

The frontend Dockerfile compiles the Angular application in a Node.js stage and serves it with an unprivileged Nginx image.

```dockerfile
# Stage 1: Build
FROM node:22-alpine AS build
WORKDIR /app
COPY package*.json ./
RUN npm ci
COPY . .
RUN npm run build -- --configuration production

# Stage 2: Serve with Nginx
FROM nginxinc/nginx-unprivileged:1.27-alpine
COPY nginx.conf /etc/nginx/conf.d/default.conf
COPY --from=build /app/dist/kino-frontend/browser /usr/share/nginx/html
EXPOSE 8080

HEALTHCHECK --interval=30s --timeout=5s --start-period=10s --retries=3 \
  CMD curl -f http://localhost:8080/ || exit 1

USER nginx
```

**Key design decisions in the frontend Dockerfile:**

- `node:22-alpine` is used for the build stage. Node 22 is compatible with Angular 21 and provides a fast, Alpine-based environment.
- `npm ci` installs exact dependency versions from `package-lock.json`, ensuring reproducible builds.
- Production build mode enables tree-shaking, minification, and Ahead-of-Time compilation.
- `nginxinc/nginx-unprivileged` is an official, hardened Nginx image that runs on an unprivileged port (`8080`) without root access.
- The final container runs as the built-in `nginx` user, not `root`.
- A `HEALTHCHECK` confirms that Nginx is serving the SPA index page.

### 3.4 Security Improvements

Container hardening was applied to both images:

- **Non-root execution:** The backend runs as the `kino` user, and the frontend runs as the `nginx` user. This limits the damage that can result from container breakouts or application exploits.
- **Health checks:** Docker health checks ensure that containers are restarted automatically if they become unhealthy.
- **Minimal base images:** The frontend uses the Alpine-based unprivileged Nginx image, and the backend runtime uses a JRE-only image.
- **No secrets baked into images:** Sensitive values such as the TMDB API key are passed at runtime via environment variables, never embedded in the image layers.

### 3.5 Nginx Configuration

The frontend image includes a custom `nginx.conf` to serve the Angular SPA and proxy API calls to the backend.

```nginx
server{
  listen 8080;
  server_name _;

  root /usr/share/nginx/html;
  index index.html;

  location / {
    try_files $uri $uri/ /index.html;
  }

  location /api/ {
    proxy_pass http://backend:8080/api/;
    proxy_set_header Host $host;
    proxy_set_header X-Real-IP $remote_addr;
    proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
    proxy_set_header X-Forwarded-Proto $scheme;
  }
}
```

The `try_files` directive is essential for Angular SPAs because it ensures that deep links such as `/movie/123` are served `index.html`, allowing the Angular Router to handle the route client-side. The `/api/` location forwards API requests to the backend service, so the frontend can call relative paths such as `/api/movies/popular`.

---

## 4. Docker Compose Orchestration

### 4.1 Purpose of Docker Compose

Docker Compose is used to define and run the entire Kino stack on a single Docker host. With one command, developers and reviewers can build the backend and frontend images, start PostgreSQL, link the services, and expose the application on local ports. This eliminates environment-specific issues and provides a consistent integration target.

### 4.2 Full docker-compose.yml Content

```yaml
version: "3.9"

services:
  postgres:
    image: postgres:16-alpine
    container_name: kino-postgres
    restart: unless-stopped
    environment:
      POSTGRES_DB: ${POSTGRES_DB:-kino_db}
      POSTGRES_USER: ${POSTGRES_USER:-kino_user}
      POSTGRES_PASSWORD: ${POSTGRES_PASSWORD:-kino_password}
    ports:
      - "5432:5432"
    volumes:
      - postgres_data:/var/lib/postgresql/data
    healthcheck:
      test: ["CMD-SHELL", "pg_isready -U ${POSTGRES_USER:-kino_user} -d ${POSTGRES_DB:-kino_db}"]
      interval: 10s
      timeout: 5s
      retries: 5

  backend:
    build: ./kino-backend
    container_name: kino-backend
    restart: unless-stopped
    environment:
      TMDB_API_KEY: ${TMDB_API_KEY}
      SPRING_DATASOURCE_URL: jdbc:postgresql://postgres:5432/${POSTGRES_DB:-kino_db}
      SPRING_DATASOURCE_USERNAME: ${POSTGRES_USER:-kino_user}
      SPRING_DATASOURCE_PASSWORD: ${POSTGRES_PASSWORD:-kino_password}
    ports:
      - "8080:8080"
    depends_on:
      postgres:
        condition: service_healthy

  frontend:
    build: ./kino-frontend
    container_name: kino-frontend
    restart: unless-stopped
    ports:
      - "4200:8080"
    depends_on:
      - backend

volumes:
  postgres_data:
```

### 4.3 Service Descriptions

**PostgreSQL Service**

- Uses the official `postgres:16-alpine` image.
- Configures the default database, username, and password from environment variables, with safe defaults.
- Exposes port `5432` for external tooling and debugging.
- Mounts a named volume `postgres_data` to persist data across container restarts.
- Defines a health check using `pg_isready`, which reports healthy once the database accepts connections.

**Backend Service**

- Builds the backend image from `./kino-backend`.
- Receives the TMDB API key and PostgreSQL connection settings via environment variables.
- Exposes the API on port `8080`.
- Uses `depends_on` with `condition: service_healthy` to ensure the backend starts only after PostgreSQL is ready, preventing connection failures during startup.

**Frontend Service**

- Builds the frontend image from `./kino-frontend`.
- Maps host port `4200` to container port `8080`, consistent with Angular's default development port and making the local URL easy to remember (`http://localhost:4200`).
- Depends on the backend so that API proxying is available once the frontend starts.

### 4.4 Health Checks and Startup Order

The `depends_on` directive in Docker Compose controls startup order. By setting `condition: service_healthy` on the backend's dependency on PostgreSQL, the backend container is created only after PostgreSQL reports a successful health check. Although Docker Compose does not provide a built-in condition for the frontend-to-backend relationship beyond container creation, the backend's own startup time is short enough that the frontend can proxy requests reliably once both are running.

### 4.5 Environment Variables and .env

Sensitive and environment-specific values are externalized to a `.env` file at the repository root. The checked-in `.env` file contains:

```
TMDB_API_KEY=45f0092b2a18be9de613023acb8c7bc6
```

Docker Compose automatically loads variables from this file. Default values for PostgreSQL credentials are embedded directly in the Compose file using the `${VAR:-default}` syntax, so the application runs even if the user does not define additional variables. In a production setting, the `.env` file should be excluded from version control and supplied through the deployment environment or a secrets manager.

---

## 5. CI/CD with GitHub Actions

### 5.1 Workflow Overview

The GitHub Actions workflow defined in `.github/workflows/ci-cd.yml` automates the entire software delivery lifecycle: code checkout, testing, Docker image building, image publishing to DockerHub, and optional deployment to Kubernetes. The workflow is triggered on pushes to `main` or `feature/kiii-deployment`, and on pull requests targeting `main`.

### 5.2 Full CI/CD Workflow File

```yaml
name: CI/CD Pipeline

on:
  push:
    branches:
      - main
      - feature/kiii-deployment
  pull_request:
    branches:
      - main

env:
  REGISTRY: docker.io
  BACKEND_IMAGE: splinter998/kino-backend
  FRONTEND_IMAGE: splinter998/kino-frontend

jobs:
  test-backend:
    name: Test Backend
    runs-on: ubuntu-latest
    steps:
      - name: Checkout code
        uses: actions/checkout@v4

      - name: Set up JDK 21
        uses: actions/setup-java@v4
        with:
          java-version: '21'
          distribution: 'temurin'
          cache: maven

      - name: Run backend tests
        working-directory: ./kino-backend
        env:
          TMDB_API_KEY: ${{ secrets.TMDB_API_KEY }}
        run: ./mvnw -q test

  test-frontend:
    name: Test Frontend
    runs-on: ubuntu-latest
    steps:
      - name: Checkout code
        uses: actions/checkout@v4

      - name: Set up Node.js
        uses: actions/setup-node@v4
        with:
          node-version: '22'
          cache: 'npm'
          cache-dependency-path: ./kino-frontend/package-lock.json

      - name: Install dependencies
        working-directory: ./kino-frontend
        run: npm ci

      - name: Run frontend tests
        working-directory: ./kino-frontend
        run: npx vitest run

  build-and-push:
    name: Build & Push Docker Images
    runs-on: ubuntu-latest
    needs: [test-backend, test-frontend]
    if: github.event_name == 'push'
    steps:
      - name: Checkout code
        uses: actions/checkout@v4

      - name: Set up QEMU
        uses: docker/setup-qemu-action@v3

      - name: Set up Docker Buildx
        uses: docker/setup-buildx-action@v3

      - name: Login to DockerHub
        uses: docker/login-action@v3
        with:
          username: ${{ secrets.DOCKERHUB_USERNAME }}
          password: ${{ secrets.DOCKERHUB_TOKEN }}

      - name: Extract short SHA
        id: sha
        run: echo "sha=$(git rev-parse --short HEAD)" >> "$GITHUB_OUTPUT"

      - name: Build and push backend image
        uses: docker/build-push-action@v5
        with:
          context: ./kino-backend
          push: true
          tags: |
            ${{ env.BACKEND_IMAGE }}:latest
            ${{ env.BACKEND_IMAGE }}:${{ steps.sha.outputs.sha }}
          cache-from: type=gha
          cache-to: type=gha,mode=max

      - name: Build and push frontend image
        uses: docker/build-push-action@v5
        with:
          context: ./kino-frontend
          push: true
          tags: |
            ${{ env.FRONTEND_IMAGE }}:latest
            ${{ env.FRONTEND_IMAGE }}:${{ steps.sha.outputs.sha }}
          cache-from: type=gha
          cache-to: type=gha,mode=max

  deploy:
    name: Deploy to Kubernetes
    runs-on: ubuntu-latest
    needs: build-and-push
    if: github.ref == 'refs/heads/main'
    steps:
      - name: Checkout code
        uses: actions/checkout@v4

      - name: Configure kubectl
        uses: azure/setup-kubectl@v4
        with:
          version: 'v1.30.0'

      - name: Set up kubeconfig
        run: |
          mkdir -p ~/.kube
          echo "${{ secrets.KUBECONFIG }}" | base64 -d > ~/.kube/config

      - name: Deploy to Kubernetes
        run: |
          kubectl apply -f k8s/
          kubectl rollout status deployment/backend -n kino-kiii
          kubectl rollout status deployment/frontend -n kino-kiii
```

### 5.3 Job Breakdown

**test-backend**

This job checks out the repository, installs Eclipse Temurin JDK 21, caches Maven dependencies, and runs the backend test suite using the Maven wrapper. The TMDB API key is supplied from GitHub Secrets so that tests that interact with TMDB services can execute successfully.

**test-frontend**

This job checks out the repository, sets up Node.js 22 with npm caching, installs frontend dependencies with `npm ci`, and runs the Vitest test suite. Separating frontend and backend tests into parallel jobs reduces overall pipeline duration.

**build-and-push**

This job depends on both test jobs and runs only on push events (not pull requests). It sets up QEMU and Docker Buildx for advanced build capabilities, logs in to DockerHub, computes the short Git commit SHA, and builds and pushes both the backend and frontend images. Each image receives two tags:

- `latest` — always points to the most recent successful build.
- `<short-sha>` — immutable tag that allows rollback to a specific commit.

Build layers are cached using GitHub Actions cache (`type=gha`), significantly reducing subsequent build times.

**deploy**

This optional job runs only on pushes to the `main` branch after images have been pushed. It installs kubectl 1.30.0, decodes the `KUBECONFIG` secret into the runner's kubeconfig file, applies all manifests from the `k8s/` directory, and waits for the backend and frontend deployments to roll out successfully. Because the project currently has no Kubernetes cluster, this step is documented and configured but not yet exercised.

### 5.4 DockerHub Integration

DockerHub serves as the public container registry for Kino. Two repositories were created under the DockerHub user `splinter998`:

- `splinter998/kino-backend`
- `splinter998/kino-frontend`

The workflow logs in using a DockerHub access token rather than the account password, following the principle of least privilege. Images are pushed automatically after every successful build on the configured branches.

### 5.5 GitHub Secrets

The following secrets are configured in the GitHub repository:

| Secret Name         | Purpose                                                                 |
|---------------------|-------------------------------------------------------------------------|
| `DOCKERHUB_USERNAME`| DockerHub username (`splinter998`)                                       |
| `DOCKERHUB_TOKEN`   | DockerHub access token for image push                                    |
| `TMDB_API_KEY`      | API key for The Movie Database, used during backend testing              |
| `KUBECONFIG`        | Base64-encoded Kubernetes kubeconfig for the optional CD deployment step |

These secrets ensure that credentials never appear in source code or workflow logs.

### 5.6 Optional CD Deploy Step

The `deploy` job represents the continuous deployment portion of the pipeline. It is gated to the `main` branch so that feature branch builds do not automatically modify the production cluster. When a Kubernetes cluster becomes available, merging `feature/kiii-deployment` into `main` will trigger an automatic deployment. Until then, the manifests are applied manually or the job is allowed to remain inactive.

---

## 6. Kubernetes Deployment

### 6.1 k8s/ Directory Structure

All Kubernetes manifests are stored in the `k8s/` directory at the repository root:

```
k8s/
├── namespace.yaml
├── configmap.yaml
├── secret.yaml
├── backend-deployment.yaml
├── backend-service.yaml
├── frontend-deployment.yaml
├── frontend-service.yaml
├── ingress.yaml
├── postgres-configmap.yaml
├── postgres-secret.yaml
├── postgres-statefulset.yaml
└── postgres-service.yaml
```

### 6.2 Namespace Isolation

The `namespace.yaml` file creates a dedicated namespace `kino-kiii`:

```yaml
apiVersion: v1
kind: Namespace
metadata:
  name: kino-kiii
  labels:
    app: kino
```

Using a dedicated namespace isolates Kino resources from other workloads in the cluster, simplifies access control, and makes resource management and logging more straightforward.

### 6.3 ConfigMaps and Secrets

**ConfigMap — Application Configuration**

```yaml
apiVersion: v1
kind: ConfigMap
metadata:
  name: kino-config
  namespace: kino-kiii
data:
  SPRING_DATASOURCE_URL: "jdbc:postgresql://postgres:5432/kino_db"
  FRONTEND_API_URL: "http://backend:8080"
```

This ConfigMap stores non-sensitive configuration values that both the backend and frontend can reference.

**Secret — Application Secrets**

```yaml
apiVersion: v1
kind: Secret
metadata:
  name: kino-secret
  namespace: kino-kiii
type: Opaque
data:
  TMDB_API_KEY: eW91cl90bWRiX2FwaV9rZXk=
  SPRING_DATASOURCE_USERNAME: a2lub191c2Vy
  SPRING_DATASOURCE_PASSWORD: a2lub19wYXNzd29yZA==
```

The Secret holds the TMDB API key and database credentials. The values shown are base64-encoded placeholders and must be replaced with real values before production use.

**PostgreSQL ConfigMap**

```yaml
apiVersion: v1
kind: ConfigMap
metadata:
  name: postgres-config
  namespace: kino-kiii
data:
  POSTGRES_DB: "kino_db"
```

**PostgreSQL Secret**

```yaml
apiVersion: v1
kind: Secret
metadata:
  name: postgres-secret
  namespace: kino-kiii
type: Opaque
data:
  POSTGRES_USER: a2lub191c2Vy
  POSTGRES_PASSWORD: a2lub19wYXNzd29yZA==
```

### 6.4 Backend Deployment

```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: backend
  namespace: kino-kiii
  labels:
    app: backend
spec:
  replicas: 1
  selector:
    matchLabels:
      app: backend
  template:
    metadata:
      labels:
        app: backend
    spec:
      containers:
        - name: backend
          image: splinter998/kino-backend:latest
          imagePullPolicy: Always
          ports:
            - containerPort: 8080
          envFrom:
            - configMapRef:
                name: kino-config
          env:
            - name: TMDB_API_KEY
              valueFrom:
                secretKeyRef:
                  name: kino-secret
                  key: TMDB_API_KEY
            - name: SPRING_DATASOURCE_USERNAME
              valueFrom:
                secretKeyRef:
                  name: kino-secret
                  key: SPRING_DATASOURCE_USERNAME
            - name: SPRING_DATASOURCE_PASSWORD
              valueFrom:
                secretKeyRef:
                  name: kino-secret
                  key: SPRING_DATASOURCE_PASSWORD
          resources:
            requests:
              memory: "512Mi"
              cpu: "250m"
            limits:
              memory: "1Gi"
              cpu: "500m"
          livenessProbe:
            httpGet:
              path: /actuator/health
              port: 8080
            initialDelaySeconds: 60
            periodSeconds: 30
          readinessProbe:
            httpGet:
              path: /actuator/health
              port: 8080
            initialDelaySeconds: 30
            periodSeconds: 10
```

The backend Deployment mounts the ConfigMap, injects Secrets as environment variables, exposes port `8080`, and defines both liveness and readiness probes against the Spring Boot Actuator health endpoint. Resource requests and limits prevent a single pod from consuming excessive cluster resources.

### 6.5 Frontend Deployment

```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: frontend
  namespace: kino-kiii
  labels:
    app: frontend
spec:
  replicas: 1
  selector:
    matchLabels:
      app: frontend
  template:
    metadata:
      labels:
        app: frontend
    spec:
      containers:
        - name: frontend
          image: splinter998/kino-frontend:latest
          imagePullPolicy: Always
          ports:
            - containerPort: 8080
          resources:
            requests:
              memory: "128Mi"
              cpu: "100m"
            limits:
              memory: "256Mi"
              cpu: "200m"
          livenessProbe:
            httpGet:
              path: /
              port: 8080
            initialDelaySeconds: 10
            periodSeconds: 30
          readinessProbe:
            httpGet:
              path: /
              port: 8080
            initialDelaySeconds: 5
            periodSeconds: 10
```

The frontend Deployment is lightweight, requesting only 128 MiB of memory and 100 millicores of CPU, reflecting the static nature of the Nginx-served Angular application.

### 6.6 Services

**Backend Service**

```yaml
apiVersion: v1
kind: Service
metadata:
  name: backend
  namespace: kino-kiii
spec:
  selector:
    app: backend
  ports:
    - protocol: TCP
      port: 8080
      targetPort: 8080
  type: ClusterIP
```

**Frontend Service**

```yaml
apiVersion: v1
kind: Service
metadata:
  name: frontend
  namespace: kino-kiii
spec:
  selector:
    app: frontend
  ports:
    - protocol: TCP
      port: 8080
      targetPort: 8080
  type: ClusterIP
```

Both Services use `ClusterIP`, meaning they are accessible only inside the cluster. External access is provided through the Ingress resource.

### 6.7 Ingress

```yaml
apiVersion: networking.k8s.io/v1
kind: Ingress
metadata:
  name: kino-ingress
  namespace: kino-kiii
  annotations:
    nginx.ingress.kubernetes.io/rewrite-target: /
spec:
  ingressClassName: nginx
  rules:
    - host: kino.local
      http:
        paths:
          - path: /
            pathType: Prefix
            backend:
              service:
                name: frontend
                port:
                  number: 8080
          - path: /api
            pathType: Prefix
            backend:
              service:
                name: backend
                port:
                  number: 8080
```

The Ingress routes traffic for the host `kino.local`. Requests to the root path are sent to the frontend Service, while requests under `/api` are sent to the backend Service. This single-host design simplifies local development and can be extended later with TLS termination.

### 6.8 PostgreSQL StatefulSet

```yaml
apiVersion: apps/v1
kind: StatefulSet
metadata:
  name: postgres
  namespace: kino-kiii
spec:
  serviceName: postgres
  replicas: 1
  selector:
    matchLabels:
      app: postgres
  template:
    metadata:
      labels:
        app: postgres
    spec:
      containers:
        - name: postgres
          image: postgres:16-alpine
          ports:
            - containerPort: 5432
          envFrom:
            - configMapRef:
                name: postgres-config
          env:
            - name: POSTGRES_USER
              valueFrom:
                secretKeyRef:
                  name: postgres-secret
                  key: POSTGRES_USER
            - name: POSTGRES_PASSWORD
              valueFrom:
                secretKeyRef:
                  name: postgres-secret
                  key: POSTGRES_PASSWORD
          volumeMounts:
            - name: postgres-storage
              mountPath: /var/lib/postgresql/data
          resources:
            requests:
              memory: "256Mi"
              cpu: "100m"
            limits:
              memory: "512Mi"
              cpu: "200m"
  volumeClaimTemplates:
    - metadata:
        name: postgres-storage
      spec:
        accessModes: ["ReadWriteOnce"]
        resources:
          requests:
            storage: 5Gi
```

A StatefulSet is used instead of a Deployment for PostgreSQL because databases require stable network identities and persistent storage. The `volumeClaimTemplates` section dynamically provisions a 5 GiB persistent volume for each pod, ensuring that data survives pod restarts.

### 6.9 PostgreSQL Service

```yaml
apiVersion: v1
kind: Service
metadata:
  name: postgres
  namespace: kino-kiii
spec:
  selector:
    app: postgres
  ports:
    - protocol: TCP
      port: 5432
      targetPort: 5432
  type: ClusterIP
  clusterIP: None
```

The PostgreSQL Service is a headless Service (`clusterIP: None`), which is the recommended pattern for StatefulSets because it provides stable DNS entries for individual pods.

### 6.10 Probes, Resource Limits, and Storage

Every production workload defines resource requests and limits, ensuring fair scheduling and preventing resource exhaustion. Both backend and frontend deployments define liveness probes to detect and restart failed containers, and readiness probes to ensure traffic is routed only to healthy pods. PostgreSQL persistence is handled through a `volumeClaimTemplates`, which relies on the cluster's default StorageClass to provision a PersistentVolume.

---

## 7. Testing and Deployment

### 7.1 Docker Compose Verification

The following commands can be used to verify the local Docker Compose deployment.

Build and start all services in detached mode:

```bash
docker compose up --build -d
```

Expected output of `docker compose ps` after startup:

```
NAME            IMAGE                  COMMAND                  SERVICE     CREATED          STATUS                    PORTS
kino-backend    kino-backend           "sh -c 'java $JAVA_O…"   backend     2 minutes ago    Up 2 minutes (healthy)    0.0.0.0:8080->8080/tcp
kino-frontend   kino-frontend          "/docker-entrypoint.…"   frontend    2 minutes ago    Up 2 minutes (healthy)    0.0.0.0:4200->8080/tcp
kino-postgres   postgres:16-alpine     "docker-entrypoint.s…"   postgres    2 minutes ago    Up 2 minutes (healthy)    0.0.0.0:5432->5432/tcp
```

Verify the backend health endpoint:

```bash
curl http://localhost:8080/actuator/health
```

Expected response:

```json
{"status":"UP"}
```

Test the frontend through the browser at `http://localhost:4200`. The Kino home page should load and display media content fetched from TMDB through the backend.

### 7.2 Kubernetes Verification

When a Kubernetes cluster becomes available, the manifests can be applied with:

```bash
kubectl apply -f k8s/
```

Check namespace creation:

```bash
kubectl get namespace kino-kiii
```

Expected output:

```
NAME        STATUS   AGE
kino-kiii   Active   5m
```

Check pod status:

```bash
kubectl get pods -n kino-kiii
```

Expected output:

```
NAME                         READY   STATUS    RESTARTS   AGE
backend-7d9f4b8c5-x2v1k      1/1     Running   0          3m
frontend-6a2c9d4e8-y5w9q     1/1     Running   0          3m
postgres-0                   1/1     Running   0          3m
```

Check services:

```bash
kubectl get svc -n kino-kiii
```

Check ingress:

```bash
kubectl get ingress -n kino-kiii
```

### 7.3 Local Access

- **Docker Compose frontend:** http://localhost:4200
- **Docker Compose backend API:** http://localhost:8080
- **Kubernetes via Ingress:** http://kino.local (requires adding the Ingress controller IP to `/etc/hosts`)

### 7.4 Cluster Availability Note

At the time of writing this report, a live Kubernetes cluster was not yet available. Therefore, the Kubernetes manifests have been authored, validated for correct syntax, and committed to the repository, but they have not been applied to a running cluster. The Docker Compose stack has been verified locally and serves as the current reference deployment.

---

## 8. Security Considerations

### 8.1 Secrets Management

Sensitive values are never hard-coded into application code, Dockerfiles, or Kubernetes manifests in plain text. Instead:

- The TMDB API key and database credentials are stored in GitHub Secrets.
- Kubernetes Secrets are used for runtime injection into pods.
- The `.env` file in the repository root should be treated as sensitive and excluded from version control in production environments.

### 8.2 Non-Root Containers

Both the backend and frontend images are configured to run as non-root users. This reduces the risk of privilege escalation if a container is compromised. The backend uses a dedicated `kino` user, and the frontend uses the built-in `nginx` unprivileged image.

### 8.3 Resource Limits

Resource requests and limits are defined for every Kubernetes workload. This prevents any single component from monopolizing cluster CPU or memory and protects neighboring workloads from noisy-neighbor effects.

### 8.4 Base64 Is Not Encryption

It is important to note that Kubernetes Secrets encode values using Base64, which is not encryption. Base64 is merely a reversible encoding scheme. Anyone with read access to the Secret object can decode the values. For true protection, Kubernetes Secrets should be combined with:

- Role-Based Access Control (RBAC) restricting who can read Secrets.
- Encryption at rest for etcd, configured at the cluster level.
- External secrets managers such as HashiCorp Vault or AWS Secrets Manager for production clusters.

---

## 9. Conclusion

### 9.1 Summary of Achievements

This phase of the Kino Movie Tracker project successfully introduced a complete DevOps and deployment layer. The backend and frontend were containerized with hardened, multi-stage Dockerfiles. A Docker Compose configuration was created to run the full stack locally, including PostgreSQL. A GitHub Actions CI/CD pipeline was implemented to run tests, build images, push them to DockerHub, and optionally deploy to Kubernetes. Finally, a full set of Kubernetes manifests was authored for namespace-isolated deployment, complete with ConfigMaps, Secrets, Deployments, Services, Ingress, and a PostgreSQL StatefulSet.

### 9.2 Future Improvements

Several enhancements can be pursued in future iterations:

- **Real Kubernetes cluster:** Provision a cluster using minikube, kind, k3s, or a managed cloud service and apply the manifests.
- **Database integration:** Extend the backend to persist user data, ratings, and watchlists in PostgreSQL using Spring Data JPA.
- **TLS termination:** Configure the Ingress with a TLS certificate (for example, via cert-manager and Let's Encrypt) to serve traffic over HTTPS.
- **Monitoring and logging:** Integrate Prometheus and Grafana for metrics, and Fluentd or Loki for centralized logging.
- **Horizontal Pod Autoscaling:** Add HPA resources to scale frontend and backend pods based on CPU utilization.
- **Secret management:** Migrate from plain Kubernetes Secrets to an external secrets operator for stronger security.

---

## 10. Links and References

| Resource | URL |
|----------|-----|
| GitHub Repository | https://github.com/riste-gjorgjiev/Kino |
| Backend DockerHub Image | https://hub.docker.com/r/splinter998/kino-backend |
| Frontend DockerHub Image | https://hub.docker.com/r/splinter998/kino-frontend |
| Spring Boot Documentation | https://spring.io/projects/spring-boot |
| Angular Documentation | https://angular.dev |
| Docker Documentation | https://docs.docker.com |
| Kubernetes Documentation | https://kubernetes.io/docs |
| NGINX Ingress Controller | https://kubernetes.github.io/ingress-nginx/ |
| TMDB API | https://developer.themoviedb.org/reference |

---

## 11. Screenshots Checklist

The following screenshots should be captured and included with the report submission. Each screenshot documents a specific artifact or verification step from the project.

1. **GitHub repository branch selector** — Show the repository open to the `feature/kiii-deployment` branch.
2. **GitHub repository file tree** — Show the root directory with `docker-compose.yml`, `.github/workflows/ci-cd.yml`, `k8s/`, `kino-backend/Dockerfile`, and `kino-frontend/Dockerfile` visible.
3. **GitHub Secrets page** — Show the secrets `DOCKERHUB_USERNAME`, `DOCKERHUB_TOKEN`, `TMDB_API_KEY`, and optionally `KUBECONFIG` with values masked.
4. **DockerHub repositories overview** — Show both `splinter998/kino-backend` and `splinter998/kino-frontend` repositories.
5. **DockerHub backend image tags** — Show the `latest` and commit SHA tags for `splinter998/kino-backend`.
6. **DockerHub frontend image tags** — Show the `latest` and commit SHA tags for `splinter998/kino-frontend`.
7. **Backend Dockerfile** — Show the non-root user creation, `USER kino`, and `HEALTHCHECK` lines.
8. **Frontend Dockerfile** — Show the multi-stage build, `nginxinc/nginx-unprivileged` base image, and `HEALTHCHECK`.
9. **docker-compose.yml** — Show all three services, health check, and `depends_on` configuration.
10. **CI/CD workflow file** — Show the `test-backend`, `test-frontend`, `build-and-push`, and `deploy` jobs.
11. **GitHub Actions workflow run** — Show a successful CI/CD pipeline run with all jobs green.
12. **Docker Compose running containers** — Show `docker compose ps` output with all three containers healthy.
13. **Backend health check response** — Show `curl http://localhost:8080/actuator/health` returning `{"status":"UP"}`.
14. **Frontend in browser via Docker Compose** — Show `http://localhost:4200` displaying the Kino home page.
15. **Kubernetes namespace** — Show `kubectl get namespace kino-kiii` output.
16. **Kubernetes pods** — Show `kubectl get pods -n kino-kiii` with backend, frontend, and postgres pods Running.
17. **Kubernetes services** — Show `kubectl get svc -n kino-kiii` output.
18. **Kubernetes ingress** — Show `kubectl get ingress -n kino-kiii` output with `kino.local` host.
19. **Application via ingress domain** — Show a browser or curl request to `http://kino.local/` returning the frontend.
20. **API via ingress domain** — Show a browser or curl request to `http://kino.local/api/...` returning backend data.

---

*End of report.*
