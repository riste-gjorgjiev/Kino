import {Component, OnDestroy, OnInit} from '@angular/core';
import {MediaDetailsDto} from '../../core/models/media-details-dto';
import {ActivatedRoute, NavigationEnd, Router} from '@angular/router';
import {KinoApiService} from '../../kino-api.service';
import {filter, Subscription} from 'rxjs';
import {CommonModule} from '@angular/common';

@Component({
  selector: 'app-details',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './details.html',
  styleUrl: './details.css',
})
export class Details implements OnInit, OnDestroy{
  data?: MediaDetailsDto;
  loading = true;
  error = '';

  private sub = new Subscription();

  constructor(private route: ActivatedRoute, private router: Router, private api: KinoApiService) {}

  ngOnInit() {
    console.log('DETAILS INIT', this.router.url);
    this.sub.add(this.router.events
      .pipe(filter(e => e instanceof NavigationEnd))
      .subscribe(() => this.load())
    );
    this.load()
  }

  ngOnDestroy() {
    this.sub.unsubscribe();
  }

  private load() {
    const id = Number(this.route.snapshot.paramMap.get('id'))
    const isMovie = this.router.url.startsWith('/movies');

    if (!id){
      this.error = 'Invalid ID'
      return;
    }
    this.loading = true;
    this.error = '';
    this.data = undefined;

    const req = isMovie ? this.api.detailsMovie(id) : this.api.detailsTv(id);

    this.sub.add(
      req.subscribe({
        next: res => {
          this.data = res;
          this.loading = false;
        },
        error: err => {
          console.error(err)
          this.error = 'Failed to load details'
          this.loading = false;
        },
      })
    );
  }
}


