import { ComponentFixture, TestBed } from '@angular/core/testing';

import { MediaListPage } from './media-list-page';

describe('MediaListPage', () => {
  let component: MediaListPage;
  let fixture: ComponentFixture<MediaListPage>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [MediaListPage]
    })
    .compileComponents();

    fixture = TestBed.createComponent(MediaListPage);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
