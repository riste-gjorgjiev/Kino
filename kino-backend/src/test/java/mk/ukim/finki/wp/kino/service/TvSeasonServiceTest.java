package mk.ukim.finki.wp.kino.service;

import mk.ukim.finki.wp.kino.dto.api.EpisodeDto;
import mk.ukim.finki.wp.kino.dto.api.SeasonDetailsDto;
import mk.ukim.finki.wp.kino.dto.api.SeasonDto;
import mk.ukim.finki.wp.kino.dto.tmdb.details.TmdbTvDetailsDto;
import mk.ukim.finki.wp.kino.dto.tmdb.details.misc.TmdbEpisodeDto;
import mk.ukim.finki.wp.kino.tmdb.TmdbClient;
import mk.ukim.finki.wp.kino.util.ImageUrlBuilder;
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
class TvSeasonServiceTest {

    @Mock
    private TmdbClient tmdbClient;

    private TvSeasonService tvSeasonService;

    @BeforeEach
    void setUp() {
        tvSeasonService = new TvSeasonService(
                tmdbClient,
                new ImageUrlBuilder("https://image.tmdb.org/t/p/w500")
        );
    }

    @Test
    void getSeasons_mapsAndSortsBySeasonNumber() {
        when(tmdbClient.getTvDetails(anyLong())).thenReturn(TestDataFactory.createTvDetails());

        List<SeasonDto> result = tvSeasonService.getSeasons(1399L);

        assertEquals(3, result.size());
        assertEquals(List.of(0, 1, 2), result.stream().map(SeasonDto::getSeasonNumber).toList());
        assertEquals("Specials", result.get(0).getName());
        assertEquals("https://image.tmdb.org/t/p/w500/season-1.jpg", result.get(1).getPosterUrl());
        assertEquals(2, result.get(1).getEpisodeCount());
    }

    @Test
    void getSeasons_whenTvHasNoSeasons_returnsEmptyList() {
        TmdbTvDetailsDto tv = TestDataFactory.createTvDetails();
        tv.setSeasons(null);
        when(tmdbClient.getTvDetails(anyLong())).thenReturn(tv);

        assertTrue(tvSeasonService.getSeasons(1399L).isEmpty());
    }

    @Test
    void getSeasons_whenResponseIsNull_returnsEmptyList() {
        when(tmdbClient.getTvDetails(anyLong())).thenReturn(null);

        assertTrue(tvSeasonService.getSeasons(1399L).isEmpty());
    }

    @Test
    void getSeasonDetails_mapsEpisodesSortedByEpisodeNumber() {
        TmdbEpisodeDto first = TestDataFactory.createEpisode(1, 2, "Second", "/second.jpg");
        TmdbEpisodeDto second = TestDataFactory.createEpisode(1, 1, "First", "/first.jpg");
        when(tmdbClient.getTvSeasonDetails(anyLong(), anyInt())).thenReturn(
                TestDataFactory.createSeasonDetails(1, new ArrayList<>(List.of(first, second)))
        );

        SeasonDetailsDto result = tvSeasonService.getSeasonDetails(1399L, 1);

        assertEquals(1, result.getSeasonNumber());
        assertEquals("Season 1", result.getName());
        assertEquals("https://image.tmdb.org/t/p/w500/season-1.jpg", result.getPosterUrl());
        assertEquals(2, result.getEpisodeCount());
        assertEquals(List.of(1, 2), result.getEpisodes().stream().map(EpisodeDto::getEpisodeNumber).toList());
        assertEquals("https://image.tmdb.org/t/p/w500/first.jpg", result.getEpisodes().get(0).getStillUrl());
        assertEquals(45, result.getEpisodes().get(0).getRuntimeMinutes());
        assertEquals(8.0, result.getEpisodes().get(0).getRating());
        assertEquals(120, result.getEpisodes().get(0).getVoteCount());
    }

    @Test
    void getSeasonDetails_withNullResponse_returnsEmptySeason() {
        when(tmdbClient.getTvSeasonDetails(anyLong(), anyInt())).thenReturn(null);

        SeasonDetailsDto result = tvSeasonService.getSeasonDetails(1399L, 3);

        assertEquals(3, result.getSeasonNumber());
        assertNull(result.getName());
        assertEquals(0, result.getEpisodeCount());
        assertNotNull(result.getEpisodes());
        assertTrue(result.getEpisodes().isEmpty());
    }

    @Test
    void getEpisodes_returnsSeasonEpisodes() {
        TmdbEpisodeDto episode = TestDataFactory.createEpisode(2, 1, "Pilot", "/pilot.jpg");
        when(tmdbClient.getTvSeasonDetails(anyLong(), anyInt())).thenReturn(
                TestDataFactory.createSeasonDetails(2, List.of(episode))
        );

        List<EpisodeDto> result = tvSeasonService.getEpisodes(1399L, 2);

        assertEquals(1, result.size());
        assertEquals("Pilot", result.get(0).getName());
        assertEquals(2, result.get(0).getSeasonNumber());
    }
}
