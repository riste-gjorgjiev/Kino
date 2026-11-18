import { ComponentFixture, TestBed } from '@angular/core/testing';
import { Observable, of, throwError } from 'rxjs';
import { SeasonEpisodeGuideComponent } from './season-episode-guide.component';
import { KinoApiService } from '../../kino-api.service';
import { SeasonDto } from '../../core/models/season-dto';
import { SeasonDetailsDto } from '../../core/models/season-details-dto';

const seasons: SeasonDto[] = [
  { id: 10, seasonNumber: 0, name: 'Specials', overview: null, posterUrl: null, airDate: null, episodeCount: 1 },
  { id: 11, seasonNumber: 1, name: 'Season 1', overview: 'First season', posterUrl: '/s1.jpg', airDate: '2024-01-01', episodeCount: 2 },
  { id: 12, seasonNumber: 2, name: 'Season 2', overview: null, posterUrl: '/s2.jpg', airDate: '2025-01-01', episodeCount: 2 }
];

function seasonDetails(seasonNumber: number): SeasonDetailsDto {
  return {
    id: 10 + seasonNumber,
    seasonNumber,
    name: `Season ${seasonNumber}`,
    overview: seasonNumber === 1 ? 'First season' : null,
    posterUrl: `/s${seasonNumber}.jpg`,
    airDate: '2024-01-01',
    episodeCount: 2,
    episodes: [
      {
        id: 101,
        seasonNumber,
        episodeNumber: 1,
        name: `Season ${seasonNumber} Pilot`,
        overview: 'Pilot overview',
        stillUrl: '/pilot.jpg',
        airDate: '2024-01-01',
        runtimeMinutes: 45,
        rating: 8.25,
        voteCount: 10
      }
    ]
  };
}

describe('SeasonEpisodeGuideComponent', () => {
  let component: SeasonEpisodeGuideComponent;
  let fixture: ComponentFixture<SeasonEpisodeGuideComponent>;
  let api: {
    tvSeasons: ReturnType<typeof vi.fn>;
    tvSeasonDetails: ReturnType<typeof vi.fn>;
  };

  async function setup(
    seasonsResponse: Observable<SeasonDto[]> = of(seasons),
    detailsResponse: (seasonNumber: number) => Observable<SeasonDetailsDto> = seasonNumber =>
      of(seasonDetails(seasonNumber))
  ): Promise<void> {
    api = {
      tvSeasons: vi.fn(() => seasonsResponse),
      tvSeasonDetails: vi.fn((_id: number, seasonNumber: number) => detailsResponse(seasonNumber))
    };

    await TestBed.configureTestingModule({
      imports: [SeasonEpisodeGuideComponent],
      providers: [{ provide: KinoApiService, useValue: api }]
    }).compileComponents();

    fixture = TestBed.createComponent(SeasonEpisodeGuideComponent);
    component = fixture.componentInstance;
    fixture.componentRef.setInput('tvId', 1399);
    fixture.detectChanges();
    await fixture.whenStable();
    fixture.detectChanges();
  }

  it('should load seasons and select the first non-special season', async () => {
    await setup();

    expect(api.tvSeasons).toHaveBeenCalledWith(1399);
    expect(component.selectedSeasonNumber).toBe(1);
    expect(api.tvSeasonDetails).toHaveBeenCalledWith(1399, 1);

    const options = fixture.nativeElement.querySelectorAll('#season-select option');
    expect(options.length).toBe(3);
    expect(options[1].textContent).toContain('Season 1');
  });

  it('should render the selected season episodes', async () => {
    await setup();

    const episodes = fixture.nativeElement.querySelectorAll('.episode');
    expect(episodes.length).toBe(1);
    expect(episodes[0].textContent).toContain('S01E01');
    expect(episodes[0].textContent).toContain('Season 1 Pilot');
    expect(episodes[0].textContent).toContain('45 min');
    expect(episodes[0].textContent).toContain('8.3');
  });

  it('should load another season when the selector changes', async () => {
    await setup();

    const select = fixture.nativeElement.querySelector('#season-select') as HTMLSelectElement;
    select.value = '2';
    select.dispatchEvent(new Event('change'));
    await fixture.whenStable();
    fixture.detectChanges();

    expect(api.tvSeasonDetails).toHaveBeenCalledWith(1399, 2);
    expect(component.selectedSeasonNumber).toBe(2);
    expect(fixture.nativeElement.querySelector('.episode').textContent).toContain('Season 2 Pilot');
  });

  it('should show an error state with retry when seasons fail to load', async () => {
    await setup(throwError(() => new Error('nope')));

    const error = fixture.nativeElement.querySelector('.state.error');
    expect(error).toBeTruthy();
    expect(error.textContent).toContain('Failed to load seasons.');
    expect(error.querySelector('.retry-btn')).toBeTruthy();
  });

  it('should show an empty state when there are no seasons', async () => {
    await setup(of([]));

    const state = fixture.nativeElement.querySelector('.state');
    expect(state.textContent).toContain('No seasons available.');
  });
});
