import { CastDto } from './cast-dto';
import {MediaType} from './media-card.dto';
import { VideoDto } from './video-dto';

export interface MediaDetailsDto{
  id: number
  mediaType: MediaType;
  title: string;
  tagline: string | null;
  overview: string | null;

  posterUrl: string | null;
  backdropUrl: string | null;

  rating: string | null;
  date: string | null;
  genres: string[];
  runtimeMinutes: number | null;
  status: string | null;
  originalLanguage: string | null;
  creator: string | null;
  directors: string[];
  cast: CastDto[];
  videos: VideoDto[];
}
