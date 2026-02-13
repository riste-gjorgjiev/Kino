import { Component } from '@angular/core';
import {CommonModule} from '@angular/common';
import {MediaCardDto} from '../../core/models/media-card.dto';
import {ActivatedRoute, RouterLink} from '@angular/router';
import {KinoApiService} from '../../kino-api.service';
import {combineLatest} from 'rxjs';

type MoviesCategory = 'popular' | 'upcoming' | 'top-rated';
type TvCategory = 'popular' | 'on-the-air' | 'top-rated';

@Component({
  selector: 'app-media-list-page',
  imports: [CommonModule, RouterLink],
  templateUrl: './media-list-page.html',
  styleUrl: './media-list-page.css',
})
export class MediaListPage {
  items: MediaCardDto[] = [];
  page = 1;
  totalPages = 1;
  loading = false;

  title = '';

  constructor(private route: ActivatedRoute, private api: KinoApiService) {
    combineLatest([this.route.data, this.route.paramMap]).subscribe(() => {
      this.page = 1;
      this.load();
    })
  }

  private load(){
    const media = this.route.snapshot.url[0]?.path;
    const category = this.route.snapshot.paramMap.get('category') || '';

    this.title = this.makeTitle(media, category);

    this.loading = true;

    const req =
      media === 'movies' ? this.fetchMovies(category as MoviesCategory, this.page)
      : this.fetchTv(category as TvCategory, this.page);

    console.log('SUBSCRIBE', req)
    req.subscribe({
      next: (res) => {
        this.items = res.items;
        this.page = res.page;
        this.totalPages = res.totalPages;
        this.loading = false;
      },
      error: () => {
        this.loading = false;
      },
    });
  }

  private fetchMovies(category: MoviesCategory, page: number){
    switch (category){
      case 'popular': return this.api.popularMovies(page);
      case 'upcoming': return this.api.upcomingMovies(page);
      case 'top-rated': return this.api.topRatedMovies(page);
    }
  }

  private fetchTv(category: TvCategory, page: number){
    switch (category){
      case 'popular': return this.api.popularTv(page);
      case 'on-the-air': return this.api.onTheAirTv(page);
      case 'top-rated': return this.api.topRatedTv(page);
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
