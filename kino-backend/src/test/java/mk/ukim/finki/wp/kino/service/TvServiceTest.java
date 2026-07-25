package mk.ukim.finki.wp.kino.service;

import mk.ukim.finki.wp.kino.dto.api.MediaCardDto;
import mk.ukim.finki.wp.kino.dto.api.MediaType;
import mk.ukim.finki.wp.kino.dto.api.PagedResponseDto;
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
class TvServiceTest {

    @Mock
    private TmdbClient tmdbClient;

    private TvService tvService;
    private final String imageBaseUrl = "https://image.tmdb.org/t/p/w500";

    @BeforeEach
    void setUp() {
        tvService = new TvService(tmdbClient, imageBaseUrl);
    }

    @Test
    void getTrendingTvShows_returnsMappedResults() {
        TmdbTvDto tv = TestDataFactory.createTvShow(1L, "Test TV Show", "/poster.jpg");
        TmdbPagedResponse<TmdbTvDto> tmdbResponse = TestDataFactory.createPagedResponse(
            List.of(tv), 1, 5, 100
        );

        when(tmdbClient.getTrendingTvShows("day", 1)).thenReturn(tmdbResponse);

        PagedResponseDto<MediaCardDto> result = tvService.getTrendingTvShows("day", 1);

        assertNotNull(result);
        assertEquals(1, result.getPage());
        assertEquals(5, result.getTotalPages());
        assertEquals(100, result.getTotalResults());
        assertEquals(1, result.getItems().size());

        MediaCardDto card = result.getItems().get(0);
        assertEquals(1L, card.getId());
        assertEquals(MediaType.TV, card.getMediaType());
        assertEquals("Test TV Show", card.getTitle());
        assertEquals("https://image.tmdb.org/t/p/w500/poster.jpg", card.getPosterUrl());
        assertEquals(8.2, card.getRating());
        assertEquals("2024-04-20", card.getDate());

        verify(tmdbClient).getTrendingTvShows("day", 1);
    }

    @Test
    void getTrendingTvShows_withNullPosterPath_returnsNullPosterUrl() {
        TmdbTvDto tv = TestDataFactory.createTvShow(1L, "Test TV Show", null);
        TmdbPagedResponse<TmdbTvDto> tmdbResponse = TestDataFactory.createPagedResponse(List.of(tv));

        when(tmdbClient.getTrendingTvShows("day", 1)).thenReturn(tmdbResponse);

        PagedResponseDto<MediaCardDto> result = tvService.getTrendingTvShows("day", 1);

        assertNull(result.getItems().get(0).getPosterUrl());
    }

    @Test
    void getTrendingTvShows_withBlankPosterPath_returnsNullPosterUrl() {
        TmdbTvDto tv = TestDataFactory.createTvShow(1L, "Test TV Show", "   ");
        TmdbPagedResponse<TmdbTvDto> tmdbResponse = TestDataFactory.createPagedResponse(List.of(tv));

        when(tmdbClient.getTrendingTvShows("day", 1)).thenReturn(tmdbResponse);

        PagedResponseDto<MediaCardDto> result = tvService.getTrendingTvShows("day", 1);

        assertNull(result.getItems().get(0).getPosterUrl());
    }

    @Test
    void getPopularTvShows_returnsMappedResults() {
        TmdbTvDto tv = TestDataFactory.createTvShow(2L, "Popular TV", "/popular.jpg");
        TmdbPagedResponse<TmdbTvDto> tmdbResponse = TestDataFactory.createPagedResponse(List.of(tv));

        when(tmdbClient.getPopularTvShows(1)).thenReturn(tmdbResponse);

        PagedResponseDto<MediaCardDto> result = tvService.getPopularTvShows(1);

        assertNotNull(result);
        assertEquals(1, result.getItems().size());
        assertEquals("Popular TV", result.getItems().get(0).getTitle());
        assertEquals(MediaType.TV, result.getItems().get(0).getMediaType());
        verify(tmdbClient).getPopularTvShows(1);
    }

    @Test
    void getTopRatedTvShows_returnsMappedResults() {
        TmdbTvDto tv = TestDataFactory.createTvShow(3L, "Top Rated TV", "/top.jpg");
        TmdbPagedResponse<TmdbTvDto> tmdbResponse = TestDataFactory.createPagedResponse(List.of(tv));

        when(tmdbClient.getTopRatedTvShows(2)).thenReturn(tmdbResponse);

        PagedResponseDto<MediaCardDto> result = tvService.getTopRatedTvShows(2);

        assertNotNull(result);
        assertEquals(1, result.getItems().size());
        assertEquals("Top Rated TV", result.getItems().get(0).getTitle());
        verify(tmdbClient).getTopRatedTvShows(2);
    }

    @Test
    void getAiringTvShows_returnsMappedResults() {
        TmdbTvDto tv = TestDataFactory.createTvShow(4L, "Airing TV", "/airing.jpg");
        TmdbPagedResponse<TmdbTvDto> tmdbResponse = TestDataFactory.createPagedResponse(List.of(tv));

        when(tmdbClient.getAiringTvShows(3)).thenReturn(tmdbResponse);

        PagedResponseDto<MediaCardDto> result = tvService.getAiringTvShows(3);

        assertNotNull(result);
        assertEquals(1, result.getItems().size());
        assertEquals("Airing TV", result.getItems().get(0).getTitle());
        verify(tmdbClient).getAiringTvShows(3);
    }

    @Test
    void getPopularTvShows_withEmptyResults_returnsEmptyList() {
        TmdbPagedResponse<TmdbTvDto> tmdbResponse = TestDataFactory.createPagedResponse(List.of());

        when(tmdbClient.getPopularTvShows(1)).thenReturn(tmdbResponse);

        PagedResponseDto<MediaCardDto> result = tvService.getPopularTvShows(1);

        assertNotNull(result);
        assertTrue(result.getItems().isEmpty());
        assertEquals(0, result.getTotalResults());
    }

    @Test
    void getTrendingTvShows_withMultipleShows_mapsAllCorrectly() {
        TmdbTvDto tv1 = TestDataFactory.createTvShow(1L, "Show One", "/p1.jpg");
        TmdbTvDto tv2 = TestDataFactory.createTvShow(2L, "Show Two", "/p2.jpg");
        TmdbTvDto tv3 = TestDataFactory.createTvShow(3L, "Show Three", "/p3.jpg");
        TmdbPagedResponse<TmdbTvDto> tmdbResponse = TestDataFactory.createPagedResponse(
            List.of(tv1, tv2, tv3)
        );

        when(tmdbClient.getTrendingTvShows("week", 1)).thenReturn(tmdbResponse);

        PagedResponseDto<MediaCardDto> result = tvService.getTrendingTvShows("week", 1);

        assertEquals(3, result.getItems().size());
        assertEquals("Show One", result.getItems().get(0).getTitle());
        assertEquals("Show Two", result.getItems().get(1).getTitle());
        assertEquals("Show Three", result.getItems().get(2).getTitle());
        assertEquals("https://image.tmdb.org/t/p/w500/p1.jpg", result.getItems().get(0).getPosterUrl());
        assertEquals("https://image.tmdb.org/t/p/w500/p2.jpg", result.getItems().get(1).getPosterUrl());
        assertEquals("https://image.tmdb.org/t/p/w500/p3.jpg", result.getItems().get(2).getPosterUrl());
    }
}
