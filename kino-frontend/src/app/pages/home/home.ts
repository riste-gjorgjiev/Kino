import { Component } from '@angular/core';
import {MediaCardDto} from '../../core/models/media-card.dto';
import {ActivatedRoute, RouterLink} from '@angular/router';
import {KinoApiService} from '../../kino-api.service';
import {CommonModule} from '@angular/common';
import {forkJoin} from 'rxjs';

@Component({
  selector: 'app-home',
  imports: [CommonModule, RouterLink],
  templateUrl: './home.html',
  styleUrl: './home.css',
})
export class Home {
  movies: MediaCardDto[] = []
  tv: MediaCardDto[] = []

  loading = true;
  error = '';

  constructor(private api: KinoApiService) {
    this.load()
  }

  load(){
    this.loading = true;
    this.error = '';

    forkJoin({
      movies: this.api.trendingMovies('day', 1),
      tv: this.api.trendingTv('day', 1),
    }).subscribe({
      next: ({movies, tv}) => {
        this.movies = (movies.items ?? []).slice(0, 20)
        this.tv = (tv.items ?? []).slice(0, 20)
        this.loading = false;
      },
      error: () => {
        this.error = 'Failed to load trending content.'
        this.loading = false;
      },
    });
  }
}
