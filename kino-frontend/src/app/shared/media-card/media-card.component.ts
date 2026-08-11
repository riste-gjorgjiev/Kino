import { Component, Input } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { MediaCardDto } from '../../core/models/media-card.dto';

@Component({
  selector: 'app-media-card',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './media-card.component.html',
  styleUrl: './media-card.component.css'
})
export class MediaCardComponent {
  @Input({ required: true }) item!: MediaCardDto;

  getDetailLink(): any[] {
    if (this.item.mediaType === 'MOVIE') {
      return ['/movies/details', this.item.id];
    } else {
      return ['/tv/details', this.item.id];
    }
  }

  getDisplayTitle(): string {
    return this.item.title || 'Unknown Title';
  }

  getDisplayDate(): string {
    return this.item.date || '—';
  }

  getDisplayRating(): string {
    return this.item.rating?.toFixed(1) || 'N/A';
  }
}
