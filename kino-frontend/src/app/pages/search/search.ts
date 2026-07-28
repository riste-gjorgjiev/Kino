import { Component } from '@angular/core';
import {CommonModule} from '@angular/common';
import {FormsModule} from '@angular/forms';
import {MediaCardDto} from '../../core/models/media-card.dto';
import {KinoApiService} from '../../kino-api.service';
import {forkJoin} from 'rxjs';
import {ActivatedRoute, Route, Router, RouterLink} from '@angular/router';
import {DEFAULT_FILTER, FilterState} from '../../core/models/filter-state.model';
import {FilterControlsComponent} from '../../shared/filter-controls/filter-controls.component';

type SearchTab = 'all' | 'movie' | 'tv'

@Component({
  selector: 'app-search',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink, FilterControlsComponent],
  templateUrl: './search.html',
  styleUrl: './search.css',
})
export class Search {
  q = '';
  tab: SearchTab = 'all'

  moviePage = 1;
  tvPage = 1;
  allPage = 1;
  movieTotalPages = 1;
  tvTotalPages = 1;
  allTotalPages = 1;

  movieItems: MediaCardDto[] = [];
  tvItems: MediaCardDto[] = [];
  allItems: MediaCardDto[] = [];

  loading = false;
  error = '';
  hasSearched = false;

  filter: FilterState = { ...DEFAULT_FILTER }

  constructor(
    private api: KinoApiService,
    private route: ActivatedRoute,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.readFiltersFromUrl();
  }

  onFilterChanged(filter: FilterState): void {
    this.filter = { ...filter };
  }

  applyFilters(): void {
    this.resetPagination();
    this.syncFiltersToUrl();
    if (this.hasSearched){
      this.fetch();
    }
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

  private resetPagination(): void {
    this.moviePage = 1;
    this.tvPage = 1;
    this.allPage = 1;
  }

  submit(){
    const query = this.q.trim();
    if (!query) return;

    this.hasSearched = true;
    this.resetPagination();
    this.fetch();
  }

  setTab(t: SearchTab){
    if (this.tab === t) return;

    this.tab = t;
    if (this.hasSearched){
      this.resetPagination();
      this.fetch();
    }
  }

  fetch(){
    const query = this.q.trim();
    if (!query){
      this.error = 'Please enter search parameters';
      return;
    }

    this.loading = true;
    this.error = '';

    if (this.tab === 'all'){
      this.api.searchAll(query, this.allPage, this.filter).subscribe({
        next: res => {
          this.allItems = res.items;
          this.allTotalPages = res.totalPages;
          this.loading = false;
        },
        error: err => {
          this.error = err?.error?.message ?? 'Search failed. Try again';
          this.loading = false;
        }
      });
      return;
    }

    if (this.tab === 'movie'){
      this.api.searchMovies(query, this.moviePage, this.filter).subscribe({
        next: res => {
          this.movieItems = res.items;
          this.movieTotalPages = res.totalPages;
          this.loading = false;
        },
        error: err => {
          this.error = err?.error?.message ?? 'Search failed. Try again';
          this.loading = false;
        }
      });
      return;
    }
    if (this.tab === 'tv'){
      this.api.searchTv(query, this.tvPage, this.filter).subscribe({
        next: res => {
          this.tvItems = res.items;
          this.tvTotalPages = res.totalPages;
          this.loading = false;
        },
        error: err => {
          this.error = err?.error?.message ?? 'Search failed. Try again';
          this.loading = false;
        }
      });
      return;
    }
  }

  prevMovies(){
    if (this.moviePage > 1) this.moviePage--;
    this.fetch();
  }
  prevTv(){
    if (this.tvPage > 1) this.tvPage--;
    this.fetch();
  }

  nextMovie(){
    if (this.moviePage < this.movieTotalPages){
      this.moviePage++;
      this.fetch();
    }
  }
  nextTv(){
    if (this.tvPage < this.tvTotalPages){
      this.tvPage++;
      this.fetch();
    }
  }
}
