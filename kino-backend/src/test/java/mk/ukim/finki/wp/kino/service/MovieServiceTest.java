package mk.ukim.finki.wp.kino.service;

import mk.ukim.finki.wp.kino.dto.api.MediaCardDto;
import mk.ukim.finki.wp.kino.dto.api.MediaFilterDto;
import mk.ukim.finki.wp.kino.dto.api.MediaType;
import mk.ukim.finki.wp.kino.dto.api.PagedResponseDto;
import mk.ukim.finki.wp.kino.dto.tmdb.TmdbMovieDto;
import mk.ukim.finki.wp.kino.dto.tmdb.TmdbPagedResponse;
import mk.ukim.finki.wp.kino.tmdb.TmdbClient;
import mk.ukim.finki.wp.kino.util.TestDataFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MovieServiceTest {

    @Mock
    private TmdbClient tmdbClient;

    @Mock
    private MediaFilterService mediaFilterService;

    private MovieService movieService;
    private final String imageBaseUrl = "https://image.tmdb.org/t/p/w500";

    @BeforeEach
    void setUp() {
        movieService = new MovieService(tmdbClient, imageBaseUrl, mediaFilterService);
    }

    @Test
    void getTrendingMovies_returnsMappedResults() {
        TmdbMovieDto movie = TestDataFactory.createMovie(1L, "Test Movie", "/poster.jpg");
        TmdbPagedResponse<TmdbMovieDto> tmdbResponse = TestDataFactory.createPagedResponse(
            List.of(movie), 1, 5, 100
        );

        MediaFilterDto filter = new MediaFilterDto();
        when(tmdbClient.getTrendingMovies("day", 1)).thenReturn(tmdbResponse);
        when(mediaFilterService.applyFiltersAndSort(anyList(), eq(filter), eq(1), eq(20)))
            .thenAnswer(inv -> inv.getArgument(0));
        when(mediaFilterService.countAfterFilters(anyList(), eq(filter))).thenReturn(1);

        PagedResponseDto<MediaCardDto> result = movieService.getTrendingMovies("day", 1, filter);

        assertNotNull(result);
        assertEquals(1, result.getPage());
        assertEquals(1, result.getTotalPages());
        assertEquals(1, result.getTotalResults());
        assertEquals(1, result.getItems().size());

        MediaCardDto card = result.getItems().get(0);
        assertEquals(1L, card.getId());
        assertEquals(MediaType.MOVIE, card.getMediaType());
        assertEquals("Test Movie", card.getTitle());
        assertEquals("https://image.tmdb.org/t/p/w500/poster.jpg", card.getPosterUrl());
        assertEquals(7.5, card.getRating());
        assertEquals("2024-01-15", card.getDate());

        verify(tmdbClient).getTrendingMovies("day", 1);
    }

    @Test
    void getTrendingMovies_withNullPosterPath_returnsNullPosterUrl() {
        TmdbMovieDto movie = TestDataFactory.createMovie(1L, "Test Movie", null);
        TmdbPagedResponse<TmdbMovieDto> tmdbResponse = TestDataFactory.createPagedResponse(List.of(movie));

        MediaFilterDto filter = new MediaFilterDto();
        when(tmdbClient.getTrendingMovies("day", 1)).thenReturn(tmdbResponse);
        when(mediaFilterService.applyFiltersAndSort(anyList(), eq(filter), eq(1), eq(20)))
            .thenAnswer(inv -> inv.getArgument(0));
        when(mediaFilterService.countAfterFilters(anyList(), eq(filter))).thenReturn(1);

        PagedResponseDto<MediaCardDto> result = movieService.getTrendingMovies("day", 1, filter);

        assertNull(result.getItems().get(0).getPosterUrl());
    }

    @Test
    void getTrendingMovies_withBlankPosterPath_returnsNullPosterUrl() {
        TmdbMovieDto movie = TestDataFactory.createMovie(1L, "Test Movie", "   ");
        TmdbPagedResponse<TmdbMovieDto> tmdbResponse = TestDataFactory.createPagedResponse(List.of(movie));

        MediaFilterDto filter = new MediaFilterDto();
        when(tmdbClient.getTrendingMovies("day", 1)).thenReturn(tmdbResponse);
        when(mediaFilterService.applyFiltersAndSort(anyList(), eq(filter), eq(1), eq(20)))
            .thenAnswer(inv -> inv.getArgument(0));
        when(mediaFilterService.countAfterFilters(anyList(), eq(filter))).thenReturn(1);

        PagedResponseDto<MediaCardDto> result = movieService.getTrendingMovies("day", 1, filter);

        assertNull(result.getItems().get(0).getPosterUrl());
    }

    @Test
    void getPopularMovies_returnsMappedResults() {
        TmdbMovieDto movie = TestDataFactory.createMovie(2L, "Popular Movie", "/popular.jpg");
        TmdbPagedResponse<TmdbMovieDto> tmdbResponse = TestDataFactory.createPagedResponse(List.of(movie));

        MediaFilterDto filter = new MediaFilterDto();
        when(tmdbClient.getPopularMovies(1)).thenReturn(tmdbResponse);
        when(mediaFilterService.applyFiltersAndSort(anyList(), eq(filter), eq(1), eq(20)))
            .thenAnswer(inv -> inv.getArgument(0));
        when(mediaFilterService.countAfterFilters(anyList(), eq(filter))).thenReturn(1);

        PagedResponseDto<MediaCardDto> result = movieService.getPopularMovies(1, filter);

        assertNotNull(result);
        assertEquals(1, result.getItems().size());
        assertEquals("Popular Movie", result.getItems().get(0).getTitle());
        assertEquals(MediaType.MOVIE, result.getItems().get(0).getMediaType());
        verify(tmdbClient).getPopularMovies(1);
    }

    @Test
    void getTopRatedMovies_returnsMappedResults() {
        TmdbMovieDto movie = TestDataFactory.createMovie(3L, "Top Rated", "/top.jpg");
        TmdbPagedResponse<TmdbMovieDto> tmdbResponse = TestDataFactory.createPagedResponse(List.of(movie));

        MediaFilterDto filter = new MediaFilterDto();
        when(tmdbClient.getTopRatedMovies(2)).thenReturn(tmdbResponse);
        when(mediaFilterService.applyFiltersAndSort(anyList(), eq(filter), eq(1), eq(20)))
            .thenAnswer(inv -> inv.getArgument(0));
        when(mediaFilterService.countAfterFilters(anyList(), eq(filter))).thenReturn(1);

        PagedResponseDto<MediaCardDto> result = movieService.getTopRatedMovies(2, filter);

        assertNotNull(result);
        assertEquals(1, result.getItems().size());
        assertEquals("Top Rated", result.getItems().get(0).getTitle());
        verify(tmdbClient).getTopRatedMovies(2);
    }

    @Test
    void getUpcomingMovies_returnsMappedResults() {
        TmdbMovieDto movie = TestDataFactory.createMovie(4L, "Upcoming", "/upcoming.jpg");
        TmdbPagedResponse<TmdbMovieDto> tmdbResponse = TestDataFactory.createPagedResponse(List.of(movie));

        MediaFilterDto filter = new MediaFilterDto();
        when(tmdbClient.getUpcomingMovies(3)).thenReturn(tmdbResponse);
        when(mediaFilterService.applyFiltersAndSort(anyList(), eq(filter), eq(1), eq(20)))
            .thenAnswer(inv -> inv.getArgument(0));
        when(mediaFilterService.countAfterFilters(anyList(), eq(filter))).thenReturn(1);

        PagedResponseDto<MediaCardDto> result = movieService.getUpcomingMovies(3, filter);

        assertNotNull(result);
        assertEquals(1, result.getItems().size());
        assertEquals("Upcoming", result.getItems().get(0).getTitle());
        verify(tmdbClient).getUpcomingMovies(3);
    }

    @Test
    void getPopularMovies_withEmptyResults_returnsEmptyList() {
        TmdbPagedResponse<TmdbMovieDto> tmdbResponse = TestDataFactory.createPagedResponse(List.of());

        MediaFilterDto filter = new MediaFilterDto();
        when(tmdbClient.getPopularMovies(1)).thenReturn(tmdbResponse);
        when(mediaFilterService.applyFiltersAndSort(anyList(), eq(filter), eq(1), eq(20)))
            .thenAnswer(inv -> List.of());
        when(mediaFilterService.countAfterFilters(anyList(), eq(filter))).thenReturn(0);

        PagedResponseDto<MediaCardDto> result = movieService.getPopularMovies(1, filter);

        assertNotNull(result);
        assertTrue(result.getItems().isEmpty());
        assertEquals(0, result.getTotalResults());
    }

    @Test
    void getTrendingMovies_withMultipleMovies_mapsAllCorrectly() {
        TmdbMovieDto movie1 = TestDataFactory.createMovie(1L, "Movie One", "/p1.jpg");
        TmdbMovieDto movie2 = TestDataFactory.createMovie(2L, "Movie Two", "/p2.jpg");
        TmdbMovieDto movie3 = TestDataFactory.createMovie(3L, "Movie Three", "/p3.jpg");
        TmdbPagedResponse<TmdbMovieDto> tmdbResponse = TestDataFactory.createPagedResponse(
            List.of(movie1, movie2, movie3)
        );

        MediaFilterDto filter = new MediaFilterDto();
        when(tmdbClient.getTrendingMovies("week", 1)).thenReturn(tmdbResponse);
        when(mediaFilterService.applyFiltersAndSort(anyList(), eq(filter), eq(1), eq(20)))
            .thenAnswer(inv -> inv.getArgument(0));
        when(mediaFilterService.countAfterFilters(anyList(), eq(filter))).thenReturn(3);

        PagedResponseDto<MediaCardDto> result = movieService.getTrendingMovies("week", 1, filter);

        assertEquals(3, result.getItems().size());
        assertEquals("Movie One", result.getItems().get(0).getTitle());
        assertEquals("Movie Two", result.getItems().get(1).getTitle());
        assertEquals("Movie Three", result.getItems().get(2).getTitle());
        assertEquals("https://image.tmdb.org/t/p/w500/p1.jpg", result.getItems().get(0).getPosterUrl());
        assertEquals("https://image.tmdb.org/t/p/w500/p2.jpg", result.getItems().get(1).getPosterUrl());
        assertEquals("https://image.tmdb.org/t/p/w500/p3.jpg", result.getItems().get(2).getPosterUrl());
    }
}
