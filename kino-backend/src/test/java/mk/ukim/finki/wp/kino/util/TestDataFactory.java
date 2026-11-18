package mk.ukim.finki.wp.kino.util;

import mk.ukim.finki.wp.kino.dto.tmdb.TmdbMovieDto;
import mk.ukim.finki.wp.kino.dto.tmdb.TmdbMultiSearchDto;
import mk.ukim.finki.wp.kino.dto.tmdb.TmdbPagedResponse;
import mk.ukim.finki.wp.kino.dto.tmdb.TmdbTvDto;
import mk.ukim.finki.wp.kino.dto.api.MediaDetailsDto;
import mk.ukim.finki.wp.kino.dto.api.MediaType;
import mk.ukim.finki.wp.kino.dto.tmdb.details.TmdbMovieDetailsDto;
import mk.ukim.finki.wp.kino.dto.tmdb.details.TmdbSeasonDetailsDto;
import mk.ukim.finki.wp.kino.dto.tmdb.details.TmdbTvDetailsDto;
import mk.ukim.finki.wp.kino.dto.api.VideoDto;
import mk.ukim.finki.wp.kino.dto.api.KeywordDto;
import mk.ukim.finki.wp.kino.dto.api.SeasonDto;
import mk.ukim.finki.wp.kino.dto.api.EpisodeDto;
import mk.ukim.finki.wp.kino.dto.tmdb.details.misc.*;

import java.util.List;

/**
 * Factory for creating sample TMDB DTOs in tests.
 */
public final class TestDataFactory {

    private TestDataFactory() {
        // Utility class - prevent instantiation
    }

    public static TmdbMovieDto createMovie() {
        TmdbMovieDto movie = new TmdbMovieDto();
        movie.setId(1L);
        movie.setTitle("Test Movie");
        movie.setOverview("A test movie overview");
        movie.setPosterPath("/poster.jpg");
        movie.setBackdropPath("/backdrop.jpg");
        movie.setReleaseDate("2024-01-15");
        movie.setVoteAverage(7.5);
        return movie;
    }

    public static TmdbMovieDto createMovie(Long id, String title, String posterPath) {
        TmdbMovieDto movie = createMovie();
        movie.setId(id);
        movie.setTitle(title);
        movie.setPosterPath(posterPath);
        return movie;
    }

    public static TmdbTvDto createTvShow() {
        TmdbTvDto tv = new TmdbTvDto();
        tv.setId(2L);
        tv.setName("Test TV Show");
        tv.setOverview("A test TV show overview");
        tv.setPosterPath("/tv-poster.jpg");
        tv.setBackdropPath("/tv-backdrop.jpg");
        tv.setFirstAirDate("2024-04-20");
        tv.setVoteAverage(8.2);
        return tv;
    }

    public static TmdbTvDto createTvShow(Long id, String name, String posterPath) {
        TmdbTvDto tv = createTvShow();
        tv.setId(id);
        tv.setName(name);
        tv.setPosterPath(posterPath);
        return tv;
    }

    public static TmdbMultiSearchDto createMultiSearchMovie() {
        TmdbMultiSearchDto dto = new TmdbMultiSearchDto();
        dto.setId(1L);
        dto.setMediaType("movie");
        dto.setTitle("Test Movie");
        dto.setPosterPath("/poster.jpg");
        dto.setReleaseDate("2024-01-15");
        dto.setVoteAverage(7.5);
        return dto;
    }

    public static TmdbMultiSearchDto createMultiSearchTv() {
        TmdbMultiSearchDto dto = new TmdbMultiSearchDto();
        dto.setId(2L);
        dto.setMediaType("tv");
        dto.setName("Test TV Show");
        dto.setPosterPath("/tv-poster.jpg");
        dto.setFirstAirDate("2024-04-20");
        dto.setVoteAverage(8.2);
        return dto;
    }

    public static TmdbMultiSearchDto createMultiSearchPerson() {
        TmdbMultiSearchDto dto = new TmdbMultiSearchDto();
        dto.setId(3L);
        dto.setMediaType("person");
        dto.setName("Test Person");
        dto.setPosterPath("/person.jpg");
        return dto;
    }

    public static <T> TmdbPagedResponse<T> createPagedResponse(List<T> results, int page, int totalPages, int totalResults) {
        TmdbPagedResponse<T> response = new TmdbPagedResponse<>();
        response.setPage(page);
        response.setResults(results);
        response.setTotalPages(totalPages);
        response.setTotalResults(totalResults);
        return response;
    }

    public static <T> TmdbPagedResponse<T> createPagedResponse(List<T> results) {
        return createPagedResponse(results, 1, 1, results.size());
    }

    public static TmdbMovieDetailsDto createMovieDetails() {
        TmdbMovieDetailsDto details = new TmdbMovieDetailsDto();
        details.setId(1L);
        details.setTitle("Test Movie");
        details.setTagline("A test tagline");
        details.setOverview("A test movie overview");
        details.setPosterPath("/poster.jpg");
        details.setBackdropPath("/backdrop.jpg");
        details.setVoteAverage(7.5);
        details.setReleaseDate("2024-01-15");
        details.setRuntime(120);
        details.setStatus("Released");
        details.setOriginalLanguage("en");
        details.setGenres(List.of(createGenre(28L, "Action")));
        return details;
    }

    public static TmdbTvDetailsDto createTvDetails() {
        TmdbTvDetailsDto details = new TmdbTvDetailsDto();
        details.setId(2L);
        details.setName("Test TV Show");
        details.setTagline("A test tagline");
        details.setOverview("A test TV show overview");
        details.setPosterPath("/tv-poster.jpg");
        details.setBackdropPath("/tv-backdrop.jpg");
        details.setVoteAverage(8.2);
        details.setFirstAirDate("2024-04-20");
        details.setEpisodeRunTime(List.of(45));
        details.setStatus("Returning Series");
        details.setOriginalLanguage("en");
        details.setGenres(List.of(createGenre(18L, "Drama")));
        details.setCreatedBy(List.of(createCreator("Test Creator")));
        details.setNumberOfSeasons(2);
        details.setNumberOfEpisodes(3);
        details.setSeasons(List.of(
                createSeason(0, "Specials", 0),
                createSeason(1, "Season 1", 2),
                createSeason(2, "Season 2", 1)
        ));
        return details;
    }

    public static TmdbCreditsDto createCredits() {
        TmdbCreditsDto credits = new TmdbCreditsDto();
        credits.setCast(List.of(
                createCast(100L, "Actor One", "Character One", "/actor1.jpg"),
                createCast(101L, "Actor Two", "Character Two", "/actor2.jpg")
        ));
        credits.setCrew(List.of(
                createCrew(200L, "Director Name", "Director")
        ));
        return credits;
    }

    public static TmdbCastDto createCast(Long id, String name, String character, String profilePath) {
        TmdbCastDto cast = new TmdbCastDto();
        cast.setId(id);
        cast.setName(name);
        cast.setCharacter(character);
        cast.setProfilePath(profilePath);
        return cast;
    }

    public static TmdbCrewDto createCrew(Long id, String name, String job) {
        TmdbCrewDto crew = new TmdbCrewDto();
        crew.setId(id);
        crew.setName(name);
        crew.setJob(job);
        return crew;
    }

    public static TmdbCreatorDto createCreator(String name) {
        TmdbCreatorDto creator = new TmdbCreatorDto();
        creator.setName(name);
        return creator;
    }

    public static TmdbGenreDto createGenre(Long id, String name) {
        TmdbGenreDto genre = new TmdbGenreDto();
        genre.setId(id);
        genre.setName(name);
        return genre;
    }

    public static MediaDetailsDto createMovieDetailsDto() {
        MediaDetailsDto details = new MediaDetailsDto();
        details.setId(1L);
        details.setMediaType(MediaType.MOVIE);
        details.setTitle("Test Movie");
        details.setTagline("A test tagline");
        details.setOverview("A test movie overview");
        details.setPosterUrl("/poster.jpg");
        details.setBackdropUrl("/backdrop.jpg");
        details.setRating(7.5);
        details.setDate("2024-01-15");
        details.setGenres(List.of("Action"));
        details.setRuntimeMinutes(120);
        details.setStatus("Released");
        details.setOriginalLanguage("en");
        return details;
    }

    public static MediaDetailsDto createTvDetailsDto() {
        MediaDetailsDto details = new MediaDetailsDto();
        details.setId(2L);
        details.setMediaType(MediaType.TV);
        details.setTitle("Test TV Show");
        details.setTagline("A test tagline");
        details.setOverview("A test TV show overview");
        details.setPosterUrl("/tv-poster.jpg");
        details.setBackdropUrl("/tv-backdrop.jpg");
        details.setRating(8.2);
        details.setDate("2024-04-20");
        details.setGenres(List.of("Drama"));
        details.setRuntimeMinutes(45);
        details.setStatus("Returning Series");
        details.setOriginalLanguage("en");
        details.setCreator("Test Creator");
        return details;
    }

    public static TmdbVideoDto createVideo(String id, String key, String name, String site, String type, boolean official, String publishedAt) {
        TmdbVideoDto video = new TmdbVideoDto();
        video.setId(id);
        video.setKey(key);
        video.setName(name);
        video.setSite(site);
        video.setType(type);
        video.setOfficial(official);
        video.setPublishedAt(publishedAt);
        return video;
    }

    public static TmdbVideoResponseDto createTmdbVideoResponse(List<TmdbVideoDto> videos) {
        TmdbVideoResponseDto response = new TmdbVideoResponseDto();
        response.setId(1L);
        response.setResults(videos);
        return response;
    }

    public static VideoDto createVideoDto(String id, String key, String name, String site, String type, boolean official, String publishedAt) {
        return new VideoDto(id, key, name, site, type, official, publishedAt);
    }

    public static VideoDto createVideoDto() {
        return createVideoDto("vid1", "abc123", "Official Trailer", "YouTube", "Trailer", true, "2024-01-15T10:00:00.000Z");
    }

    public static TmdbKeywordDto createKeyword(Long id, String name) {
        TmdbKeywordDto keyword = new TmdbKeywordDto();
        keyword.setId(id);
        keyword.setName(name);
        return keyword;
    }

    public static TmdbKeywordsDto createTmdbMovieKeywords(List<TmdbKeywordDto> keywords) {
        TmdbKeywordsDto dto = new TmdbKeywordsDto();
        dto.setKeywords(keywords);
        return dto;
    }

    public static TmdbKeywordsDto createTmdbTvKeywords(List<TmdbKeywordDto> keywords) {
        TmdbKeywordsDto dto = new TmdbKeywordsDto();
        dto.setResults(keywords);
        return dto;
    }

    public static KeywordDto createKeywordDto(Long id, String name) {
        KeywordDto dto = new KeywordDto();
        dto.setId(id);
        dto.setName(name);
        return dto;
    }

    public static TmdbSeasonDto createSeason(Integer seasonNumber, String name, Integer episodeCount) {
        TmdbSeasonDto season = new TmdbSeasonDto();
        season.setId(1000L + seasonNumber);
        season.setSeasonNumber(seasonNumber);
        season.setName(name);
        season.setOverview(name + " overview");
        season.setPosterPath("/season-" + seasonNumber + ".jpg");
        season.setAirDate("2024-0" + Math.min(9, seasonNumber + 1) + "-01");
        season.setEpisodeCount(episodeCount);
        return season;
    }

    public static TmdbSeasonDetailsDto createSeasonDetails(Integer seasonNumber, List<TmdbEpisodeDto> episodes) {
        TmdbSeasonDetailsDto season = new TmdbSeasonDetailsDto();
        season.setId(1000L + seasonNumber);
        season.setSeasonNumber(seasonNumber);
        season.setName("Season " + seasonNumber);
        season.setOverview("Season " + seasonNumber + " overview");
        season.setPosterPath("/season-" + seasonNumber + ".jpg");
        season.setAirDate("2024-01-01");
        season.setEpisodes(episodes);
        return season;
    }

    public static TmdbEpisodeDto createEpisode(Integer seasonNumber, Integer episodeNumber, String name, String stillPath) {
        TmdbEpisodeDto episode = new TmdbEpisodeDto();
        episode.setId(2000L + seasonNumber * 100L + episodeNumber);
        episode.setSeasonNumber(seasonNumber);
        episode.setEpisodeNumber(episodeNumber);
        episode.setName(name);
        episode.setOverview(name + " overview");
        episode.setStillPath(stillPath);
        episode.setAirDate("2024-01-0" + Math.min(9, episodeNumber) + "");
        episode.setRuntime(45);
        episode.setVoteAverage(8.0);
        episode.setVoteCount(120);
        return episode;
    }

    public static SeasonDto createSeasonDto(Integer seasonNumber, String name, Integer episodeCount) {
        return new SeasonDto(
                1000L + seasonNumber,
                seasonNumber,
                name,
                name + " overview",
                "https://image.tmdb.org/t/p/w500/season-" + seasonNumber + ".jpg",
                "2024-01-01",
                episodeCount
        );
    }

    public static EpisodeDto createEpisodeDto(Integer seasonNumber, Integer episodeNumber, String name) {
        return new EpisodeDto(
                2000L + seasonNumber * 100L + episodeNumber,
                seasonNumber,
                episodeNumber,
                name,
                name + " overview",
                "https://image.tmdb.org/t/p/w500/still-" + episodeNumber + ".jpg",
                "2024-01-01",
                45,
                8.0,
                120
        );
    }
}
