import { Component, Input, ViewChild, ElementRef, HostListener } from '@angular/core';
import { MediaCardDto } from '../../core/models/media-card.dto';
import { MediaCardComponent } from '../media-card/media-card.component';

@Component({
  selector: 'app-media-carousel',
  standalone: true,
  imports: [MediaCardComponent],
  templateUrl: './media-carousel.component.html',
  styleUrl: './media-carousel.component.css'
})
export class MediaCarouselComponent {
  @Input({ required: true }) title!: string;
  @Input({ required: true }) items: MediaCardDto[] = [];
  @ViewChild('carouselContainer') carouselContainer!: ElementRef<HTMLDivElement>;

  private touchStartX = 0;
  private touchEndX = 0;
  private readonly swipeThreshold = 50;

  scrollLeft() {
    if (this.carouselContainer) {
      this.carouselContainer.nativeElement.scrollBy({
        left: -300,
        behavior: 'smooth'
      });
    }
  }

  scrollRight() {
    if (this.carouselContainer) {
      this.carouselContainer.nativeElement.scrollBy({
        left: 300,
        behavior: 'smooth'
      });
    }
  }

  onTouchStart(event: TouchEvent) {
    this.touchStartX = event.touches[0].clientX;
    this.touchEndX = this.touchStartX;
  }

  onTouchMove(event: TouchEvent) {
    this.touchEndX = event.touches[0].clientX;
  }

  onTouchEnd() {
    const swipeDistance = this.touchStartX - this.touchEndX;

    if (Math.abs(swipeDistance) > this.swipeThreshold) {
      if (swipeDistance > 0) {
        this.scrollRight();
      } else {
        this.scrollLeft();
      }
    }
  }

  @HostListener('keydown', ['$event'])
  onKeyDown(event: KeyboardEvent) {
    if (event.target === this.carouselContainer?.nativeElement) {
      if (event.key === 'ArrowLeft') {
        event.preventDefault();
        this.scrollLeft();
      } else if (event.key === 'ArrowRight') {
        event.preventDefault();
        this.scrollRight();
      }
    }
  }
}
