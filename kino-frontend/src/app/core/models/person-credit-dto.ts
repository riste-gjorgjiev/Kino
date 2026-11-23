export interface PersonCreditDto {
  id: number;
  mediaType: 'MOVIE' | 'TV';
  title: string | null;
  posterUrl: string | null;
  rating: number | null;
  date: string | null;
  character: string | null;
}
