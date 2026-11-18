import { expect, test, Page } from '@playwright/test';

const movieCard = (id: number, title: string) => ({
  id,
  mediaType: 'MOVIE',
  title,
  posterUrl: null,
  rating: 7.5,
  date: '2024-01-01'
});

const movieDetails = (id: number, title: string) => ({
  id,
  mediaType: 'MOVIE',
  title,
  tagline: 'A test tagline',
  overview: 'A test overview',
  posterUrl: null,
  backdropUrl: null,
  rating: 7.5,
  date: '2024-01-01',
  genres: ['Action'],
  runtimeMinutes: 120,
  status: 'Released',
  originalLanguage: 'en',
  creator: null,
  directors: ['Test Director'],
  cast: [],
  videos: [],
  keywords: [],
  recommendations: []
});

const paged = (items: unknown[]) => ({
  page: 1,
  totalPages: 1,
  items,
  totalResults: items.length
});

async function mockApi(page: Page): Promise<void> {
  await page.route('**/api/**', async route => {
    const path = new URL(route.request().url()).pathname.replace(/^\/api/, '');
    const json = async (body: unknown) =>
      route.fulfill({
        status: 200,
        contentType: 'application/json',
        body: JSON.stringify(body)
      });

    if (path.startsWith('/movies/trending')) {
      return json(paged([movieCard(1, 'Trending Movie')]));
    }
    if (path.startsWith('/tv/trending')) {
      return json(paged([]));
    }
    if (path.startsWith('/movies/popular')) {
      return json(paged([movieCard(2, 'Popular Movie')]));
    }
    if (path.startsWith('/search/all')) {
      return json(paged([movieCard(3, 'Batman Begins')]));
    }
    if (/^\/movies\/\d+$/.test(path)) {
      return json(movieDetails(1, 'Trending Movie'));
    }

    return route.fulfill({
      status: 404,
      contentType: 'application/json',
      body: JSON.stringify({ message: `Unmocked API path: ${path}` })
    });
  });
}

test('smoke: home → details → list → search', async ({ page }) => {
  await mockApi(page);

  await page.goto('/');
  await expect(page.locator('app-media-card').first()).toBeVisible();
  await expect(page.getByText('Trending Movie', { exact: true })).toBeVisible();

  await page.locator('app-media-carousel app-media-card').first().click();
  await expect(page).toHaveURL(/\/movies\/details\/1$/);
  await expect(page.getByRole('heading', { level: 1 })).toHaveText('Trending Movie');
  await expect(page.getByText('A test overview')).toBeVisible();

  await page.goto('/movies/popular');
  await expect(page.getByRole('heading', { level: 1 })).toContainText('Movies · Popular');
  await expect(page.getByText('Popular Movie')).toBeVisible();

  await page.goto('/search');
  await page.getByPlaceholder('Search movies or TV...').fill('batman');
  await page.getByRole('button', { name: 'Search' }).click();
  await expect(page.getByText('Batman Begins')).toBeVisible();
});
