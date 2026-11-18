export interface EpisodeDto {
  id: number | null;
  seasonNumber: number | null;
  episodeNumber: number;
  name: string | null;
  overview: string | null;
  stillUrl: string | null;
  airDate: string | null;
  runtimeMinutes: number | null;
  rating: number | null;
  voteCount: number | null;
}
