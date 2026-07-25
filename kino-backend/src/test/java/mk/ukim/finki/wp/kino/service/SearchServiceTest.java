package mk.ukim.finki.wp.kino.service;

import mk.ukim.finki.wp.kino.dto.api.MediaCardDto;
import mk.ukim.finki.wp.kino.dto.api.MediaType;
import mk.ukim.finki.wp.kino.dto.api.PagedResponseDto;
import mk.ukim.finki.wp.kino.dto.tmdb.TmdbMovieDto;
import mk.ukim.finki.wp.kino.dto.tmdb.TmdbMultiSearchDto;
import mk.ukim.finki.wp.kino.dto.tmdb.TmdbPagedResponse;
import mk.ukim.finki.wp.kino.dto.tmdb.TmdbTvDto;
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
class SearchServiceTest {

    @Mock
    private TmdbClient tmdbClient;

    private SearchService searchService;
    private final String imageBaseUrl = "https://image.tmdb.org/t/p/w500";

    @BeforeEach
    void setUp() {
        searchService = new SearchService(tmdbClient, imageBaseUrl);
    }

    // ===== Search Movies =====

    @Test
    void searchMovies_withValidQuery_returnsMappedResults() {
        TmdbMovieDto movie = TestDataFactory.createMovie(1L, "Batman", "/batman.jpg");
        TmdbPagedResponse<TmdbMovieDto> tmdbResponse = TestDataFactory.createPagedResponse(List.of(movie));

        when(tmdbClient.searchMovies("batman", 1)).thenReturn(tmdbResponse);

        PagedResponseDto<MediaCardDto> result = searchService.searchMovies("batman", 1);

        assertNotNull(result);
        assertEquals(1, result.getItems().size());
        assertEquals("Batman", result.getItems().get(0).getTitle());
        assertEquals(MediaType.MOVIE, result.getItems().get(0).getMediaType());
        verify(tmdbClient).searchMovies("batman", 1);
    }

    @Test
    void searchMovies_withNullQuery_returnsEmptyResponse() {
        PagedResponseDto<MediaCardDto> result = searchService.searchMovies(null, 1);

        assertNotNull(result);
        assertEquals(1, result.getPage());
        assertEquals(0, result.getTotalPages());
        assertTrue(result.getItems().isEmpty());
        assertEquals(0, result.getTotalResults());
        verifyNoInteractions(tmdbClient);
    }

    @Test
    void searchMovies_withBlankQuery_returnsEmptyResponse() {
        PagedResponseDto<MediaCardDto> result = searchService.searchMovies("   ", 1);

        assertNotNull(result);
        assertTrue(result.getItems().isEmpty());
        verifyNoInteractions(tmdbClient);
    }

    @Test
    void searchMovies_withEmptyQuery_returnsEmptyResponse() {
        PagedResponseDto<MediaCardDto> result = searchService.searchMovies("", 1);

        assertNotNull(result);
        assertTrue(result.getItems().isEmpty());
        verifyNoInteractions(tmdbClient);
    }

    @Test
    void searchMovies_withPageLessThanOne_defaultsToOne() {
        TmdbMovieDto movie = TestDataFactory.createMovie();
        TmdbPagedResponse<TmdbMovieDto> tmdbResponse = TestDataFactory.createPagedResponse(List.of(movie));

        when(tmdbClient.searchMovies("test", 1)).thenReturn(tmdbResponse);

        searchService.searchMovies("test", 0);

        verify(tmdbClient).searchMovies("test", 1);
    }

    // ===== Search TV =====

    @Test
    void searchTv_withValidQuery_returnsMappedResults() {
        TmdbTvDto tv = TestDataFactory.createTvShow(2L, "Breaking Bad", "/bb.jpg");
        TmdbPagedResponse<TmdbTvDto> tmdbResponse = TestDataFactory.createPagedResponse(List.of(tv));

        when(tmdbClient.searchTvShows("breaking bad", 1)).thenReturn(tmdbResponse);

        PagedResponseDto<MediaCardDto> result = searchService.searchTv("breaking bad", 1);

        assertNotNull(result);
        assertEquals(1, result.getItems().size());
        assertEquals("Breaking Bad", result.getItems().get(0).getTitle());
        assertEquals(MediaType.TV, result.getItems().get(0).getMediaType());
        verify(tmdbClient).searchTvShows("breaking bad", 1);
    }

    @Test
    void searchTv_withNullQuery_returnsEmptyResponse() {
        PagedResponseDto<MediaCardDto> result = searchService.searchTv(null, 1);

        assertNotNull(result);
        assertTrue(result.getItems().isEmpty());
        verifyNoInteractions(tmdbClient);
    }

    @Test
    void searchTv_withBlankQuery_returnsEmptyResponse() {
        PagedResponseDto<MediaCardDto> result = searchService.searchTv("   ", 1);

        assertNotNull(result);
        assertTrue(result.getItems().isEmpty());
        verifyNoInteractions(tmdbClient);
    }

    @Test
    void searchTv_withPageLessThanOne_defaultsToOne() {
        TmdbTvDto tv = TestDataFactory.createTvShow();
        TmdbPagedResponse<TmdbTvDto> tmdbResponse = TestDataFactory.createPagedResponse(List.of(tv));

        when(tmdbClient.searchTvShows("test", 1)).thenReturn(tmdbResponse);

        searchService.searchTv("test", -5);

        verify(tmdbClient).searchTvShows("test", 1);
    }

    // ===== Search Multi =====

    @Test
    void searchMulti_withValidQuery_returnsMappedMoviesAndTv() {
        TmdbMultiSearchDto movie = TestDataFactory.createMultiSearchMovie();
        TmdbMultiSearchDto tv = TestDataFactory.createMultiSearchTv();
        TmdbPagedResponse<TmdbMultiSearchDto> tmdbResponse = TestDataFactory.createPagedResponse(
            List.of(movie, tv)
        );

        when(tmdbClient.searchMulti("star wars", 1)).thenReturn(tmdbResponse);

        PagedResponseDto<MediaCardDto> result = searchService.searchMulti("star wars", 1);

        assertNotNull(result);
        assertEquals(2, result.getItems().size());
        assertEquals(MediaType.MOVIE, result.getItems().get(0).getMediaType());
        assertEquals(MediaType.TV, result.getItems().get(1).getMediaType());
        verify(tmdbClient).searchMulti("star wars", 1);
    }

    @Test
    void searchMulti_filtersOutPersonResults() {
        TmdbMultiSearchDto movie = TestDataFactory.createMultiSearchMovie();
        TmdbMultiSearchDto person = TestDataFactory.createMultiSearchPerson();
        TmdbMultiSearchDto tv = TestDataFactory.createMultiSearchTv();
        TmdbPagedResponse<TmdbMultiSearchDto> tmdbResponse = TestDataFactory.createPagedResponse(
            List.of(movie, person, tv)
        );

        when(tmdbClient.searchMulti("test", 1)).thenReturn(tmdbResponse);

        PagedResponseDto<MediaCardDto> result = searchService.searchMulti("test", 1);

        assertEquals(2, result.getItems().size());
        assertEquals(MediaType.MOVIE, result.getItems().get(0).getMediaType());
        assertEquals(MediaType.TV, result.getItems().get(1).getMediaType());
    }

    @Test
    void searchMulti_withNullQuery_returnsEmptyResponse() {
        PagedResponseDto<MediaCardDto> result = searchService.searchMulti(null, 1);

        assertNotNull(result);
        assertTrue(result.getItems().isEmpty());
        verifyNoInteractions(tmdbClient);
    }

    @Test
    void searchMulti_withBlankQuery_returnsEmptyResponse() {
        PagedResponseDto<MediaCardDto> result = searchService.searchMulti("   ", 1);

        assertNotNull(result);
        assertTrue(result.getItems().isEmpty());
        verifyNoInteractions(tmdbClient);
    }

    @Test
    void searchMulti_withPageLessThanOne_defaultsToOne() {
        TmdbMultiSearchDto movie = TestDataFactory.createMultiSearchMovie();
        TmdbPagedResponse<TmdbMultiSearchDto> tmdbResponse = TestDataFactory.createPagedResponse(List.of(movie));

        when(tmdbClient.searchMulti("test", 1)).thenReturn(tmdbResponse);

        searchService.searchMulti("test", 0);

        verify(tmdbClient).searchMulti("test", 1);
    }

    @Test
    void searchMulti_withOnlyPersonResults_returnsEmptyList() {
        TmdbMultiSearchDto person1 = TestDataFactory.createMultiSearchPerson();
        TmdbMultiSearchDto person2 = TestDataFactory.createMultiSearchPerson();
        TmdbPagedResponse<TmdbMultiSearchDto> tmdbResponse = TestDataFactory.createPagedResponse(
            List.of(person1, person2)
        );

        when(tmdbClient.searchMulti("actor name", 1)).thenReturn(tmdbResponse);

        PagedResponseDto<MediaCardDto> result = searchService.searchMulti("actor name", 1);

        assertTrue(result.getItems().isEmpty());
    }

    @Test
    void searchMulti_mapsMovieFieldsCorrectly() {
        TmdbMultiSearchDto movie = TestDataFactory.createMultiSearchMovie();
        TmdbPagedResponse<TmdbMultiSearchDto> tmdbResponse = TestDataFactory.createPagedResponse(List.of(movie));

        when(tmdbClient.searchMulti("test", 1)).thenReturn(tmdbResponse);

        PagedResponseDto<MediaCardDto> result = searchService.searchMulti("test", 1);

        MediaCardDto card = result.getItems().get(0);
        assertEquals(1L, card.getId());
        assertEquals(MediaType.MOVIE, card.getMediaType());
        assertEquals("Test Movie", card.getTitle());
        assertEquals("https://image.tmdb.org/t/p/w500/poster.jpg", card.getPosterUrl());
        assertEquals(7.5, card.getRating());
        assertEquals("2024-01-15", card.getDate());
    }

    @Test
    void searchMulti_mapsTvFieldsCorrectly() {
        TmdbMultiSearchDto tv = TestDataFactory.createMultiSearchTv();
        TmdbPagedResponse<TmdbMultiSearchDto> tmdbResponse = TestDataFactory.createPagedResponse(List.of(tv));

        when(tmdbClient.searchMulti("test", 1)).thenReturn(tmdbResponse);

        PagedResponseDto<MediaCardDto> result = searchService.searchMulti("test", 1);

        MediaCardDto card = result.getItems().get(0);
        assertEquals(2L, card.getId());
        assertEquals(MediaType.TV, card.getMediaType());
        assertEquals("Test TV Show", card.getTitle());
        assertEquals("https://image.tmdb.org/t/p/w500/tv-poster.jpg", card.getPosterUrl());
        assertEquals(8.2, card.getRating());
        assertEquals("2024-04-20", card.getDate());
    }
}
