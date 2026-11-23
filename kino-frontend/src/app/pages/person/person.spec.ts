import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';
import { of, throwError } from 'rxjs';
import { Person } from './person';
import { KinoApiService } from '../../kino-api.service';

const mockPerson = {
  id: 1000,
  name: 'Test Actor',
  biography: 'A test biography',
  profileUrl: 'https://image.tmdb.org/t/p/w500/actor.jpg',
  birthday: '1980-05-15',
  deathday: null,
  placeOfBirth: 'Los Angeles, USA',
  alsoKnownAs: ['Test Actor Jr.'],
  knownForDepartment: 'Acting',
  credits: [
    {
      id: 100,
      mediaType: 'MOVIE' as const,
      title: 'Test Movie',
      posterUrl: 'https://image.tmdb.org/t/p/w500/movie.jpg',
      rating: 7.5,
      date: '2024-01-15',
      character: 'Test Character',
    },
  ],
};

describe('Person', () => {
  let component: Person;
  let fixture: ComponentFixture<Person>;
  let apiService: { personDetails: ReturnType<typeof vi.fn> };

  beforeEach(async () => {
    apiService = { personDetails: vi.fn() };

    await TestBed.configureTestingModule({
      imports: [Person],
      providers: [
        { provide: KinoApiService, useValue: apiService },
        {
          provide: ActivatedRoute,
          useValue: {
            snapshot: { paramMap: { get: () => '1000' } },
          },
        },
        {
          provide: Router,
          useValue: {
            events: of(),
            createUrlTree: vi.fn(),
            serializeUrl: vi.fn(),
          },
        },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(Person);
    component = fixture.componentInstance;
  });

  it('should create', () => {
    apiService.personDetails.mockReturnValue(of(mockPerson));
    fixture.detectChanges();
    expect(component).toBeTruthy();
  });

  it('should load person details on init', () => {
    apiService.personDetails.mockReturnValue(of(mockPerson));
    fixture.detectChanges();

    expect(apiService.personDetails).toHaveBeenCalledWith(1000);
    expect(component.data).toEqual(mockPerson);
    expect(component.loading).toBeFalsy();
  });

  it('should handle error when loading fails', () => {
    apiService.personDetails.mockReturnValue(throwError(() => new Error('API error')));
    fixture.detectChanges();

    expect(component.error).toBe('Failed to load person details');
    expect(component.loading).toBeFalsy();
  });

  it('should calculate age correctly', () => {
    apiService.personDetails.mockReturnValue(of(mockPerson));
    fixture.detectChanges();

    const age = component.calculateAge();
    expect(age).toBeGreaterThanOrEqual(44);
  });

  it('should return null age when birthday is missing', () => {
    const personNoBirthday = { ...mockPerson, birthday: null };
    apiService.personDetails.mockReturnValue(of(personNoBirthday));
    fixture.detectChanges();

    expect(component.calculateAge()).toBeNull();
  });

  it('should format date correctly', () => {
    const formatted = component.formatDate('2024-01-15');
    expect(formatted).toContain('2024');
  });

  it('should return empty string for null date', () => {
    expect(component.formatDate(null)).toBe('');
  });

  it('should get credit year from date', () => {
    expect(component.getCreditYear('2024-01-15')).toBe('2024');
  });

  it('should return empty string for null credit date', () => {
    expect(component.getCreditYear(null)).toBe('');
  });

  it('should get correct link for movie credit', () => {
    const credit = { mediaType: 'MOVIE', id: 100 };
    expect(component.getCreditLink(credit)).toEqual(['/movies/details', 100]);
  });

  it('should get correct link for TV credit', () => {
    const credit = { mediaType: 'TV', id: 200 };
    expect(component.getCreditLink(credit)).toEqual(['/tv/details', 200]);
  });
});
