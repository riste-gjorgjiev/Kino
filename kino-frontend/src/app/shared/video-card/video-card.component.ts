import { Component, EventEmitter, Input, Output } from '@angular/core';
import { CommonModule } from '@angular/common';
import { VideoDto } from '../../core/models/video-dto';

@Component({
  selector: 'app-video-card',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './video-card.component.html',
  styleUrl: './video-card.component.css'
})
export class VideoCardComponent {
  @Input({ required: true }) video!: VideoDto;
  @Output() playVideo = new EventEmitter<string>();

  get thumbnailUrl(): string {
    return `https://img.youtube.com/vi/${this.video.key}/hqdefault.jpg`;
  }

  onClick() {
    this.playVideo.emit(this.video.key);
  }
}
