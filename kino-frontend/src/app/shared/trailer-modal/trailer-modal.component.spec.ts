import { ComponentFixture, TestBed } from '@angular/core/testing';
import { TrailerModalComponent } from './trailer-modal.component';

describe('TrailerModalComponent', () => {
  let component: TrailerModalComponent;
  let fixture: ComponentFixture<TrailerModalComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [TrailerModalComponent]
    }).compileComponents();

    fixture = TestBed.createComponent(TrailerModalComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should not render when videoKey is null', () => {
    const backdrop = fixture.nativeElement.querySelector('.modal-backdrop');
    expect(backdrop).toBeFalsy();
  });

  it('should render iframe when videoKey is set', () => {
    fixture.componentRef.setInput('videoKey', 'abc123');
    fixture.detectChanges();
    const iframe = fixture.nativeElement.querySelector('iframe');
    expect(iframe).toBeTruthy();
    expect(iframe.src).toContain('abc123');
  });

  it('should close on Escape key', () => {
    fixture.componentRef.setInput('videoKey', 'abc123');
    fixture.detectChanges();
    const spy = vi.spyOn(component.close, 'emit');
    component.onEscape();
    expect(spy).toHaveBeenCalled();
  });

  it('should close on backdrop click', () => {
    fixture.componentRef.setInput('videoKey', 'abc123');
    fixture.detectChanges();
    const spy = vi.spyOn(component.close, 'emit');
    const backdrop = fixture.nativeElement.querySelector('.modal-backdrop');
    const event = new MouseEvent('click', { bubbles: true });
    Object.defineProperty(event, 'target', { value: backdrop });
    component.onBackdropClick(event);
    expect(spy).toHaveBeenCalled();
  });

  it('should not close when clicking inside video container', () => {
    fixture.componentRef.setInput('videoKey', 'abc123');
    fixture.detectChanges();
    const spy = vi.spyOn(component.close, 'emit');
    const videoContainer = fixture.nativeElement.querySelector('.video-container');
    const event = new MouseEvent('click', { bubbles: true });
    Object.defineProperty(event, 'target', { value: videoContainer });
    component.onBackdropClick(event);
    expect(spy).not.toHaveBeenCalled();
  });
});
