# KIII Deployment Implementation Guide

**Branch:** `feature/kiii-deployment`  
**DockerHub username:** `splinter998`  
**GitHub username:** `riste-gjorgjiev`  
**Repository:** `Kino`  
**Kubernetes namespace:** `kino-kiii`  
**Ingress domain:** `kino.local`  
**Include optional CD deploy step:** yes  
**Harden Dockerfiles:** yes (non-root user, health checks)  
**Database approach:** Option 1 — infrastructure-only PostgreSQL, no application code changes  
**Cluster status:** no Kubernetes cluster yet; cluster commands are for when one is available

> ⏸️ **Status:** this guide describes the target DevOps state. The work lives on the `feature/kiii-deployment` branch and is **not merged into `main`**. See [../plans/backlog.md](../plans/backlog.md).

---

## 0. Grading Criteria Mapping

| % | Criterion | How this plan satisfies it |
|---|-----------|----------------------------|
| 10% | Public Git repository | Public GitHub repo, push `feature/kiii-deployment`, optionally merge to `main` |
| 10% | Dockerize the application | Harden existing Dockerfiles; build & run backend + frontend images |
| 10% | Docker Compose orchestration (app + database) | Add a PostgreSQL service to `docker-compose.yml` even though the app does not use it yet |
| 20% | CI/CD (GitHub Actions + DockerHub + optional CD) | `.github/workflows/ci-cd.yml` runs tests, builds images, pushes SHA + latest to DockerHub, optional deploy step |
| 10% | Kubernetes Deployment + ConfigMaps/Secrets | `k8s/backend-deployment.yaml`, `k8s/frontend-deployment.yaml`, `k8s/configmap.yaml`, `k8s/secret.yaml` |
| 10% | Kubernetes Service | `k8s/backend-service.yaml`, `k8s/frontend-service.yaml`, `k8s/postgres-service.yaml` |
| 10% | Kubernetes Ingress | `k8s/ingress.yaml` |
| 10% | Kubernetes StatefulSet for database + ConfigMaps/Secrets | `k8s/postgres-statefulset.yaml`, `k8s/postgres-configmap.yaml`, `k8s/postgres-secret.yaml` |
| 10% | Manifests in a separate namespace + proof it works | All resources live in namespace `kino-kiii`; verification commands provided |

### High-level workflow

1. Create and switch to branch `feature/kiii-deployment`.
2. Add/verify the public GitHub remote and push the branch.
3. Review and harden the Dockerfiles.
4. Extend `docker-compose.yml` to include PostgreSQL.
5. Add the GitHub Actions workflow at `.github/workflows/ci-cd.yml`.
6. Add Kubernetes manifests under `k8s/`.
7. Commit, push, and verify locally with Docker Compose and/or Kubernetes.
8. Submit the public repo link.

---

## 1. Pre-implementation Checklist

Complete these tasks before changing any code.

| # | Task | How to verify |
|---|------|---------------|
| 1 | Create and check out branch `feature/kiii-deployment` | `git branch --show-current` returns `feature/kiii-deployment` |
| 2 | DockerHub account exists and username is `splinter998` | Log in at https://hub.docker.com/ |
| 3 | Create DockerHub repositories `splinter998/kino-backend` and `splinter998/kino-frontend` | Visit https://hub.docker.com/repositories/splinter998 |
| 4 | Create GitHub Secret `DOCKERHUB_USERNAME` = `splinter998` | Repo → Settings → Secrets and variables → Actions |
| 5 | Create GitHub Secret `DOCKERHUB_TOKEN` (DockerHub access token, not password) | Repo → Settings → Secrets and variables → Actions |
| 6 | (Optional CD) Create GitHub Secret `KUBECONFIG` with base64-encoded kubeconfig | `cat ~/.kube/config | base64 -w0` |
| 7 | (Optional CD) Create GitHub Secret `KUBE_HOST` if using a remote runner target | e.g. `https://k8s-api.kino.local:6443` |
| 8 | Install local tools: Docker, Docker Compose, kubectl, git | `docker --version`, `docker compose version`, `kubectl version`, `git --version` |
| 9 | Decide how to obtain a Kubernetes cluster later (Docker Desktop, minikube, kind, k3s, cloud managed service) | Not required now, but know the plan |

---

## 2. Exact List of Files to Modify and Create

### Files to modify (update if they exist; create if missing)

1. `kino-backend/Dockerfile`
2. `kino-frontend/Dockerfile`
3. `kino-frontend/nginx.conf`
4. `docker-compose.yml`
5. `kino-frontend/src/environments/environment.prod.ts`

### Files to create

1. `.github/workflows/ci-cd.yml`
2. `k8s/namespace.yaml`
3. `k8s/configmap.yaml`
4. `k8s/secret.yaml`
5. `k8s/backend-deployment.yaml`
6. `k8s/frontend-deployment.yaml`
7. `k8s/backend-service.yaml`
8. `k8s/frontend-service.yaml`
9. `k8s/ingress.yaml`
10. `k8s/postgres-configmap.yaml`
11. `k8s/postgres-secret.yaml`
12. `k8s/postgres-statefulset.yaml`
13. `k8s/postgres-service.yaml`

---

## 3. Step-by-step Implementation

### 3.1 Create the feature branch

Run these commands from the repository root `/home/riste/IdeaProjects/Kino`:

```bash
git checkout -b feature/kiii-deployment
```

### 3.2 Update `kino-backend/Dockerfile`

Create or replace the file `kino-backend/Dockerfile`.

```dockerfile
# --------------------------
# Build stage
# --------------------------
FROM eclipse-temurin:17-jdk-alpine AS builder

WORKDIR /app
COPY .mvn/ .mvn
COPY mvnw pom.xml ./
RUN ./mvnw dependency:go-offline -B

COPY src ./src
RUN ./mvnw clean package -DskipTests -B

# --------------------------
# Runtime stage
# --------------------------
FROM eclipse-temurin:17-jre-alpine

# Create non-root user/group
RUN addgroup -S kino && adduser -S kino -G kino

WORKDIR /app

# Copy the built JAR from the builder stage
COPY --from=builder /app/target/*.jar app.jar

# Change ownership to the non-root user
RUN chown -R kino:kino /app

USER kino

EXPOSE 8080

HEALTHCHECK --interval=30s --timeout=5s --start-period=60s --retries=3 \
  CMD wget --no-verbose --tries=1 --spider http://localhost:8080/actuator/health || exit 1

ENTRYPOINT ["java", "-jar", "/app/app.jar"]
```

> **Note:** If the backend does not expose `/actuator/health`, add Spring Boot Actuator or replace the `HEALTHCHECK` URL with any stable endpoint such as `/api/health`.

### 3.3 Update `kino-frontend/Dockerfile`

Create or replace the file `kino-frontend/Dockerfile`.

```dockerfile
# --------------------------
# Build stage
# --------------------------
FROM node:18-alpine AS builder

WORKDIR /app

COPY package*.json ./
RUN npm ci

COPY . .
RUN npm run build -- --configuration production

# --------------------------
# Runtime stage
# --------------------------
FROM nginxinc/nginx-unprivileged:1.25-alpine

USER root

# Remove default nginx static assets and config
RUN rm -rf /usr/share/nginx/html/* /etc/nginx/conf.d/default.conf

# Copy custom nginx configuration
COPY nginx.conf /etc/nginx/conf.d/default.conf

# Copy built Angular application
COPY --from=builder /app/dist/kino-frontend/browser /usr/share/nginx/html

# Fix ownership for the unprivileged nginx user
RUN chown -R nginx:nginx /usr/share/nginx/html /etc/nginx/conf.d

USER nginx

EXPOSE 8080

HEALTHCHECK --interval=30s --timeout=5s --start-period=10s --retries=3 \
  CMD wget --no-verbose --tries=1 --spider http://localhost:8080/ || exit 1

CMD ["nginx", "-g", "daemon off;"]
```

### 3.4 Update `kino-frontend/nginx.conf`

Create or replace the file `kino-frontend/nginx.conf`. This configuration listens on port `8080` (required for the unprivileged nginx image) and proxies `/api` requests to the backend.

```nginx
server {
    listen 8080;
    server_name localhost;

    root /usr/share/nginx/html;
    index index.html;

    location / {
        try_files $uri $uri/ /index.html;
    }

    location /api/ {
        proxy_pass http://backend:8080/api/;
        proxy_http_version 1.1;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
    }

    error_page 500 502 503 504 /50x.html;
    location = /50x.html {
        root /usr/share/nginx/html;
    }
}
```

### 3.5 Update `docker-compose.yml`

Create or replace the file `docker-compose.yml` at the repository root.

```yaml
version: "3.9"

services:
  postgres:
    image: postgres:16-alpine
    container_name: kino-postgres
    restart: unless-stopped
    environment:
      POSTGRES_DB: kino
      POSTGRES_USER: kino
      POSTGRES_PASSWORD: kino123
    ports:
      - "5432:5432"
    volumes:
      - postgres-data:/var/lib/postgresql/data
    healthcheck:
      test: ["CMD-SHELL", "pg_isready -U kino -d kino"]
      interval: 10s
      timeout: 5s
      retries: 5

  backend:
    build:
      context: ./kino-backend
      dockerfile: Dockerfile
    container_name: kino-backend
    restart: unless-stopped
    environment:
      SPRING_DATASOURCE_URL: jdbc:postgresql://postgres:5432/kino
      SPRING_DATASOURCE_USERNAME: kino
      SPRING_DATASOURCE_PASSWORD: kino123
      SERVER_PORT: 8080
    ports:
      - "8080:8080"
    depends_on:
      postgres:
        condition: service_healthy
    healthcheck:
      test: ["CMD", "wget", "--no-verbose", "--tries=1", "--spider", "http://localhost:8080/actuator/health"]
      interval: 30s
      timeout: 5s
      retries: 3
      start_period: 60s

  frontend:
    build:
      context: ./kino-frontend
      dockerfile: Dockerfile
    container_name: kino-frontend
    restart: unless-stopped
    ports:
      - "80:8080"
    depends_on:
      - backend
    healthcheck:
      test: ["CMD", "wget", "--no-verbose", "--tries=1", "--spider", "http://localhost:8080/"]
      interval: 30s
      timeout: 5s
      retries: 3
      start_period: 10s

volumes:
  postgres-data:
```

> **Option 1 note:** Only infrastructure is changed. No application code is modified to use PostgreSQL. If the backend already reads `SPRING_DATASOURCE_URL`, it will connect automatically.

### 3.6 Update `kino-frontend/src/environments/environment.prod.ts`

Create or replace the file `kino-frontend/src/environments/environment.prod.ts`.

```typescript
export const environment = {
  production: true,
  apiUrl: '/api'
};
```

> Verify that the Angular services use `environment.apiUrl` when building API requests. No other frontend code changes are required for Option 1.

### 3.7 Create `.github/workflows/ci-cd.yml`

Create the directories and file.

```bash
mkdir -p .github/workflows
```

File `.github/workflows/ci-cd.yml`:

```yaml
name: CI/CD Pipeline

on:
  push:
    branches:
      - feature/kiii-deployment
      - main
  pull_request:
    branches:
      - main

env:
  BACKEND_IMAGE: splinter998/kino-backend
  FRONTEND_IMAGE: splinter998/kino-frontend

jobs:
  test-backend:
    name: Test Backend
    runs-on: ubuntu-latest
    defaults:
      run:
        working-directory: ./kino-backend
    steps:
      - name: Checkout code
        uses: actions/checkout@v4

      - name: Set up JDK 17
        uses: actions/setup-java@v4
        with:
          java-version: '17'
          distribution: 'temurin'
          cache: maven

      - name: Run Maven tests
        run: ./mvnw test -B

  test-frontend:
    name: Test Frontend
    runs-on: ubuntu-latest
    defaults:
      run:
        working-directory: ./kino-frontend
    steps:
      - name: Checkout code
        uses: actions/checkout@v4

      - name: Set up Node.js
        uses: actions/setup-node@v4
        with:
          node-version: '18'
          cache: 'npm'
          cache-dependency-path: ./kino-frontend/package-lock.json

      - name: Install dependencies
        run: npm ci

      - name: Run lint
        run: npm run lint --if-present

      - name: Run tests
        run: npm test -- --watch=false --browsers=ChromeHeadless --no-progress

  build-and-push:
    name: Build and Push Docker Images
    runs-on: ubuntu-latest
    needs:
      - test-backend
      - test-frontend
    if: github.event_name == 'push'
    steps:
      - name: Checkout code
        uses: actions/checkout@v4

      - name: Set up Docker Buildx
        uses: docker/setup-buildx-action@v3

      - name: Log in to DockerHub
        uses: docker/login-action@v3
        with:
          username: ${{ secrets.DOCKERHUB_USERNAME }}
          password: ${{ secrets.DOCKERHUB_TOKEN }}

      - name: Build and push backend image
        uses: docker/build-push-action@v5
        with:
          context: ./kino-backend
          file: ./kino-backend/Dockerfile
          push: true
          tags: |
            ${{ env.BACKEND_IMAGE }}:latest
            ${{ env.BACKEND_IMAGE }}:${{ github.sha }}
          cache-from: type=gha
          cache-to: type=gha,mode=max

      - name: Build and push frontend image
        uses: docker/build-push-action@v5
        with:
          context: ./kino-frontend
          file: ./kino-frontend/Dockerfile
          push: true
          tags: |
            ${{ env.FRONTEND_IMAGE }}:latest
            ${{ env.FRONTEND_IMAGE }}:${{ github.sha }}
          cache-from: type=gha
          cache-to: type=gha,mode=max

  deploy:
    name: Deploy to Kubernetes
    runs-on: ubuntu-latest
    needs: build-and-push
    if: github.ref == 'refs/heads/main' && github.event_name == 'push'
    steps:
      - name: Checkout code
        uses: actions/checkout@v4

      - name: Install kubectl
        uses: azure/setup-kubectl@v3
        with:
          version: 'v1.29.0'

      - name: Configure kubeconfig
        run: |
          mkdir -p $HOME/.kube
          echo "${{ secrets.KUBECONFIG }}" | base64 -d > $HOME/.kube/config
          chmod 600 $HOME/.kube/config

      - name: Deploy manifests to Kubernetes
        run: |
          kubectl apply -f k8s/namespace.yaml
          kubectl apply -f k8s/postgres-configmap.yaml
          kubectl apply -f k8s/postgres-secret.yaml
          kubectl apply -f k8s/postgres-service.yaml
          kubectl apply -f k8s/postgres-statefulset.yaml
          kubectl apply -f k8s/configmap.yaml
          kubectl apply -f k8s/secret.yaml
          kubectl apply -f k8s/backend-service.yaml
          kubectl apply -f k8s/frontend-service.yaml
          kubectl apply -f k8s/backend-deployment.yaml
          kubectl apply -f k8s/frontend-deployment.yaml
          kubectl apply -f k8s/ingress.yaml

      - name: Wait for rollout
        run: |
          kubectl rollout status deployment/backend -n kino-kiii --timeout=300s
          kubectl rollout status deployment/frontend -n kino-kiii --timeout=300s
```

> The `deploy` job is optional and only runs on pushes to `main`. It requires the `KUBECONFIG` secret.

### 3.8 Create Kubernetes manifests in `k8s/`

Create the directory:

```bash
mkdir -p k8s
```

#### `k8s/namespace.yaml`

```yaml
apiVersion: v1
kind: Namespace
metadata:
  name: kino-kiii
```

#### `k8s/configmap.yaml`

```yaml
apiVersion: v1
kind: ConfigMap
metadata:
  name: kino-config
  namespace: kino-kiii
data:
  SPRING_DATASOURCE_URL: "jdbc:postgresql://postgres:5432/kino"
  SERVER_PORT: "8080"
```

#### `k8s/secret.yaml`

```yaml
apiVersion: v1
kind: Secret
metadata:
  name: kino-secret
  namespace: kino-kiii
type: Opaque
data:
  SPRING_DATASOURCE_USERNAME: a2lubw==   # base64 for "kino"
  SPRING_DATASOURCE_PASSWORD: a2lubzEyMw== # base64 for "kino123"
```

> Generate base64 values with: `echo -n 'kino' | base64`

#### `k8s/backend-deployment.yaml`

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
            - secretRef:
                name: kino-secret
          resources:
            requests:
              memory: "256Mi"
              cpu: "250m"
            limits:
              memory: "512Mi"
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

#### `k8s/frontend-deployment.yaml`

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
              memory: "64Mi"
              cpu: "100m"
            limits:
              memory: "128Mi"
              cpu: "250m"
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

#### `k8s/backend-service.yaml`

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

#### `k8s/frontend-service.yaml`

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

#### `k8s/ingress.yaml`

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
          - path: /api
            pathType: Prefix
            backend:
              service:
                name: backend
                port:
                  number: 8080
          - path: /
            pathType: Prefix
            backend:
              service:
                name: frontend
                port:
                  number: 8080
```

#### `k8s/postgres-configmap.yaml`

```yaml
apiVersion: v1
kind: ConfigMap
metadata:
  name: postgres-config
  namespace: kino-kiii
data:
  POSTGRES_DB: "kino"
```

#### `k8s/postgres-secret.yaml`

```yaml
apiVersion: v1
kind: Secret
metadata:
  name: postgres-secret
  namespace: kino-kiii
type: Opaque
data:
  POSTGRES_USER: a2lubw==     # base64 for "kino"
  POSTGRES_PASSWORD: a2lubzEyMw== # base64 for "kino123"
```

#### `k8s/postgres-statefulset.yaml`

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
            - secretRef:
                name: postgres-secret
          volumeMounts:
            - name: postgres-data
              mountPath: /var/lib/postgresql/data
          resources:
            requests:
              memory: "128Mi"
              cpu: "100m"
            limits:
              memory: "256Mi"
              cpu: "250m"
  volumeClaimTemplates:
    - metadata:
        name: postgres-data
      spec:
        accessModes: ["ReadWriteOnce"]
        resources:
          requests:
            storage: 1Gi
```

#### `k8s/postgres-service.yaml`

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
```

---

## 4. Git Commands to Commit and Push

After all files are created or updated, run the following commands from the repository root.

```bash
# 1. Check which files are changed/new
git status

# 2. Stage the new and modified files
git add kino-backend/Dockerfile \
        kino-frontend/Dockerfile \
        kino-frontend/nginx.conf \
        docker-compose.yml \
        kino-frontend/src/environments/environment.prod.ts \
        .github/workflows/ci-cd.yml \
        k8s/

# 3. Commit with a clear message
git commit -m "feat(kiii): add Docker, CI/CD and Kubernetes deployment setup

- Harden backend and frontend Dockerfiles with non-root users and health checks
- Add PostgreSQL service to docker-compose.yml
- Configure frontend nginx to listen on 8080 and proxy /api to backend
- Add GitHub Actions CI/CD pipeline with DockerHub push and optional K8s deploy
- Add Kubernetes manifests for namespace, config, secrets, deployments, services,
  ingress and PostgreSQL StatefulSet under namespace kino-kiii"

# 4. Push the branch to GitHub
git push -u origin feature/kiii-deployment
```

Then open a pull request on GitHub from `feature/kiii-deployment` to `main`.

---

## 5. Local Verification Commands for Docker Compose

Run these commands from the repository root `/home/riste/IdeaProjects/Kino`.

### Build and start everything

```bash
docker compose up --build -d
```

### View running containers

```bash
docker compose ps
```

### Follow logs

```bash
docker compose logs -f
```

### Test backend health

```bash
curl http://localhost:8080/actuator/health
```

If the actuator endpoint is not available, test a known working endpoint instead:

```bash
curl http://localhost:8080/api/health
```

### Test frontend

Open http://localhost in a browser or run:

```bash
curl -I http://localhost
```

### Test API proxy through frontend

```bash
curl http://localhost/api/health
```

### Stop and clean up

```bash
docker compose down
```

To also remove the persistent PostgreSQL volume:

```bash
docker compose down -v
```

---

## 6. Kubernetes Verification Commands (for When Cluster Is Available)

These commands assume a working cluster with `kubectl` configured and an Ingress controller installed (for example NGINX Ingress).

### Apply all manifests

```bash
kubectl apply -f k8s/
```

### Check namespace

```bash
kubectl get namespace kino-kiii
```

### Check pods

```bash
kubectl get pods -n kino-kiii
```

### Check services

```bash
kubectl get svc -n kino-kiii
```

### Check deployments

```bash
kubectl get deployments -n kino-kiii
```

### Check StatefulSet

```bash
kubectl get statefulset -n kino-kiii
```

### Check Ingress

```bash
kubectl get ingress -n kino-kiii
```

### Follow logs

```bash
kubectl logs -n kino-kiii -l app=backend --tail=100 -f
kubectl logs -n kino-kiii -l app=frontend --tail=100 -f
kubectl logs -n kino-kiii -l app=postgres --tail=100 -f
```

### Test from inside the cluster

```bash
kubectl run -n kino-kiii debug --rm -it --image=curlimages/curl --restart=Never -- \
  curl http://backend:8080/actuator/health
```

### Test via Ingress

Add the following line to `/etc/hosts` on your local machine (replace `<cluster-ip>` with the Ingress controller IP):

```
<cluster-ip> kino.local
```

Then test:

```bash
curl http://kino.local/
curl http://kino.local/api/health
```

### Scale deployments

```bash
kubectl scale deployment backend --replicas=2 -n kino-kiii
kubectl scale deployment frontend --replicas=2 -n kino-kiii
```

### Delete everything

```bash
kubectl delete -f k8s/
```

---

## 7. Screenshot Checklist for School Submission

Capture the following screenshots and organize them in a `screenshots/` folder or document.

| # | Screenshot | Expected content |
|---|------------|------------------|
| 1 | GitHub repository with branch | Show `feature/kiii-deployment` branch in the branch selector |
| 2 | GitHub Secrets page | Show `DOCKERHUB_USERNAME`, `DOCKERHUB_TOKEN`, and optional `KUBECONFIG` secrets (values hidden) |
| 3 | DockerHub repositories | Show `splinter998/kino-backend` and `splinter998/kino-frontend` repositories |
| 4 | Project file tree | Show created/updated files: Dockerfiles, nginx.conf, docker-compose.yml, CI/CD workflow, `k8s/` manifests |
| 5 | Dockerfile backend | Show non-root user and `HEALTHCHECK` |
| 6 | Dockerfile frontend | Show unprivileged nginx and `HEALTHCHECK` |
| 7 | CI/CD workflow file | Show test, build, push, and optional deploy job |
| 8 | GitHub Actions run | Show a successful workflow run |
| 9 | DockerHub pushed images | Show `latest` and commit SHA tags for both images |
| 10 | Docker Compose running | `docker compose ps` showing all three services healthy |
| 11 | Backend health check | Browser or terminal showing successful backend response |
| 12 | Frontend via localhost | Browser showing the Kino frontend |
| 13 | Kubernetes namespace | `kubectl get namespace kino-kiii` output |
| 14 | Kubernetes pods | `kubectl get pods -n kino-kiii` showing all pods Running |
| 15 | Kubernetes services | `kubectl get svc -n kino-kiii` output |
| 16 | Kubernetes ingress | `kubectl get ingress -n kino-kiii` output |
| 17 | Application via ingress | Browser showing `http://kino.local/` |
| 18 | API via ingress | Browser/terminal showing `http://kino.local/api/...` |

---

## 8. Troubleshooting Section

### Docker Compose issues

| Symptom | Cause | Fix |
|---------|-------|-----|
| `backend` container exits immediately | Wrong JAR name or missing `target/*.jar` | Check the `COPY --from=builder` path matches the generated JAR name |
| `frontend` shows `403 Forbidden` | Static files not copied to correct nginx root | Verify `dist/kino-frontend/browser` path matches Angular output |
| `frontend` cannot reach `backend` | nginx proxy_pass uses hostname not resolvable in compose | Ensure service name in `proxy_pass` matches the compose service name `backend` |
| PostgreSQL container keeps restarting | Wrong `POSTGRES_PASSWORD` or volume corruption | Run `docker compose down -v` and restart |
| Port 5432 already in use | Local PostgreSQL running | Stop local Postgres or change the host port mapping |

### Dockerfile issues

| Symptom | Cause | Fix |
|---------|-------|-----|
| `permission denied` during build | Running commands as non-root too early | Only switch to `USER` after installing dependencies and copying files |
| `HEALTHCHECK` fails | Endpoint does not exist | Add Spring Boot Actuator or change to an existing endpoint |
| nginx unprivileged image fails to bind port 80 | Default nginx image expects root | Use `nginxinc/nginx-unprivileged` and listen on `8080` |

### CI/CD issues

| Symptom | Cause | Fix |
|---------|-------|-----|
| `denied: requested access to the resource is denied` | Wrong DockerHub token | Create a new access token at DockerHub and update `DOCKERHUB_TOKEN` |
| `secrets.DOCKERHUB_USERNAME` not found | Secret name typo | Verify secret names in GitHub repository settings |
| Deploy job fails with `Unable to connect to the server` | Invalid kubeconfig | Re-encode `~/.kube/config` and update `KUBECONFIG` secret |
| Workflow does not trigger | YAML syntax error | Validate workflow file with `actionlint` or GitHub web editor |

### Kubernetes issues

| Symptom | Cause | Fix |
|---------|-------|-----|
| Pods stuck in `Pending` | No Ingress controller or no storage class | Install NGINX Ingress Controller and verify default StorageClass |
| `ImagePullBackOff` | Image does not exist or is private | Push images to DockerHub or create an `imagePullSecret` |
| `CrashLoopBackOff` | Application fails to start | Check logs with `kubectl logs -n kino-kiii <pod-name>` |
| Ingress returns `404` | Host not mapped or path mismatch | Add `kino.local` to `/etc/hosts` and verify ingress rules |
| Backend cannot connect to Postgres | Wrong service name or credentials | Verify `SPRING_DATASOURCE_URL` points to `jdbc:postgresql://postgres:5432/kino` |
| `kino.local` does not resolve | Missing `/etc/hosts` entry or wrong Ingress IP | Find Ingress controller external IP with `kubectl get svc -n ingress-nginx` |

### Useful debug commands

```bash
# Describe a failing pod
kubectl describe pod -n kino-kiii <pod-name>

# Get pod logs
kubectl logs -n kino-kiii <pod-name> --previous

# Check events
kubectl get events -n kino-kiii --sort-by=.lastTimestamp

# Validate manifests without applying
kubectl apply -f k8s/ --dry-run=client

# Shell into a running container
kubectl exec -n kino-kiii -it <pod-name> -- /bin/sh
```

---

## End of Guide

Follow the steps in order. If a file already exists, update it with the contents shown. If it does not exist, create it. Do not change application business logic; this deployment uses **Option 1** (infrastructure-only PostgreSQL).
