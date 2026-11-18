import { Component, DestroyRef, Input, OnInit, inject } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { KinoApiService } from '../../kino-api.service';
import { SeasonDto } from '../../core/models/season-dto';
import { SeasonDetailsDto } from '../../core/models/season-details-dto';
import { EpisodeDto } from '../../core/models/episode-dto';

@Component({
  selector: 'app-season-episode-guide',
  standalone: true,
  templateUrl: './season-episode-guide.component.html',
  styleUrl: './season-episode-guide.component.css'
})
export class SeasonEpisodeGuideComponent implements OnInit {
  @Input({ required: true }) tvId!: number;

  seasons: SeasonDto[] = [];
  selectedSeasonNumber: number | null = null;
  season: SeasonDetailsDto | null = null;

  loadingSeasons = true;
  loadingSeason = false;
  error = '';

  private readonly destroyRef = inject(DestroyRef);

  constructor(private api: KinoApiService) {}

  ngOnInit(): void {
    this.loadSeasons();
  }

  loadSeasons(): void {
    this.loadingSeasons = true;
    this.error = '';

    this.api.tvSeasons(this.tvId)
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe({
        next: seasons => {
          this.seasons = seasons ?? [];
          this.loadingSeasons = false;

          if (this.seasons.length === 0) {
            this.selectedSeasonNumber = null;
            this.season = null;
            return;
          }

          const preferred =
            this.seasons.find(season => season.seasonNumber > 0) ?? this.seasons[0];
          this.selectSeason(preferred.seasonNumber);
        },
        error: err => {
          console.error(err);
          this.error = 'Failed to load seasons.';
          this.loadingSeasons = false;
        }
      });
  }

  onSeasonChange(event: Event): void {
    const value = Number((event.target as HTMLSelectElement).value);
    this.selectSeason(value);
  }

  selectSeason(seasonNumber: number): void {
    if (this.selectedSeasonNumber === seasonNumber && this.season) {
      return;
    }
    this.selectedSeasonNumber = seasonNumber;
    this.loadSeason(seasonNumber);
  }

  loadSeason(seasonNumber: number): void {
    this.loadingSeason = true;
    this.error = '';

    this.api.tvSeasonDetails(this.tvId, seasonNumber)
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe({
        next: season => {
          if (this.selectedSeasonNumber !== seasonNumber) {
            return;
          }
          this.season = season;
          this.loadingSeason = false;
        },
        error: err => {
          console.error(err);
          if (this.selectedSeasonNumber !== seasonNumber) {
            return;
          }
          this.error = 'Failed to load episodes.';
          this.loadingSeason = false;
        }
      });
  }

  retry(): void {
    if (this.seasons.length === 0) {
      this.loadSeasons();
      return;
    }
    if (this.selectedSeasonNumber != null) {
      this.loadSeason(this.selectedSeasonNumber);
    }
  }

  seasonLabel(season: SeasonDto): string {
    if (season.seasonNumber === 0) {
      return season.name ? `Specials · ${season.name}` : 'Specials';
    }
    const base = `Season ${season.seasonNumber}`;
    if (season.episodeCount != null) {
      return `${base} · ${season.episodeCount} episodes`;
    }
    return base;
  }

  episodeCode(episode: EpisodeDto): string {
    const seasonNumber = (episode.seasonNumber ?? this.selectedSeasonNumber ?? 0)
      .toString()
      .padStart(2, '0');
    const episodeNumber = episode.episodeNumber.toString().padStart(2, '0');
    return `S${seasonNumber}E${episodeNumber}`;
  }

  episodeRating(rating: number | null): string {
    return rating == null ? '' : rating.toFixed(1);
  }
}
