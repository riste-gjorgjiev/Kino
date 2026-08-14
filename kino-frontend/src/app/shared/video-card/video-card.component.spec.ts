import { ComponentFixture, TestBed } from '@angular/core/testing';
import { VideoCardComponent } from './video-card.component';
import { VideoDto } from '../../core/models/video-dto';

describe('VideoCardComponent', () => {
  let component: VideoCardComponent;
  let fixture: ComponentFixture<VideoCardComponent>;

  const mockVideo: VideoDto = {
    id: '1',
    key: 'abc123',
    name: 'Official Trailer',
    site: 'YouTube',
    type: 'Trailer',
    official: true,
    publishedAt: '2024-01-01'
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [VideoCardComponent]
    }).compileComponents();

    fixture = TestBed.createComponent(VideoCardComponent);
    component = fixture.componentInstance;
    fixture.componentRef.setInput('video', mockVideo);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should render YouTube thumbnail', () => {
    const img = fixture.nativeElement.querySelector('img');
    expect(img.src).toContain('img.youtube.com/vi/abc123/hqdefault.jpg');
  });

  it('should display video name', () => {
    const title = fixture.nativeElement.querySelector('.video-title');
    expect(title.textContent.trim()).toBe('Official Trailer');
  });

  it('should show type badge', () => {
    const badge = fixture.nativeElement.querySelector('.type-badge');
    expect(badge.textContent.trim()).toBe('Trailer');
  });

  it('should show official badge when video is official', () => {
    const officialBadge = fixture.nativeElement.querySelector('.official-badge');
    expect(officialBadge).toBeTruthy();
    expect(officialBadge.textContent.trim()).toBe('Official');
  });

  it('should not show official badge when video is not official', () => {
    fixture.componentRef.setInput('video', { ...mockVideo, official: false });
    fixture.detectChanges();
    const officialBadge = fixture.nativeElement.querySelector('.official-badge');
    expect(officialBadge).toBeFalsy();
  });

  it('should emit playVideo on click', () => {
    const spy = vi.spyOn(component.playVideo, 'emit');
    const card = fixture.nativeElement.querySelector('.video-card');
    card.click();
    expect(spy).toHaveBeenCalledWith('abc123');
  });
});
