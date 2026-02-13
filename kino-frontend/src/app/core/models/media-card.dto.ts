export type MediaType = 'MOVIE' | 'TV';

export interface MediaCardDto {
  id: number;
  mediaType: MediaType;
  title: string;
  posterUrl: string;
  rating: number;
  date: string;
}
