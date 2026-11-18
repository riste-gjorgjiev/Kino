import { EpisodeDto } from './episode-dto';

export interface SeasonDetailsDto {
  id: number | null;
  seasonNumber: number;
  name: string | null;
  overview: string | null;
  posterUrl: string | null;
  airDate: string | null;
  episodeCount: number | null;
  episodes: EpisodeDto[];
}
