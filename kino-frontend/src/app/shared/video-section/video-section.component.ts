import { Component, EventEmitter, Input, Output } from '@angular/core';
import { CommonModule } from '@angular/common';
import { VideoDto } from '../../core/models/video-dto';
import { VideoCardComponent } from '../video-card/video-card.component';

@Component({
  selector: 'app-video-section',
  standalone: true,
  imports: [CommonModule, VideoCardComponent],
  templateUrl: './video-section.component.html',
  styleUrl: './video-section.component.css'
})
export class VideoSectionComponent {
  @Input({ required: true }) videos: VideoDto[] = [];
  @Output() playVideo = new EventEmitter<string>();

  onPlayVideo(key: string) {
    this.playVideo.emit(key);
  }
}
