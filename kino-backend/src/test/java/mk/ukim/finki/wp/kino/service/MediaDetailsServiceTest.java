package mk.ukim.finki.wp.kino.service;

import mk.ukim.finki.wp.kino.dto.api.MediaType;
import mk.ukim.finki.wp.kino.dto.tmdb.TmdbMovieDto;
import mk.ukim.finki.wp.kino.dto.tmdb.TmdbPagedResponse;
import mk.ukim.finki.wp.kino.dto.tmdb.TmdbTvDto;
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
import static org.mockito.ArgumentMatchers.anyInt;
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
        when(tmdbClient.getMovieKeywords(anyLong())).thenReturn(null);
        when(tmdbClient.getMovieRecommendations(anyLong(), anyInt())).thenReturn(null);
        when(tmdbClient.getMovieSimilar(anyLong(), anyInt())).thenReturn(null);

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
        when(tmdbClient.getMovieKeywords(anyLong())).thenReturn(null);
        when(tmdbClient.getMovieRecommendations(anyLong(), anyInt())).thenReturn(null);
        when(tmdbClient.getMovieSimilar(anyLong(), anyInt())).thenReturn(null);

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
        when(tmdbClient.getMovieKeywords(anyLong())).thenReturn(null);
        when(tmdbClient.getMovieRecommendations(anyLong(), anyInt())).thenReturn(null);
        when(tmdbClient.getMovieSimilar(anyLong(), anyInt())).thenReturn(null);

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
        when(tmdbClient.getMovieKeywords(anyLong())).thenReturn(null);
        when(tmdbClient.getMovieRecommendations(anyLong(), anyInt())).thenReturn(null);
        when(tmdbClient.getMovieSimilar(anyLong(), anyInt())).thenReturn(null);

        MediaDetailsDto result = mediaDetailsService.getMovieDetails(1L);

        assertNotNull(result.getVideos());
        assertTrue(result.getVideos().isEmpty());
    }

    @Test
    void getMovieDetails_nullResultsReturnsEmptyList() {
        when(tmdbClient.getMovieDetails(anyLong())).thenReturn(TestDataFactory.createMovieDetails());
        when(tmdbClient.getMovieCredits(anyLong())).thenReturn(TestDataFactory.createCredits());
        when(tmdbClient.getMovieKeywords(anyLong())).thenReturn(null);
        when(tmdbClient.getMovieRecommendations(anyLong(), anyInt())).thenReturn(null);
        when(tmdbClient.getMovieSimilar(anyLong(), anyInt())).thenReturn(null);

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
        when(tmdbClient.getTvKeywords(anyLong())).thenReturn(null);
        when(tmdbClient.getTvRecommendations(anyLong(), anyInt())).thenReturn(null);
        when(tmdbClient.getTvSimilar(anyLong(), anyInt())).thenReturn(null);

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
        when(tmdbClient.getMovieKeywords(anyLong())).thenReturn(null);
        when(tmdbClient.getMovieRecommendations(anyLong(), anyInt())).thenReturn(null);
        when(tmdbClient.getMovieSimilar(anyLong(), anyInt())).thenReturn(null);

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

    @Test
    void getMovieDetails_populatesKeywords() {
        when(tmdbClient.getMovieDetails(anyLong())).thenReturn(TestDataFactory.createMovieDetails());
        when(tmdbClient.getMovieCredits(anyLong())).thenReturn(TestDataFactory.createCredits());
        when(tmdbClient.getMovieVideos(anyLong())).thenReturn(null);
        when(tmdbClient.getMovieRecommendations(anyLong(), anyInt())).thenReturn(null);
        when(tmdbClient.getMovieSimilar(anyLong(), anyInt())).thenReturn(null);
        when(tmdbClient.getMovieKeywords(anyLong())).thenReturn(
                TestDataFactory.createTmdbMovieKeywords(List.of(
                        TestDataFactory.createKeyword(1L, "superhero"),
                        TestDataFactory.createKeyword(2L, "  blank-name  ")
                ))
        );

        MediaDetailsDto result = mediaDetailsService.getMovieDetails(1L);

        assertEquals(2, result.getKeywords().size());
        assertEquals("superhero", result.getKeywords().get(0).getName());
        assertEquals("blank-name", result.getKeywords().get(1).getName());
    }

    @Test
    void getTvDetails_populatesKeywordsUsingResultsField() {
        when(tmdbClient.getTvDetails(anyLong())).thenReturn(TestDataFactory.createTvDetails());
        when(tmdbClient.getTvCredits(anyLong())).thenReturn(TestDataFactory.createCredits());
        when(tmdbClient.getTvVideos(anyLong())).thenReturn(null);
        when(tmdbClient.getTvRecommendations(anyLong(), anyInt())).thenReturn(null);
        when(tmdbClient.getTvSimilar(anyLong(), anyInt())).thenReturn(null);
        when(tmdbClient.getTvKeywords(anyLong())).thenReturn(
                TestDataFactory.createTmdbTvKeywords(List.of(TestDataFactory.createKeyword(3L, "drama")))
        );

        MediaDetailsDto result = mediaDetailsService.getTvDetails(1L);

        assertEquals(1, result.getKeywords().size());
        assertEquals("drama", result.getKeywords().get(0).getName());
    }

    @Test
    void getMovieDetails_combinesRecommendationsAndSimilarAndCapsAt8() {
        when(tmdbClient.getMovieDetails(anyLong())).thenReturn(TestDataFactory.createMovieDetails());
        when(tmdbClient.getMovieCredits(anyLong())).thenReturn(TestDataFactory.createCredits());
        when(tmdbClient.getMovieVideos(anyLong())).thenReturn(null);
        when(tmdbClient.getMovieKeywords(anyLong())).thenReturn(null);

        List<TmdbMovieDto> recs = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            recs.add(TestDataFactory.createMovie(100L + i, "Rec " + i, "/p" + i + ".jpg"));
        }
        List<TmdbMovieDto> sims = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            sims.add(TestDataFactory.createMovie(200L + i, "Sim " + i, "/s" + i + ".jpg"));
        }

        when(tmdbClient.getMovieRecommendations(anyLong(), anyInt())).thenReturn(
                TestDataFactory.createPagedResponse(recs)
        );
        when(tmdbClient.getMovieSimilar(anyLong(), anyInt())).thenReturn(
                TestDataFactory.createPagedResponse(sims)
        );

        MediaDetailsDto result = mediaDetailsService.getMovieDetails(1L);

        assertEquals(8, result.getRecommendations().size());
        assertTrue(result.getRecommendations().stream()
                .allMatch(r -> r.getMediaType() == MediaType.MOVIE));
    }

    @Test
    void getMovieDetails_deduplicatesRecommendationsAndSimilar() {
        when(tmdbClient.getMovieDetails(anyLong())).thenReturn(TestDataFactory.createMovieDetails());
        when(tmdbClient.getMovieCredits(anyLong())).thenReturn(TestDataFactory.createCredits());
        when(tmdbClient.getMovieVideos(anyLong())).thenReturn(null);
        when(tmdbClient.getMovieKeywords(anyLong())).thenReturn(null);

        TmdbMovieDto shared = TestDataFactory.createMovie(999L, "Shared", "/shared.jpg");
        List<TmdbMovieDto> recs = List.of(shared);
        List<TmdbMovieDto> sims = List.of(shared);

        when(tmdbClient.getMovieRecommendations(anyLong(), anyInt())).thenReturn(
                TestDataFactory.createPagedResponse(recs)
        );
        when(tmdbClient.getMovieSimilar(anyLong(), anyInt())).thenReturn(
                TestDataFactory.createPagedResponse(sims)
        );

        MediaDetailsDto result = mediaDetailsService.getMovieDetails(1L);

        assertEquals(1, result.getRecommendations().size());
        assertEquals(999L, result.getRecommendations().get(0).getId());
    }

    @Test
    void getMovieDetails_nonCriticalFailuresReturnEmptyLists() {
        when(tmdbClient.getMovieDetails(anyLong())).thenReturn(TestDataFactory.createMovieDetails());
        when(tmdbClient.getMovieCredits(anyLong())).thenReturn(TestDataFactory.createCredits());
        when(tmdbClient.getMovieVideos(anyLong())).thenReturn(null);
        when(tmdbClient.getMovieKeywords(anyLong())).thenThrow(new RuntimeException("TMDB down"));
        when(tmdbClient.getMovieRecommendations(anyLong(), anyInt())).thenThrow(new RuntimeException("TMDB down"));
        when(tmdbClient.getMovieSimilar(anyLong(), anyInt())).thenThrow(new RuntimeException("TMDB down"));

        MediaDetailsDto result = mediaDetailsService.getMovieDetails(1L);

        assertNotNull(result);
        assertTrue(result.getKeywords().isEmpty());
        assertTrue(result.getRecommendations().isEmpty());
    }
}
