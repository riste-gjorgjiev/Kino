import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, provideRouter } from '@angular/router';
import { of } from 'rxjs';

import { MediaListPage } from './media-list-page';

describe('MediaListPage', () => {
  let component: MediaListPage;
  let fixture: ComponentFixture<MediaListPage>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [MediaListPage],
      providers: [
        provideRouter([]),
        { provide: ActivatedRoute, useValue: { snapshot: { paramMap: { get: () => 'popular' }, queryParamMap: { has: () => false, get: () => null }, url: [{ path: 'movies' }] }, params: of({}), queryParams: of({}), data: of({}), paramMap: of({}), queryParamMap: of({}) } }
      ]
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
