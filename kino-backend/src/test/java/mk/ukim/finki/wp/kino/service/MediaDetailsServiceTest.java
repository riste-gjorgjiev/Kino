package mk.ukim.finki.wp.kino.service;

import mk.ukim.finki.wp.kino.dto.tmdb.details.MediaDetailsDto;
import mk.ukim.finki.wp.kino.dto.tmdb.details.VideoDto;
import mk.ukim.finki.wp.kino.dto.tmdb.details.misc.TmdbCreditsDto;
import mk.ukim.finki.wp.kino.dto.tmdb.details.misc.TmdbVideoDto;
import mk.ukim.finki.wp.kino.dto.tmdb.details.misc.TmdbVideoResponseDto;
import mk.ukim.finki.wp.kino.tmdb.TmdbClient;
import mk.ukim.finki.wp.kino.util.TestDataFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MediaDetailsServiceTest {

    @Mock
    private TmdbClient tmdbClient;

    private MediaDetailsService mediaDetailsService;

    @BeforeEach
    void setUp() {
        mediaDetailsService = new MediaDetailsService(tmdbClient, "https://image.tmdb.org/t/p/w500");
    }

    @Test
    void getMovieDetails_onlyYouTubeVideosOfAllowedTypesReturned() {
        when(tmdbClient.getMovieDetails(anyLong())).thenReturn(TestDataFactory.createMovieDetails());
        when(tmdbClient.getMovieCredits(anyLong())).thenReturn(TestDataFactory.createCredits());

        List<TmdbVideoDto> videos = List.of(
            TestDataFactory.createVideo("v1", "k1", "YT Trailer", "YouTube", "Trailer", true, "2024-01-15"),
            TestDataFactory.createVideo("v2", "k2", "Vimeo Trailer", "Vimeo", "Trailer", true, "2024-01-15"),
            TestDataFactory.createVideo("v3", "k3", "YT Interview", "YouTube", "Interview", true, "2024-01-15"),
            TestDataFactory.createVideo("v4", "k4", "YT Teaser", "YouTube", "Teaser", true, "2024-01-15"),
            TestDataFactory.createVideo("v5", "k5", "YT Clip", "YouTube", "Clip", false, "2024-01-15"),
            TestDataFactory.createVideo("v6", "k6", "YT Featurette", "YouTube", "Featurette", false, "2024-01-15")
        );
        when(tmdbClient.getMovieVideos(anyLong())).thenReturn(TestDataFactory.createTmdbVideoResponse(videos));

        MediaDetailsDto result = mediaDetailsService.getMovieDetails(1L);

        assertEquals(4, result.getVideos().size());
        assertTrue(result.getVideos().stream().allMatch(v -> v.getSite().equalsIgnoreCase("YouTube")));
        assertTrue(result.getVideos().stream().allMatch(v ->
            v.getType().equalsIgnoreCase("Trailer") ||
            v.getType().equalsIgnoreCase("Teaser") ||
            v.getType().equalsIgnoreCase("Clip") ||
            v.getType().equalsIgnoreCase("Featurette")
        ));
    }

    @Test
    void getMovieDetails_sortingPriorityOfficialTrailerFirst() {
        when(tmdbClient.getMovieDetails(anyLong())).thenReturn(TestDataFactory.createMovieDetails());
        when(tmdbClient.getMovieCredits(anyLong())).thenReturn(TestDataFactory.createCredits());

        List<TmdbVideoDto> videos = List.of(
            TestDataFactory.createVideo("v1", "k1", "Clip", "YouTube", "Clip", false, "2024-03-15"),
            TestDataFactory.createVideo("v2", "k2", "Unofficial Trailer", "YouTube", "Trailer", false, "2024-02-15"),
            TestDataFactory.createVideo("v3", "k3", "Official Trailer", "YouTube", "Trailer", true, "2024-01-15"),
            TestDataFactory.createVideo("v4", "k4", "Teaser", "YouTube", "Teaser", true, "2024-04-15")
        );
        when(tmdbClient.getMovieVideos(anyLong())).thenReturn(TestDataFactory.createTmdbVideoResponse(videos));

        MediaDetailsDto result = mediaDetailsService.getMovieDetails(1L);

        List<VideoDto> sorted = result.getVideos();
        assertEquals(4, sorted.size());
        assertEquals("v3", sorted.get(0).getId());
        assertEquals("v4", sorted.get(1).getId());
        assertEquals("v2", sorted.get(2).getId());
        assertEquals("v1", sorted.get(3).getId());
    }

    @Test
    void getMovieDetails_videosCappedAt12() {
        when(tmdbClient.getMovieDetails(anyLong())).thenReturn(TestDataFactory.createMovieDetails());
        when(tmdbClient.getMovieCredits(anyLong())).thenReturn(TestDataFactory.createCredits());

        List<TmdbVideoDto> videos = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            videos.add(TestDataFactory.createVideo("v" + i, "k" + i, "Video " + i, "YouTube", "Trailer", false, "2024-01-15"));
        }
        when(tmdbClient.getMovieVideos(anyLong())).thenReturn(TestDataFactory.createTmdbVideoResponse(videos));

        MediaDetailsDto result = mediaDetailsService.getMovieDetails(1L);

        assertEquals(12, result.getVideos().size());
    }

    @Test
    void getMovieDetails_nullResponseReturnsEmptyList() {
        when(tmdbClient.getMovieDetails(anyLong())).thenReturn(TestDataFactory.createMovieDetails());
        when(tmdbClient.getMovieCredits(anyLong())).thenReturn(TestDataFactory.createCredits());
        when(tmdbClient.getMovieVideos(anyLong())).thenReturn(null);

        MediaDetailsDto result = mediaDetailsService.getMovieDetails(1L);

        assertNotNull(result.getVideos());
        assertTrue(result.getVideos().isEmpty());
    }

    @Test
    void getMovieDetails_nullResultsReturnsEmptyList() {
        when(tmdbClient.getMovieDetails(anyLong())).thenReturn(TestDataFactory.createMovieDetails());
        when(tmdbClient.getMovieCredits(anyLong())).thenReturn(TestDataFactory.createCredits());

        TmdbVideoResponseDto response = new TmdbVideoResponseDto();
        response.setId(1L);
        response.setResults(null);
        when(tmdbClient.getMovieVideos(anyLong())).thenReturn(response);

        MediaDetailsDto result = mediaDetailsService.getMovieDetails(1L);

        assertNotNull(result.getVideos());
        assertTrue(result.getVideos().isEmpty());
    }

    @Test
    void getTvDetails_onlyYouTubeVideosOfAllowedTypesReturned() {
        when(tmdbClient.getTvDetails(anyLong())).thenReturn(TestDataFactory.createTvDetails());
        when(tmdbClient.getTvCredits(anyLong())).thenReturn(TestDataFactory.createCredits());

        List<TmdbVideoDto> videos = List.of(
            TestDataFactory.createVideo("v1", "k1", "YT Trailer", "YouTube", "Trailer", true, "2024-01-15"),
            TestDataFactory.createVideo("v2", "k2", "Vimeo Trailer", "Vimeo", "Trailer", true, "2024-01-15"),
            TestDataFactory.createVideo("v3", "k3", "YT Behind Scenes", "YouTube", "Behind the Scenes", true, "2024-01-15")
        );
        when(tmdbClient.getTvVideos(anyLong())).thenReturn(TestDataFactory.createTmdbVideoResponse(videos));

        MediaDetailsDto result = mediaDetailsService.getTvDetails(1L);

        assertEquals(1, result.getVideos().size());
        assertEquals("v1", result.getVideos().get(0).getId());
    }

    @Test
    void getMovieDetails_newestPublishedFirst() {
        when(tmdbClient.getMovieDetails(anyLong())).thenReturn(TestDataFactory.createMovieDetails());
        when(tmdbClient.getMovieCredits(anyLong())).thenReturn(TestDataFactory.createCredits());

        List<TmdbVideoDto> videos = List.of(
            TestDataFactory.createVideo("v1", "k1", "Old Trailer", "YouTube", "Trailer", true, "2023-01-15"),
            TestDataFactory.createVideo("v2", "k2", "New Trailer", "YouTube", "Trailer", true, "2024-06-15"),
            TestDataFactory.createVideo("v3", "k3", "Mid Trailer", "YouTube", "Trailer", true, "2024-01-15")
        );
        when(tmdbClient.getMovieVideos(anyLong())).thenReturn(TestDataFactory.createTmdbVideoResponse(videos));

        MediaDetailsDto result = mediaDetailsService.getMovieDetails(1L);

        List<VideoDto> sorted = result.getVideos();
        assertEquals("v2", sorted.get(0).getId());
        assertEquals("v3", sorted.get(1).getId());
        assertEquals("v1", sorted.get(2).getId());
    }
}
