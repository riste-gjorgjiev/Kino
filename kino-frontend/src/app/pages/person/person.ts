import { Component, OnDestroy, OnInit } from '@angular/core';
import { DecimalPipe } from '@angular/common';
import { ActivatedRoute, NavigationEnd, Router, RouterLink } from '@angular/router';
import { Subscription, filter } from 'rxjs';
import { KinoApiService } from '../../kino-api.service';
import { PersonDetailsDto } from '../../core/models/person-details-dto';
import { PersonCreditDto } from '../../core/models/person-credit-dto';

/** Parse a bare ISO date (YYYY-MM-DD) as a local date, not UTC. */
function parseIsoDate(dateStr: string): Date {
  const [y, m, d] = dateStr.split('-').map(Number);
  return new Date(y, (m ?? 1) - 1, d ?? 1);
}

@Component({
  selector: 'app-person',
  standalone: true,
  imports: [DecimalPipe, RouterLink],
  templateUrl: './person.html',
  styleUrl: './person.css',
})
export class Person implements OnInit, OnDestroy {
  data?: PersonDetailsDto;
  loading = true;
  error = '';

  private sub = new Subscription();

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private api: KinoApiService
  ) {}

  ngOnInit() {
    this.sub.add(
      this.router.events
        .pipe(filter(e => e instanceof NavigationEnd))
        .subscribe(() => this.load())
    );
    this.load();
  }

  ngOnDestroy() {
    this.sub.unsubscribe();
  }

  load() {
    const id = Number(this.route.snapshot.paramMap.get('id'));

    if (!id) {
      this.error = 'Invalid ID';
      this.loading = false;
      return;
    }

    this.loading = true;
    this.error = '';
    this.data = undefined;

    this.sub.add(
      this.api.personDetails(id).subscribe({
        next: res => {
          this.data = res;
          this.loading = false;
        },
        error: err => {
          console.error(err);
          this.error = 'Failed to load person details';
          this.loading = false;
        },
      })
    );
  }

  calculateAge(): number | null {
    if (!this.data?.birthday) return null;

    const birth = parseIsoDate(this.data.birthday);
    const end = this.data.deathday ? parseIsoDate(this.data.deathday) : new Date();

    let age = end.getFullYear() - birth.getFullYear();
    const monthDiff = end.getMonth() - birth.getMonth();
    if (monthDiff < 0 || (monthDiff === 0 && end.getDate() < birth.getDate())) {
      age--;
    }

    return age >= 0 ? age : null;
  }

  getAgeLabel(): string {
    return this.data?.deathday ? 'Died at' : 'Age';
  }

  formatDate(dateStr: string | null): string {
    if (!dateStr) return '';
    const date = parseIsoDate(dateStr);
    return date.toLocaleDateString('en-US', {
      year: 'numeric',
      month: 'long',
      day: 'numeric',
    });
  }

  getCreditYear(date: string | null): string {
    if (!date) return '';
    return parseIsoDate(date).getFullYear().toString();
  }

  getCreditLink(credit: PersonCreditDto): any[] {
    if (credit.mediaType === 'MOVIE') {
      return ['/movies/details', credit.id];
    }
    return ['/tv/details', credit.id];
  }
}
