export const environment = {
  production: true,
  // Relative URL so the app works behind any reverse proxy:
  // - Nginx in the Docker image proxies /api/ to the backend container
  // - Vercel proxies /api/ to the hosted backend (see vercel.json)
  apiUrl: '/api'
};
