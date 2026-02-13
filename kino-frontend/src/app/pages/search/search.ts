import { Component } from '@angular/core';
import {CommonModule} from '@angular/common';
import {FormsModule} from '@angular/forms';
import {MediaCardDto} from '../../core/models/media-card.dto';
import {KinoApiService} from '../../kino-api.service';
import {forkJoin} from 'rxjs';
import {RouterLink} from '@angular/router';

type SearchTab = 'all' | 'movie' | 'tv'

@Component({
  selector: 'app-search',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink],
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

  constructor(private api: KinoApiService) {}

  submit(){
    const query = this.q.trim();
    if (!query) return;

    this.hasSearched = true;
    this.moviePage = 1;
    this.tvPage = 1;
    this.allPage = 1;
    this.fetch();
  }

  setTab(t: SearchTab){
    if (this.tab === t) return;

    this.tab = t;
    if (this.hasSearched){
      if (t === 'movie') this.moviePage = 1;
      if (t === 'tv') this.tvPage = 1;
      if (t === 'all') this.allPage = 1;
      this.fetch()
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
      this.api.searchAll(query, this.allPage).subscribe({
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
      this.api.searchMovies(query, this.allPage).subscribe({
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
      this.api.searchTv(query, this.allPage).subscribe({
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
