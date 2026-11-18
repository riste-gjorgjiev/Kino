import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';
import { MediaCarouselComponent } from './media-carousel.component';
import { MediaCardDto } from '../../core/models/media-card.dto';

describe('MediaCarouselComponent', () => {
  let component: MediaCarouselComponent;
  let fixture: ComponentFixture<MediaCarouselComponent>;

  const items: MediaCardDto[] = [
    { id: 1, mediaType: 'MOVIE', title: 'One', posterUrl: '/1.jpg', rating: 7, date: '2024-01-01' },
    { id: 2, mediaType: 'TV', title: 'Two', posterUrl: '/2.jpg', rating: 8, date: '2024-02-01' }
  ];

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [MediaCarouselComponent],
      providers: [provideRouter([])]
    }).compileComponents();

    fixture = TestBed.createComponent(MediaCarouselComponent);
    component = fixture.componentInstance;
    fixture.componentRef.setInput('title', 'More Like This');
    fixture.componentRef.setInput('items', items);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should render the title and one card per item', () => {
    const heading = fixture.nativeElement.querySelector('.section-head h2');
    const cards = fixture.nativeElement.querySelectorAll('app-media-card');

    expect(heading.textContent.trim()).toBe('More Like This');
    expect(cards.length).toBe(2);
  });

  it('should scroll left when the previous button is clicked', () => {
    const container = fixture.nativeElement.querySelector('.carousel-container') as HTMLDivElement;
    const scrollBy = vi.fn();
    container.scrollBy = scrollBy;

    fixture.nativeElement.querySelector('.prev-btn').click();

    expect(scrollBy).toHaveBeenCalledWith({ left: -300, behavior: 'smooth' });
  });

  it('should scroll right when the next button is clicked', () => {
    const container = fixture.nativeElement.querySelector('.carousel-container') as HTMLDivElement;
    const scrollBy = vi.fn();
    container.scrollBy = scrollBy;

    fixture.nativeElement.querySelector('.next-btn').click();

    expect(scrollBy).toHaveBeenCalledWith({ left: 300, behavior: 'smooth' });
  });
});
