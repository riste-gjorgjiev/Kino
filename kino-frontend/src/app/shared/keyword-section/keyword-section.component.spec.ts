import { ComponentFixture, TestBed } from '@angular/core/testing';
import { KeywordSectionComponent } from './keyword-section.component';

describe('KeywordSectionComponent', () => {
  let component: KeywordSectionComponent;
  let fixture: ComponentFixture<KeywordSectionComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [KeywordSectionComponent]
    }).compileComponents();

    fixture = TestBed.createComponent(KeywordSectionComponent);
    component = fixture.componentInstance;
    component.keywords = [
      { id: 1, name: 'superhero' },
      { id: 2, name: 'action' }
    ];
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should not render when keywords is empty', () => {
    fixture.componentRef.setInput('keywords', []);
    fixture.detectChanges();
    const section = fixture.nativeElement.querySelector('.keyword-section');
    expect(section).toBeFalsy();
  });

  it('should render chips for each keyword', () => {
    const chips = fixture.nativeElement.querySelectorAll('.keyword-chip');
    expect(chips.length).toBe(2);
    expect(chips[0].textContent).toContain('superhero');
    expect(chips[1].textContent).toContain('action');
  });
});
