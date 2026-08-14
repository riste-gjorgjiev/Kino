import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { Details } from './details';
import { KinoApiService } from '../../kino-api.service';
import { By } from '@angular/platform-browser';

const fakeDetails = {
  id: 1,
  mediaType: 'MOVIE' as const,
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
    { id: 2, mediaType: 'MOVIE' as const, title: 'Rec', posterUrl: '/p.jpg', rating: 7, date: '2024-01-01' }
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
    })
    .compileComponents();

    fixture = TestBed.createComponent(Details);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should render keywords when present', () => {
    component.data = fakeDetails;
    fixture.detectChanges();
    const chip = fixture.debugElement.query(By.css('.keyword-chip'));
    expect(chip).toBeTruthy();
    expect(chip.nativeElement.textContent).toContain('test-keyword');
  });

  it('should render recommendations carousel when present', () => {
    component.data = fakeDetails;
    fixture.detectChanges();
    const carousel = fixture.debugElement.query(By.css('app-media-carousel'));
    expect(carousel).toBeTruthy();
  });
});
