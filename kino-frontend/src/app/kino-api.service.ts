import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import {PagedResponseDto} from './core/models/paged-response.dto';
import {MediaCardDto} from './core/models/media-card.dto';
import {MediaDetailsDto} from './core/models/media-details-dto';

export type WindowParam = 'day' | 'week';

@Injectable({providedIn: 'root'})
export class KinoApiService {
  private readonly baseUrl = 'http://localhost:8080/api';

  constructor(private http: HttpClient) {}

  popularMovies(page: number = 1): Observable<PagedResponseDto<MediaCardDto>>{
    const params = new HttpParams().set('page', String(page));
    return this.http.get<PagedResponseDto<MediaCardDto>>(`${this.baseUrl}/movies/popular`, { params });
  }
  upcomingMovies(page: number = 1): Observable<PagedResponseDto<MediaCardDto>>{
    const params = new HttpParams().set('page', String(page));
    return this.http.get<PagedResponseDto<MediaCardDto>>(`${this.baseUrl}/movies/upcoming`, { params });
  }
  topRatedMovies(page: number = 1): Observable<PagedResponseDto<MediaCardDto>>{
    const params = new HttpParams().set('page', String(page));
    return this.http.get<PagedResponseDto<MediaCardDto>>(`${this.baseUrl}/movies/top-rated`, { params });
  }

  popularTv(page: number = 1): Observable<PagedResponseDto<MediaCardDto>>{
    const params = new HttpParams().set('page', String(page));
    return this.http.get<PagedResponseDto<MediaCardDto>>(`${this.baseUrl}/tv/popular`, { params });
  }
  topRatedTv(page: number = 1): Observable<PagedResponseDto<MediaCardDto>>{
    const params = new HttpParams().set('page', String(page));
    return this.http.get<PagedResponseDto<MediaCardDto>>(`${this.baseUrl}/tv/top-rated`, { params });
  }
  onTheAirTv(page: number = 1): Observable<PagedResponseDto<MediaCardDto>>{
    const params = new HttpParams().set('page', String(page));
    return this.http.get<PagedResponseDto<MediaCardDto>>(`${this.baseUrl}/tv/on-the-air`, { params });
  }

  trendingMovies(window: 'day' | 'week' = 'day', page: number = 1){
    return this.http.get<PagedResponseDto<MediaCardDto>>(`${this.baseUrl}/movies/trending`, {params: {window, page: String(page)}});
  }
  trendingTv(window: 'day' | 'week' = 'day', page: number = 1){
    return this.http.get<PagedResponseDto<MediaCardDto>>(`${this.baseUrl}/tv/trending`, {params: {window, page: String(page)}});
  }

  detailsMovie(id: number){
    return this.http.get<MediaDetailsDto>(`${this.baseUrl}/movies/${id}`);
  }
  detailsTv(id: number){
    return this.http.get<MediaDetailsDto>(`${this.baseUrl}/tv/${id}`);
  }

  searchMovies(query: string, page = 1){
    const params = new HttpParams().set('page', String(page));
    return this.http.get<PagedResponseDto<MediaCardDto>>(`${this.baseUrl}/search/movie`, { params: {query, page: String(page), }, });
  }
  searchTv(query: string, page = 1){
    const params = new HttpParams().set('page', String(page));
    return this.http.get<PagedResponseDto<MediaCardDto>>(`${this.baseUrl}/search/tv`, { params: {query, page: String(page), }, });
  }
  searchAll(query: string, page = 1){
    const params = new HttpParams().set('page', String(page));
    return this.http.get<PagedResponseDto<MediaCardDto>>(`${this.baseUrl}/search/all`, { params: {query, page: String(page), }, });
  }
}
