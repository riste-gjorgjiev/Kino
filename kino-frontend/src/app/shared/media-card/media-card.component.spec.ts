import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';
import { MediaCardComponent } from './media-card.component';
import { MediaCardDto } from '../../core/models/media-card.dto';

describe('MediaCardComponent', () => {
  let component: MediaCardComponent;
  let fixture: ComponentFixture<MediaCardComponent>;

  const movie: MediaCardDto = {
    id: 42,
    mediaType: 'MOVIE',
    title: 'The Movie',
    posterUrl: '/poster.jpg',
    rating: 7.25,
    date: '2024-05-01'
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [MediaCardComponent],
      providers: [provideRouter([])]
    }).compileComponents();

    fixture = TestBed.createComponent(MediaCardComponent);
    component = fixture.componentInstance;
    fixture.componentRef.setInput('item', movie);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should render title, poster, rating and date', () => {
    const img = fixture.nativeElement.querySelector('img');
    const title = fixture.nativeElement.querySelector('.title');
    const sub = fixture.nativeElement.querySelector('.sub');

    expect(img.getAttribute('src')).toBe('/poster.jpg');
    expect(img.getAttribute('alt')).toBe('The Movie');
    expect(title.textContent.trim()).toBe('The Movie');
    expect(sub.textContent).toContain('7.3');
    expect(sub.textContent).toContain('2024-05-01');
  });

  it('should link movies to the movie details route', () => {
    expect(component.getDetailLink()).toEqual(['/movies/details', 42]);
  });

  it('should link TV shows to the TV details route', () => {
    fixture.componentRef.setInput('item', { ...movie, mediaType: 'TV' });
    expect(component.getDetailLink()).toEqual(['/tv/details', 42]);
  });

  it('should fall back when optional fields are missing', () => {
    fixture.componentRef.setInput('item', {
      ...movie,
      title: '',
      date: null,
      rating: null,
      posterUrl: null
    });
    fixture.detectChanges();

    expect(component.getDisplayTitle()).toBe('Unknown Title');
    expect(component.getDisplayDate()).toBe('—');
    expect(component.getDisplayRating()).toBe('N/A');

    const img = fixture.nativeElement.querySelector('img');
    expect(img.getAttribute('src')).toBe('assets/no-poster.png');
  });
});
