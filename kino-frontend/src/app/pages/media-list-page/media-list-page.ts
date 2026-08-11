import { Component } from '@angular/core';
import {CommonModule} from '@angular/common';
import {MediaCardDto} from '../../core/models/media-card.dto';
import {ActivatedRoute, Router} from '@angular/router';
import {KinoApiService} from '../../kino-api.service';
import {combineLatest} from 'rxjs';
import {DEFAULT_FILTER, FilterState} from '../../core/models/filter-state.model';
import {FilterControlsComponent} from '../../shared/filter-controls/filter-controls.component';
import {MediaCardComponent} from '../../shared/media-card/media-card.component';

type MoviesCategory = 'popular' | 'upcoming' | 'top-rated';
type TvCategory = 'popular' | 'on-the-air' | 'top-rated';

@Component({
  selector: 'app-media-list-page',
  imports: [CommonModule, FilterControlsComponent, MediaCardComponent],
  templateUrl: './media-list-page.html',
  styleUrl: './media-list-page.css',
})
export class MediaListPage {
  items: MediaCardDto[] = [];
  page = 1;
  totalPages = 1;
  loading = false;
  error = '';

  title = '';
  filter: FilterState = { ...DEFAULT_FILTER }

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private api: KinoApiService
  ) {}

  ngOnInit(): void {
    combineLatest([this.route.data, this.route.paramMap, this.route.queryParamMap]).subscribe(
      () => {
      this.readFiltersFromUrl();
      this.page = 1;
      this.load();
    });
  }

  onFilterChanged(filter: FilterState): void {
    this.filter = { ...filter };
  }

  applyFilters(): void {
    this.page = 1;
    this.syncFiltersToUrl();
    this.load();
  }

  private readFiltersFromUrl(): void {
    const queryParams = this.route.snapshot.queryParamMap;
    this.filter = {
      yearFrom: queryParams.has('yearFrom') ? Number(queryParams.get('yearFrom')) : null,
      yearTo: queryParams.has('yearTo') ? Number(queryParams.get('yearTo')) : null,
      sortBy: queryParams.get('sortBy'),
      sortOrder: (queryParams.get('sortOrder') as 'asc' | 'desc') || 'asc',
    };
  }

  private syncFiltersToUrl(): void {
    const queryParams: any = {};
    if (this.filter.yearFrom != null) queryParams.yearFrom = this.filter.yearFrom;
    if (this.filter.yearTo != null) queryParams.yearTo = this.filter.yearTo;
    if (this.filter.sortBy) queryParams.sortBy = this.filter.sortBy;
    if (this.filter.sortOrder !== 'asc') queryParams.sortOrder = this.filter.sortOrder;

    this.router.navigate([], {
      relativeTo: this.route,
      queryParams,
      queryParamsHandling: 'merge',
    });
  }

  load(){
    const media = this.route.snapshot.url[0]?.path;
    const category = this.route.snapshot.paramMap.get('category') || '';

    this.title = this.makeTitle(media, category);

    this.loading = true;
    this.error = '';

    const req =
      media === 'movies' ? this.fetchMovies(category as MoviesCategory, this.page)
      : this.fetchTv(category as TvCategory, this.page);

    req.subscribe({
      next: (res) => {
        this.items = res.items;
        this.page = res.page;
        this.totalPages = res.totalPages;
        this.loading = false;
      },
      error: (err) => {
        this.error = 'Failed to load content. Please try again.';
        this.loading = false;
        console.error(err);
      },
    });
  }

  private fetchMovies(category: MoviesCategory, page: number){
    switch (category){
      case 'popular': return this.api.popularMovies(page, this.filter);
      case 'upcoming': return this.api.upcomingMovies(page, this.filter);
      case 'top-rated': return this.api.topRatedMovies(page, this.filter);
    }
  }

  private fetchTv(category: TvCategory, page: number){
    switch (category){
      case 'popular': return this.api.popularTv(page, this.filter);
      case 'on-the-air': return this.api.onTheAirTv(page, this.filter);
      case 'top-rated': return this.api.topRatedTv(page, this.filter);
    }
  }

  private makeTitle(media: string, category: string){
    const m = media === 'movies' ? 'Movies' : 'TV';
    const c = category.replace('-', ' ');
    return `${m} · ${c.charAt(0).toUpperCase() + c.slice(1)}`;
  }

  prev() { if (this.page > 1) { this.page--; this.load(); } }
  next() { if (this.page < this.totalPages) { this.page++; this.load(); } }
}
