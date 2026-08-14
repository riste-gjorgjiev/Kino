import { Component, EventEmitter, HostListener, Input, Output } from '@angular/core';
import { CommonModule } from '@angular/common';
import { DomSanitizer, SafeResourceUrl } from '@angular/platform-browser';

@Component({
  selector: 'app-trailer-modal',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './trailer-modal.component.html',
  styleUrl: './trailer-modal.component.css'
})
export class TrailerModalComponent {
  @Input({ required: true }) videoKey: string | null = null;
  @Output() close = new EventEmitter<void>();

  constructor(private sanitizer: DomSanitizer) {}

  get iframeSrc(): SafeResourceUrl | null {
    if (!this.videoKey) return null;
    return this.sanitizer.bypassSecurityTrustResourceUrl(
      `https://www.youtube-nocookie.com/embed/${this.videoKey}?autoplay=1`
    );
  }

  @HostListener('document:keydown.escape')
  onEscape() {
    this.close.emit();
  }

  onBackdropClick(event: MouseEvent) {
    if ((event.target as HTMLElement).classList.contains('modal-backdrop')) {
      this.close.emit();
    }
  }
}
