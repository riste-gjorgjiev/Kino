import { ComponentFixture, TestBed } from '@angular/core/testing';
import { VideoSectionComponent } from './video-section.component';
import { VideoDto } from '../../core/models/video-dto';

describe('VideoSectionComponent', () => {
  let component: VideoSectionComponent;
  let fixture: ComponentFixture<VideoSectionComponent>;

  const mockVideos: VideoDto[] = [
    {
      id: '1',
      key: 'abc123',
      name: 'Official Trailer',
      site: 'YouTube',
      type: 'Trailer',
      official: true,
      publishedAt: '2024-01-01'
    },
    {
      id: '2',
      key: 'def456',
      name: 'Teaser',
      site: 'YouTube',
      type: 'Teaser',
      official: false,
      publishedAt: '2024-01-02'
    }
  ];

  const threeMockVideos: VideoDto[] = [
    ...mockVideos,
    {
      id: '3',
      key: 'ghi789',
      name: 'Behind the Scenes',
      site: 'YouTube',
      type: 'Featurette',
      official: true,
      publishedAt: '2024-01-03'
    }
  ];

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [VideoSectionComponent]
    }).compileComponents();

    fixture = TestBed.createComponent(VideoSectionComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should not render when videos is empty', () => {
    fixture.componentRef.setInput('videos', []);
    fixture.detectChanges();
    const section = fixture.nativeElement.querySelector('.video-section');
    expect(section).toBeFalsy();
  });

  it('should render all videos in the list', () => {
    fixture.componentRef.setInput('videos', threeMockVideos);
    fixture.detectChanges();
    const cards = fixture.nativeElement.querySelectorAll('app-video-card');
    expect(cards.length).toBe(3);
  });

  it('should emit playVideo when a card is clicked', () => {
    const spy = vi.spyOn(component.playVideo, 'emit');
    component.onPlayVideo('abc123');
    expect(spy).toHaveBeenCalledWith('abc123');
  });

  it('should have horizontal scroll enabled on the gallery container', () => {
    fixture.componentRef.setInput('videos', mockVideos);
    fixture.detectChanges();
    const gallery = fixture.nativeElement.querySelector('.video-gallery');
    expect(gallery).toBeTruthy();
    const style = getComputedStyle(gallery);
    expect(style.overflowX).toBe('auto');
  });
});
