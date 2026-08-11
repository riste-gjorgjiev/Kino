import { Component, Input, ViewChild, ElementRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MediaCardDto } from '../../core/models/media-card.dto';
import { MediaCardComponent } from '../media-card/media-card.component';

@Component({
  selector: 'app-media-carousel',
  standalone: true,
  imports: [CommonModule, MediaCardComponent],
  templateUrl: './media-carousel.component.html',
  styleUrl: './media-carousel.component.css'
})
export class MediaCarouselComponent {
  @Input({ required: true }) title!: string;
  @Input({ required: true }) items: MediaCardDto[] = [];
  @ViewChild('carouselContainer') carouselContainer!: ElementRef<HTMLDivElement>;

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
}
