package mk.ukim.finki.wp.kino.util;

import mk.ukim.finki.wp.kino.dto.tmdb.TmdbMovieDto;
import mk.ukim.finki.wp.kino.dto.tmdb.TmdbMultiSearchDto;
import mk.ukim.finki.wp.kino.dto.tmdb.TmdbPagedResponse;
import mk.ukim.finki.wp.kino.dto.tmdb.TmdbTvDto;
import mk.ukim.finki.wp.kino.dto.tmdb.details.MediaDetailsDto;
import mk.ukim.finki.wp.kino.dto.tmdb.details.TmdbMovieDetailsDto;
import mk.ukim.finki.wp.kino.dto.tmdb.details.TmdbTvDetailsDto;
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
        details.setMediaType("MOVIE");
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
        details.setMediaType("TV");
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
}
