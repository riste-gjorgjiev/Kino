package mk.ukim.finki.wp.kino.controller;

import mk.ukim.finki.wp.kino.dto.api.MediaCardDto;
import mk.ukim.finki.wp.kino.dto.api.MediaFilterDto;
import mk.ukim.finki.wp.kino.dto.api.MediaType;
import mk.ukim.finki.wp.kino.dto.api.PagedResponseDto;
import mk.ukim.finki.wp.kino.dto.tmdb.details.MediaDetailsDto;
import mk.ukim.finki.wp.kino.service.MediaDetailsService;
import mk.ukim.finki.wp.kino.service.TvService;
import mk.ukim.finki.wp.kino.util.TestDataFactory;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.cache.CacheManager;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TvController.class)
class TvControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TvService tvService;

    @MockitoBean
    private MediaDetailsService mediaDetailsService;

    @MockitoBean
    private CacheManager cacheManager;

    @Test
    void trending_withDefaultParams_callsServiceWithDayAndPage1() throws Exception {
        MediaCardDto card = new MediaCardDto(2L, MediaType.TV, "Test TV Show", "/poster.jpg", 8.2, "2024-04-20");
        PagedResponseDto<MediaCardDto> response = new PagedResponseDto<>(1, 5, List.of(card), 100);

        when(tvService.getTrendingTvShows(eq("day"), eq(1), any(MediaFilterDto.class))).thenReturn(response);

        mockMvc.perform(get("/api/tv/trending"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.page").value(1))
            .andExpect(jsonPath("$.items[0].title").value("Test TV Show"))
            .andExpect(jsonPath("$.items[0].mediaType").value("TV"));

        verify(tvService).getTrendingTvShows(eq("day"), eq(1), any(MediaFilterDto.class));
    }

    @Test
    void trending_withCustomParams_passesToService() throws Exception {
        PagedResponseDto<MediaCardDto> response = new PagedResponseDto<>(2, 10, List.of(), 200);

        when(tvService.getTrendingTvShows(eq("week"), eq(2), any(MediaFilterDto.class))).thenReturn(response);

        mockMvc.perform(get("/api/tv/trending")
                .param("window", "week")
                .param("page", "2"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.page").value(2));

        verify(tvService).getTrendingTvShows(eq("week"), eq(2), any(MediaFilterDto.class));
    }

    @Test
    void popular_withDefaultPage_callsServiceWithPage1() throws Exception {
        PagedResponseDto<MediaCardDto> response = new PagedResponseDto<>(1, 5, List.of(), 100);

        when(tvService.getPopularTvShows(eq(1), any(MediaFilterDto.class))).thenReturn(response);

        mockMvc.perform(get("/api/tv/popular"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.page").value(1));

        verify(tvService).getPopularTvShows(eq(1), any(MediaFilterDto.class));
    }

    @Test
    void popular_withCustomPage_passesToService() throws Exception {
        PagedResponseDto<MediaCardDto> response = new PagedResponseDto<>(3, 10, List.of(), 300);

        when(tvService.getPopularTvShows(eq(3), any(MediaFilterDto.class))).thenReturn(response);

        mockMvc.perform(get("/api/tv/popular").param("page", "3"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.page").value(3));

        verify(tvService).getPopularTvShows(eq(3), any(MediaFilterDto.class));
    }

    @Test
    void topRated_withDefaultPage_callsServiceWithPage1() throws Exception {
        PagedResponseDto<MediaCardDto> response = new PagedResponseDto<>(1, 5, List.of(), 100);

        when(tvService.getTopRatedTvShows(eq(1), any(MediaFilterDto.class))).thenReturn(response);

        mockMvc.perform(get("/api/tv/top-rated"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.page").value(1));

        verify(tvService).getTopRatedTvShows(eq(1), any(MediaFilterDto.class));
    }

    @Test
    void topRated_withCustomPage_passesToService() throws Exception {
        PagedResponseDto<MediaCardDto> response = new PagedResponseDto<>(2, 8, List.of(), 200);

        when(tvService.getTopRatedTvShows(eq(2), any(MediaFilterDto.class))).thenReturn(response);

        mockMvc.perform(get("/api/tv/top-rated").param("page", "2"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.page").value(2));

        verify(tvService).getTopRatedTvShows(eq(2), any(MediaFilterDto.class));
    }

    @Test
    void onTheAir_withDefaultPage_callsServiceWithPage1() throws Exception {
        PagedResponseDto<MediaCardDto> response = new PagedResponseDto<>(1, 5, List.of(), 100);

        when(tvService.getAiringTvShows(eq(1), any(MediaFilterDto.class))).thenReturn(response);

        mockMvc.perform(get("/api/tv/on-the-air"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.page").value(1));

        verify(tvService).getAiringTvShows(eq(1), any(MediaFilterDto.class));
    }

    @Test
    void onTheAir_withCustomPage_passesToService() throws Exception {
        PagedResponseDto<MediaCardDto> response = new PagedResponseDto<>(4, 12, List.of(), 400);

        when(tvService.getAiringTvShows(eq(4), any(MediaFilterDto.class))).thenReturn(response);

        mockMvc.perform(get("/api/tv/on-the-air").param("page", "4"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.page").value(4));

        verify(tvService).getAiringTvShows(eq(4), any(MediaFilterDto.class));
    }

    @Test
    void details_callsMediaDetailsServiceWithId() throws Exception {
        MediaDetailsDto details = TestDataFactory.createTvDetailsDto();

        when(mediaDetailsService.getTvDetails(1399L)).thenReturn(details);

        mockMvc.perform(get("/api/tv/1399"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(2))
            .andExpect(jsonPath("$.title").value("Test TV Show"))
            .andExpect(jsonPath("$.mediaType").value("TV"));

        verify(mediaDetailsService).getTvDetails(1399L);
    }
}
