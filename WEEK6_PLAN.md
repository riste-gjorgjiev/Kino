# Week 6 Plan: Keywords & Recommendations

## 1. Week 6 Goal & Decisions

**Goal:** enrich the existing movie/TV details endpoint with **keywords**, **similar titles**, and **TMDB recommendations**, then render them on the Angular details page.

| Decision | Rationale |
|----------|-----------|
| Reuse the existing `/api/movies/{id}` and `/api/tv/{id}` endpoints | No new controllers or route changes; the details DTO simply carries more data. |
| Cap combined *similar + recommendations* at **12** items | Keeps the page fast and prevents carousel overload. |
| Deduplicate by TMDB id, with **recommendations first**, then *similar* | Recommendations are generally higher quality; similar fills the remaining slots without duplicates. |
| Expose keywords as a flat list of chips | Simple discoverability; no keyword search functionality this week. |
| Reuse `MediaCarouselComponent` + `MediaCardComponent` for recommendations | Consistent card sizing, routing, and responsive behavior already exist. |
| Treat keywords/recommendations as **non-critical** enrichment | Wrap TMDB calls so failures here never break the whole details page. |
| Caching is **optional/behind existing Caffeine config** | Cache names are added to `CacheConfig`, but `@Cacheable` annotations are a nice-to-have unless load tests show a need. |
| No Tailwind; keep current plain CSS pattern | Matches Week 5 styling and existing component conventions. |

---

## 2. Architecture Overview

```text
TMDB API
   │
   ├── /movie/{id}           ┐
   ├── /movie/{id}/credits   │
   ├── /movie/{id}/videos    ├─ spring RestClient
   ├── /movie/{id}/keywords  │   (kino-backend/.../tmdb/TmdbClient.java)
   ├── /movie/{id}/recommendations │
   ├── /movie/{id}/similar   ┘
   └── /tv/{id}/*  (same shape)
            │
            ▼
   MediaDetailsService  (enrichment + safe calls + mapping)
            │
            ▼
   MediaDetailsDto  (+ keywords, recommendations)
            │
            ▼
   MovieController / TvController  (/api/movies/{id}, /api/tv/{id})
            │
            ▼
   Angular KinoApiService → Details page
            │
            ├── KeywordSectionComponent  (keyword chips)
            └── MediaCarouselComponent   (recommendations, reuses MediaCardComponent)
```

---

## 3. Backend Tasks

> All paths are relative to `/home/riste/IdeaProjects/Kino/kino-backend/src/main/java/mk/ukim/finki/wp/kino/` unless noted.

### 3.1 Create keyword DTOs

**New file:** `dto/tmdb/details/misc/TmdbKeywordDto.java`

```java
package mk.ukim.finki.wp.kino.dto.tmdb.details.misc;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TmdbKeywordDto {
    private Long id;
    private String name;
}
```

**New file:** `dto/tmdb/details/misc/TmdbKeywordsDto.java`

TMDB uses `keywords` for movies and `results` for TV; this single DTO handles both.

```java
package mk.ukim.finki.wp.kino.dto.tmdb.details.misc;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class TmdbKeywordsDto {
    @JsonProperty("keywords")
    private List<TmdbKeywordDto> keywords;

    @JsonProperty("results")
    private List<TmdbKeywordDto> results;

    public List<TmdbKeywordDto> getAllKeywords() {
        if (keywords != null) return keywords;
        return results != null ? results : List.of();
    }
}
```

**New file:** `dto/tmdb/details/KeywordDto.java` (public API shape)

```java
package mk.ukim.finki.wp.kino.dto.tmdb.details;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class KeywordDto {
    private Long id;
    private String name;
}
```

### 3.2 Add TmdbClient methods

**Edit:** `tmdb/TmdbClient.java`

Add a new type reference and six new methods. Note that recommendations/similar reuse the existing movie/TV page types.

```java
private static final ParameterizedTypeReference<TmdbKeywordsDto> TMDB_KEYWORDS_TYPE =
        new ParameterizedTypeReference<>() {};

public TmdbKeywordsDto getMovieKeywords(long id) {
    return restClient.get()
            .uri(uriBuilder -> uriBuilder
                    .path("/movie/{id}/keywords")
                    .queryParam("api_key", apiKey)
                    .queryParam("language", language)
                    .build(id))
            .retrieve()
            .body(TMDB_KEYWORDS_TYPE);
}

public TmdbKeywordsDto getTvKeywords(long id) {
    return restClient.get()
            .uri(uriBuilder -> uriBuilder
                    .path("/tv/{id}/keywords")
                    .queryParam("api_key", apiKey)
                    .queryParam("language", language)
                    .build(id))
            .retrieve()
            .body(TMDB_KEYWORDS_TYPE);
}

public TmdbPagedResponse<TmdbMovieDto> getMovieRecommendations(long id, int page) {
    if (page < 1) page = 1;
    int finalPage = page;
    return restClient.get()
            .uri(uriBuilder -> uriBuilder
                    .path("/movie/{id}/recommendations")
                    .queryParam("api_key", apiKey)
                    .queryParam("language", language)
                    .queryParam("page", finalPage)
                    .build(id))
            .retrieve()
            .body(MOVIE_PAGE_TYPE);
}

public TmdbPagedResponse<TmdbMovieDto> getMovieSimilar(long id, int page) {
    if (page < 1) page = 1;
    int finalPage = page;
    return restClient.get()
            .uri(uriBuilder -> uriBuilder
                    .path("/movie/{id}/similar")
                    .queryParam("api_key", apiKey)
                    .queryParam("language", language)
                    .queryParam("page", finalPage)
                    .build(id))
            .retrieve()
            .body(MOVIE_PAGE_TYPE);
}

public TmdbPagedResponse<TmdbTvDto> getTvRecommendations(long id, int page) {
    if (page < 1) page = 1;
    int finalPage = page;
    return restClient.get()
            .uri(uriBuilder -> uriBuilder
                    .path("/tv/{id}/recommendations")
                    .queryParam("api_key", apiKey)
                    .queryParam("language", language)
                    .queryParam("page", finalPage)
                    .build(id))
            .retrieve()
            .body(TV_PAGE_TYPE);
}

public TmdbPagedResponse<TmdbTvDto> getTvSimilar(long id, int page) {
    if (page < 1) page = 1;
    int finalPage = page;
    return restClient.get()
            .uri(uriBuilder -> uriBuilder
                    .path("/tv/{id}/similar")
                    .queryParam("api_key", apiKey)
                    .queryParam("language", language)
                    .queryParam("page", finalPage)
                    .build(id))
            .retrieve()
            .body(TV_PAGE_TYPE);
}
```

### 3.3 Enrich MediaDetailsDto

**Edit:** `dto/tmdb/details/MediaDetailsDto.java`

```java
import mk.ukim.finki.wp.kino.dto.api.MediaCardDto;

// ... existing fields ...
private List<KeywordDto> keywords;
private List<MediaCardDto> recommendations;
```

### 3.4 Wire enrichment into MediaDetailsService

**Edit:** `service/MediaDetailsService.java`

Add imports:

```java
import mk.ukim.finki.wp.kino.dto.api.MediaCardDto;
import mk.ukim.finki.wp.kino.dto.api.MediaType;
import mk.ukim.finki.wp.kino.dto.tmdb.TmdbMovieDto;
import mk.ukim.finki.wp.kino.dto.tmdb.TmdbTvDto;
import mk.ukim.finki.wp.kino.dto.tmdb.details.KeywordDto;
import mk.ukim.finki.wp.kino.dto.tmdb.details.misc.TmdbKeywordDto;
import mk.ukim.finki.wp.kino.dto.tmdb.details.misc.TmdbKeywordsDto;

import java.util.function.Supplier;
```

Add a safe-call helper:

```java
private <T> T safeCall(Supplier<T> call) {
    try {
        return call.get();
    } catch (Exception e) {
        return null;
    }
}
```

Add mapping helpers:

```java
private List<KeywordDto> mapKeywords(TmdbKeywordsDto response) {
    if (response == null) return List.of();
    return response.getAllKeywords().stream()
            .filter(k -> k.getName() != null && !k.getName().isBlank())
            .map(k -> {
                KeywordDto dto = new KeywordDto();
                dto.setId(k.getId());
                dto.setName(k.getName().trim());
                return dto;
            })
            .toList();
}

private List<MediaCardDto> buildMovieRecommendations(TmdbPagedResponse<TmdbMovieDto> recommendations,
                                                     TmdbPagedResponse<TmdbMovieDto> similar) {
    List<MediaCardDto> merged = new ArrayList<>();
    Set<Long> seen = new HashSet<>();

    if (recommendations != null && recommendations.getResults() != null) {
        recommendations.getResults().stream()
                .filter(m -> m.getId() != null)
                .filter(m -> seen.add(m.getId()))
                .map(m -> new MediaCardDto(m.getId(), MediaType.MOVIE, m.getTitle(),
                        fullImageUrl(m.getPosterPath()), m.getVoteAverage(), m.getReleaseDate()))
                .forEach(merged::add);
    }

    if (similar != null && similar.getResults() != null) {
        similar.getResults().stream()
                .filter(m -> m.getId() != null)
                .filter(m -> seen.add(m.getId()))
                .map(m -> new MediaCardDto(m.getId(), MediaType.MOVIE, m.getTitle(),
                        fullImageUrl(m.getPosterPath()), m.getVoteAverage(), m.getReleaseDate()))
                .forEach(merged::add);
    }

    return merged.stream().limit(12).toList();
}

private List<MediaCardDto> buildTvRecommendations(TmdbPagedResponse<TmdbTvDto> recommendations,
                                                  TmdbPagedResponse<TmdbTvDto> similar) {
    List<MediaCardDto> merged = new ArrayList<>();
    Set<Long> seen = new HashSet<>();

    if (recommendations != null && recommendations.getResults() != null) {
        recommendations.getResults().stream()
                .filter(t -> t.getId() != null)
                .filter(t -> seen.add(t.getId()))
                .map(t -> new MediaCardDto(t.getId(), MediaType.TV, t.getName(),
                        fullImageUrl(t.getPosterPath()), t.getVoteAverage(), t.getFirstAirDate()))
                .forEach(merged::add);
    }

    if (similar != null && similar.getResults() != null) {
        similar.getResults().stream()
                .filter(t -> t.getId() != null)
                .filter(t -> seen.add(t.getId()))
                .map(t -> new MediaCardDto(t.getId(), MediaType.TV, t.getName(),
                        fullImageUrl(t.getPosterPath()), t.getVoteAverage(), t.getFirstAirDate()))
                .forEach(merged::add);
    }

    return merged.stream().limit(12).toList();
}
```

Update `getMovieDetails`:

```java
TmdbKeywordsDto movieKeywords = safeCall(() -> tmdbClient.getMovieKeywords(id));
List<MediaCardDto> movieRecommendations = buildMovieRecommendations(
        safeCall(() -> tmdbClient.getMovieRecommendations(id, 1)),
        safeCall(() -> tmdbClient.getMovieSimilar(id, 1))
);

// ... after setting videos ...
dto.setKeywords(mapKeywords(movieKeywords));
dto.setRecommendations(movieRecommendations);
```

Update `getTvDetails`:

```java
TmdbKeywordsDto tvKeywords = safeCall(() -> tmdbClient.getTvKeywords(id));
List<MediaCardDto> tvRecommendations = buildTvRecommendations(
        safeCall(() -> tmdbClient.getTvRecommendations(id, 1)),
        safeCall(() -> tmdbClient.getTvSimilar(id, 1))
);

// ... after setting videos ...
dto.setKeywords(mapKeywords(tvKeywords));
dto.setRecommendations(tvRecommendations);
```

### 3.5 Optional caching

**Edit:** `config/CacheConfig.java`

Add new cache names if you decide to cache:

```java
CaffeineCacheManager caffeineCacheManager = new CaffeineCacheManager(
        "trendingMovies", "popularMovies", "topRatedMovies", "upcomingMovies",
        "trendingTv", "popularTv", "topRatedTv", "airingTvShows",
        "searchMovies", "searchTv",
        "movieKeywords", "tvKeywords",
        "movieRecommendations", "tvRecommendations",
        "movieSimilar", "tvSimilar"
);
```

Then annotate the new `TmdbClient` methods (optional):

```java
@Cacheable(value = "movieKeywords", key = "#id")
public TmdbKeywordsDto getMovieKeywords(long id) { ... }

@Cacheable(value = "tvKeywords", key = "#id")
public TmdbKeywordsDto getTvKeywords(long id) { ... }
// ... etc
```

> Keep `@Cacheable` off the safe wrappers; cache the raw TMDB responses so failed calls are not cached as null.

### 3.6 Update TestDataFactory

**Edit:** `src/test/java/mk/ukim/finki/wp/kino/util/TestDataFactory.java`

Add helpers:

```java
public static TmdbKeywordDto createKeyword(Long id, String name) {
    TmdbKeywordDto keyword = new TmdbKeywordDto();
    keyword.setId(id);
    keyword.setName(name);
    return keyword;
}

public static TmdbKeywordsDto createTmdbMovieKeywords(List<TmdbKeywordDto> keywords) {
    TmdbKeywordsDto dto = new TmdbKeywordsDto();
    dto.setKeywords(keywords);
    return dto;
}

public static TmdbKeywordsDto createTmdbTvKeywords(List<TmdbKeywordDto> keywords) {
    TmdbKeywordsDto dto = new TmdbKeywordsDto();
    dto.setResults(keywords);
    return dto;
}

public static KeywordDto createKeywordDto(Long id, String name) {
    KeywordDto dto = new KeywordDto();
    dto.setId(id);
    dto.setName(name);
    return dto;
}
```

### 3.7 Add/update backend unit tests

**Edit:** `src/test/java/mk/ukim/finki/wp/kino/service/MediaDetailsServiceTest.java`

Add tests such as:

```java
@Test
void getMovieDetails_populatesKeywords() {
    when(tmdbClient.getMovieDetails(anyLong())).thenReturn(TestDataFactory.createMovieDetails());
    when(tmdbClient.getMovieCredits(anyLong())).thenReturn(TestDataFactory.createCredits());
    when(tmdbClient.getMovieVideos(anyLong())).thenReturn(null);
    when(tmdbClient.getMovieKeywords(anyLong())).thenReturn(
            TestDataFactory.createTmdbMovieKeywords(List.of(
                    TestDataFactory.createKeyword(1L, "superhero"),
                    TestDataFactory.createKeyword(2L, "  blank-name  ")
            ))
    );
    when(tmdbClient.getMovieRecommendations(anyLong(), anyInt())).thenReturn(null);
    when(tmdbClient.getMovieSimilar(anyLong(), anyInt())).thenReturn(null);

    MediaDetailsDto result = mediaDetailsService.getMovieDetails(1L);

    assertEquals(2, result.getKeywords().size());
    assertEquals("superhero", result.getKeywords().get(0).getName());
}

@Test
void getTvDetails_populatesKeywordsUsingResultsField() {
    when(tmdbClient.getTvDetails(anyLong())).thenReturn(TestDataFactory.createTvDetails());
    when(tmdbClient.getTvCredits(anyLong())).thenReturn(TestDataFactory.createCredits());
    when(tmdbClient.getTvVideos(anyLong())).thenReturn(null);
    when(tmdbClient.getTvKeywords(anyLong())).thenReturn(
            TestDataFactory.createTmdbTvKeywords(List.of(TestDataFactory.createKeyword(3L, "drama")))
    );
    when(tmdbClient.getTvRecommendations(anyLong(), anyInt())).thenReturn(null);
    when(tmdbClient.getTvSimilar(anyLong(), anyInt())).thenReturn(null);

    MediaDetailsDto result = mediaDetailsService.getTvDetails(1L);

    assertEquals(1, result.getKeywords().size());
    assertEquals("drama", result.getKeywords().get(0).getName());
}

@Test
void getMovieDetails_combinesRecommendationsAndSimilarAndCapsAt12() {
    when(tmdbClient.getMovieDetails(anyLong())).thenReturn(TestDataFactory.createMovieDetails());
    when(tmdbClient.getMovieCredits(anyLong())).thenReturn(TestDataFactory.createCredits());
    when(tmdbClient.getMovieVideos(anyLong())).thenReturn(null);
    when(tmdbClient.getMovieKeywords(anyLong())).thenReturn(null);

    List<TmdbMovieDto> recs = new ArrayList<>();
    for (int i = 0; i < 10; i++) {
        recs.add(TestDataFactory.createMovie(100L + i, "Rec " + i, "/p" + i + ".jpg"));
    }
    List<TmdbMovieDto> sims = new ArrayList<>();
    for (int i = 0; i < 10; i++) {
        sims.add(TestDataFactory.createMovie(200L + i, "Sim " + i, "/s" + i + ".jpg"));
    }

    when(tmdbClient.getMovieRecommendations(anyLong(), anyInt())).thenReturn(
            TestDataFactory.createPagedResponse(recs)
    );
    when(tmdbClient.getMovieSimilar(anyLong(), anyInt())).thenReturn(
            TestDataFactory.createPagedResponse(sims)
    );

    MediaDetailsDto result = mediaDetailsService.getMovieDetails(1L);

    assertEquals(12, result.getRecommendations().size());
    assertTrue(result.getRecommendations().stream()
            .allMatch(r -> r.getMediaType() == MediaType.MOVIE));
}

@Test
void getMovieDetails_nonCriticalFailuresReturnEmptyLists() {
    when(tmdbClient.getMovieDetails(anyLong())).thenReturn(TestDataFactory.createMovieDetails());
    when(tmdbClient.getMovieCredits(anyLong())).thenReturn(TestDataFactory.createCredits());
    when(tmdbClient.getMovieVideos(anyLong())).thenReturn(null);
    when(tmdbClient.getMovieKeywords(anyLong())).thenThrow(new RuntimeException(" TMDB down " ));
    when(tmdbClient.getMovieRecommendations(anyLong(), anyInt())).thenThrow(new RuntimeException(" TMDB down"));
    when(tmdbClient.getMovieSimilar(anyLong(), anyInt())).thenThrow(new RuntimeException(" TMDB down"));

    MediaDetailsDto result = mediaDetailsService.getMovieDetails(1L);

    assertNotNull(result);
    assertTrue(result.getKeywords().isEmpty());
    assertTrue(result.getRecommendations().isEmpty());
}
```

> If caching annotations were added, also add a quick `@SpringBootTest` or `TmdbClientTest` to confirm cache keys are used.

---

## 4. Frontend Tasks

> All paths are relative to `/home/riste/IdeaProjects/Kino/kino-frontend/src/app/` unless noted.

### 4.1 Create KeywordDto

**New file:** `core/models/keyword-dto.ts`

```typescript
export interface KeywordDto {
  id: number;
  name: string;
}
```

### 4.2 Update MediaDetailsDto

**Edit:** `core/models/media-details-dto.ts`

```typescript
import { KeywordDto } from './keyword-dto';
import { MediaCardDto } from './media-card.dto';

export interface MediaDetailsDto {
  // ... existing fields ...
  keywords: KeywordDto[];
  recommendations: MediaCardDto[];
}
```

### 4.3 Create KeywordSectionComponent

**New file:** `shared/keyword-section/keyword-section.component.ts`

```typescript
import { Component, Input } from '@angular/core';
import { CommonModule } from '@angular/common';
import { KeywordDto } from '../../core/models/keyword-dto';

@Component({
  selector: 'app-keyword-section',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './keyword-section.component.html',
  styleUrl: './keyword-section.component.css'
})
export class KeywordSectionComponent {
  @Input({ required: true }) keywords: KeywordDto[] = [];
}
```

**New file:** `shared/keyword-section/keyword-section.component.html`

```html
<section class="keyword-section" *ngIf="keywords?.length">
  <h3>Keywords</h3>
  <div class="keyword-list">
    <span class="keyword-chip" *ngFor="let keyword of keywords">
      {{ keyword.name }}
    </span>
  </div>
</section>
```

**New file:** `shared/keyword-section/keyword-section.component.css`

```css
.keyword-section {
  max-width: 1200px;
  margin: 2rem auto;
  padding: 0 2rem;
  color: #fff;
}

.keyword-section h3 {
  margin: 0 0 1rem;
  font-size: 1.25rem;
}

.keyword-list {
  display: flex;
  flex-wrap: wrap;
  gap: 0.5rem;
}

.keyword-chip {
  display: inline-block;
  background: rgba(255, 255, 255, 0.1);
  border: 1px solid rgba(255, 255, 255, 0.15);
  border-radius: 999px;
  padding: 0.35rem 0.8rem;
  font-size: 0.85rem;
}

@media (max-width: 768px) {
  .keyword-section {
    padding: 0 1rem;
  }
}
```

### 4.4 Integrate into the Details page

**Edit:** `pages/details/details.ts`

Import and register the new components:

```typescript
import { MediaCarouselComponent } from '../../shared/media-carousel/media-carousel.component';
import { KeywordSectionComponent } from '../../shared/keyword-section/keyword-section.component';

@Component({
  // ...
  imports: [
    CommonModule,
    VideoSectionComponent,
    TrailerModalComponent,
    MediaCarouselComponent,
    KeywordSectionComponent
  ],
  // ...
})
```

**Edit:** `pages/details/details.html`

Add the keyword section and recommendations carousel after the video section:

```html
  <app-video-section
    *ngIf="data?.videos?.length"
    [videos]="data.videos"
    (playVideo)="onPlayVideo($event)">
  </app-video-section>

  <app-keyword-section
    *ngIf="data?.keywords?.length"
    [keywords]="data.keywords">
  </app-keyword-section>

  <section class="recommendations" *ngIf="data?.recommendations?.length">
    <app-media-carousel
      title="More Like This"
      [items]="data.recommendations">
    </app-media-carousel>
  </section>
```

**Edit:** `pages/details/details.css`

Add spacing for the recommendations wrapper:

```css
.recommendations {
  max-width: 1200px;
  margin: 3rem auto;
  padding: 0 2rem;
}

@media (max-width: 768px) {
  .recommendations {
    padding: 0 1rem;
  }
}
```

### 4.5 Add frontend unit tests

**New file:** `shared/keyword-section/keyword-section.component.spec.ts`

```typescript
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { KeywordSectionComponent } from './keyword-section.component';

describe('KeywordSectionComponent', () => {
  let component: KeywordSectionComponent;
  let fixture: ComponentFixture<KeywordSectionComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [KeywordSectionComponent]
    }).compileComponents();

    fixture = TestBed.createComponent(KeywordSectionComponent);
    component = fixture.componentInstance;
    component.keywords = [
      { id: 1, name: 'superhero' },
      { id: 2, name: 'action' }
    ];
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should render chips for each keyword', () => {
    const chips = fixture.nativeElement.querySelectorAll('.keyword-chip');
    expect(chips.length).toBe(2);
    expect(chips[0].textContent).toContain('superhero');
  });
});
```

**Edit:** `pages/details/details.spec.ts`

Expand the existing test to include keywords and recommendations:

```typescript
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';
import { Details } from './details';
import { KinoApiService } from '../../kino-api.service';
import { By } from '@angular/platform-browser';

const fakeDetails = {
  id: 1,
  mediaType: 'MOVIE',
  title: 'Test Movie',
  tagline: null,
  overview: 'Overview',
  posterUrl: null,
  backdropUrl: null,
  rating: null,
  date: null,
  genres: [],
  runtimeMinutes: null,
  status: null,
  originalLanguage: null,
  creator: null,
  directors: [],
  cast: [],
  videos: [],
  keywords: [{ id: 1, name: 'test-keyword' }],
  recommendations: [
    { id: 2, mediaType: 'MOVIE', title: 'Rec', posterUrl: '/p.jpg', rating: 7, date: '2024-01-01' }
  ]
};

describe('Details', () => {
  let component: Details;
  let fixture: ComponentFixture<Details>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [Details],
      providers: [
        { provide: ActivatedRoute, useValue: { snapshot: { paramMap: { get: () => '1' } }, params: of({}) } },
        { provide: KinoApiService, useValue: { detailsMovie: () => of(fakeDetails), detailsTv: () => of(fakeDetails) } }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(Details);
    component = fixture.componentInstance;
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should render keywords when present', () => {
    component.data = fakeDetails;
    fixture.detectChanges();
    const chip = fixture.debugElement.query(By.css('.keyword-chip'));
    expect(chip.nativeElement.textContent).toContain('test-keyword');
  });

  it('should render recommendations carousel when present', () => {
    component.data = fakeDetails;
    fixture.detectChanges();
    const carousel = fixture.debugElement.query(By.css('app-media-carousel'));
    expect(carousel).toBeTruthy();
  });
});
```

> Vitest note: if `TestBed` tests fail under Vitest, fall back to a simple component creation test and verify via the existing `npm run test` script.

---

## 5. Verification Checklist

| # | Check | How |
|---|-------|-----|
| 1 | Backend unit tests pass | `cd kino-backend && ./mvnw test` |
| 2 | Frontend unit tests pass | `cd kino-frontend && npm run test` |
| 3 | Backend builds cleanly | `cd kino-backend && ./mvnw package -DskipTests` |
| 4 | Frontend builds cleanly | `cd kino-frontend && npm run build` |
| 5 | Keywords render as chips | Open any movie details page → confirm "Keywords" section |
| 6 | Recommendations carousel works | Confirm "More Like This" carousel scrolls and cards link to details |
| 7 | Responsive layout | Check widths: 320px, 768px, 1440px+ |
| 8 | Graceful degradation | Temporarily block `/keywords`, `/recommendations`, `/similar` TMDB calls → page still loads, sections hidden |
| 9 | Dedup & cap | Choose a title with both recommendations and similar → verify max 12 unique items |
| 10 | TV keywords work | Open a TV details page → keywords use the `results` TMDB field correctly |

---

## 6. Risks & Mitigations

| Risk | Impact | Mitigation |
|------|--------|------------|
| TMDB movie vs TV keyword JSON shape differs (`keywords` vs `results`) | High | Use `TmdbKeywordsDto` with both `@JsonProperty` aliases and a single `getAllKeywords()` accessor. |
| Similar/recommendation calls are slow or fail | Medium | Wrap them in `safeCall`; failures produce empty lists and do not break the details payload. |
| Large keyword lists overflow the page | Low | CSS `flex-wrap` keeps chips tidy; cap is not required because keywords are usually small in number. |
| Duplicate items between recommendations and similar | Medium | Deduplicate by `id` before limiting to 12. |
| Cache stores null/error responses if `@Cacheable` misapplied | Medium | Only annotate `TmdbClient` methods; never cache the `safeCall` result directly. |
| `MediaCardDto` backend enum serializes as `"MOVIE"`/`"TV"` | Low | Confirm Angular `MediaType` union type matches (`'MOVIE' \| 'TV'`). |

---

## 7. Optional Next Steps / Week 7 Preview

*Week 6 is complete when the checklist is green.*

**Week 7 preview (TV Seasons & Episodes):**

- New backend endpoints:
  - `/api/tv/{id}/seasons` – season list for a TV show.
  - `/api/tv/{id}/seasons/{seasonNumber}` – episode list for a season.
- New backend DTOs: `SeasonDto`, `EpisodeDto`, `TvSeasonDetailsDto`.
- New `TmdbClient` methods for `/tv/{id}`, `/tv/{id}/season/{n}`, and `/tv/{id}/aggregate_credits`.
- Frontend:
  - Season selector on the TV details page.
  - Episode guide component/table.
  - Route updates to support season/episode deep-links (optional).

**Week 6 stretch ideas (only if time permits):**

- Clickable keyword chips that search for titles sharing the keyword.
- Add `@Cacheable` on new TmdbClient keyword/recommendation methods backed by the new cache names.
- Surface "Similar" and "Recommended" as two separate carousels instead of one merged list.

---

*End of Week 6 plan.*
