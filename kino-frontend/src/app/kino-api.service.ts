import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import {PagedResponseDto} from './core/models/paged-response.dto';
import {MediaCardDto} from './core/models/media-card.dto';
import {MediaDetailsDto} from './core/models/media-details-dto';
import {FilterState} from './core/models/filter-state.model';

export type WindowParam = 'day' | 'week';

@Injectable({providedIn: 'root'})
export class KinoApiService {
  private readonly baseUrl = '/api';

  constructor(private http: HttpClient) {}

  private addFilterParams(params: HttpParams, filter?: FilterState): HttpParams{
    if (!filter) return params;
    if (filter.yearFrom != null) params = params.set('yearFrom', String(filter.yearFrom));
    if (filter.yearTo != null) params = params.set('yearTo', String(filter.yearTo));
    if (filter.sortBy) params = params.set('sortBy', String(filter.sortBy));
    if (filter.sortOrder) params = params.set('sortOrder', String(filter.sortOrder));
    return params;
  }

  popularMovies(page: number = 1, filter?: FilterState): Observable<PagedResponseDto<MediaCardDto>> {
    let params = new HttpParams().set('page', String(page));
    params = this.addFilterParams(params, filter);
    return this.http.get<PagedResponseDto<MediaCardDto>>(`${this.baseUrl}/movies/popular`, { params });
  }

  upcomingMovies(page: number = 1, filter?: FilterState): Observable<PagedResponseDto<MediaCardDto>> {
    let params = new HttpParams().set('page', String(page));
    params = this.addFilterParams(params, filter);
    return this.http.get<PagedResponseDto<MediaCardDto>>(`${this.baseUrl}/movies/upcoming`, { params });
  }

  topRatedMovies(page: number = 1, filter?: FilterState): Observable<PagedResponseDto<MediaCardDto>> {
    let params = new HttpParams().set('page', String(page));
    params = this.addFilterParams(params, filter);
    return this.http.get<PagedResponseDto<MediaCardDto>>(`${this.baseUrl}/movies/top-rated`, { params });
  }

  popularTv(page: number = 1, filter?: FilterState): Observable<PagedResponseDto<MediaCardDto>> {
    let params = new HttpParams().set('page', String(page));
    params = this.addFilterParams(params, filter);
    return this.http.get<PagedResponseDto<MediaCardDto>>(`${this.baseUrl}/tv/popular`, { params });
  }

  topRatedTv(page: number = 1, filter?: FilterState): Observable<PagedResponseDto<MediaCardDto>> {
    let params = new HttpParams().set('page', String(page));
    params = this.addFilterParams(params, filter);
    return this.http.get<PagedResponseDto<MediaCardDto>>(`${this.baseUrl}/tv/top-rated`, { params });
  }

  onTheAirTv(page: number = 1, filter?: FilterState): Observable<PagedResponseDto<MediaCardDto>> {
    let params = new HttpParams().set('page', String(page));
    params = this.addFilterParams(params, filter);
    return this.http.get<PagedResponseDto<MediaCardDto>>(`${this.baseUrl}/tv/on-the-air`, { params });
  }

  trendingMovies(window: 'day' | 'week' = 'day', page: number = 1, filter?: FilterState) {
    let params = new HttpParams().set('window', window).set('page', String(page));
    params = this.addFilterParams(params, filter);
    return this.http.get<PagedResponseDto<MediaCardDto>>(`${this.baseUrl}/movies/trending`, { params });
  }

  trendingTv(window: 'day' | 'week' = 'day', page: number = 1, filter?: FilterState) {
    let params = new HttpParams().set('window', window).set('page', String(page));
    params = this.addFilterParams(params, filter);
    return this.http.get<PagedResponseDto<MediaCardDto>>(`${this.baseUrl}/tv/trending`, { params });
  }

  detailsMovie(id: number) {
    return this.http.get<MediaDetailsDto>(`${this.baseUrl}/movies/${id}`);
  }

  detailsTv(id: number) {
    return this.http.get<MediaDetailsDto>(`${this.baseUrl}/tv/${id}`);
  }

  searchMovies(query: string, page = 1, filter?: FilterState) {
    let params = new HttpParams().set('query', query).set('page', String(page));
    params = this.addFilterParams(params, filter);
    return this.http.get<PagedResponseDto<MediaCardDto>>(`${this.baseUrl}/search/movie`, { params });
  }

  searchTv(query: string, page = 1, filter?: FilterState) {
    let params = new HttpParams().set('query', query).set('page', String(page));
    params = this.addFilterParams(params, filter);
    return this.http.get<PagedResponseDto<MediaCardDto>>(`${this.baseUrl}/search/tv`, { params });
  }

  searchAll(query: string, page = 1, filter?: FilterState) {
    let params = new HttpParams().set('query', query).set('page', String(page));
    params = this.addFilterParams(params, filter);
    return this.http.get<PagedResponseDto<MediaCardDto>>(`${this.baseUrl}/search/all`, { params });
  }
}
