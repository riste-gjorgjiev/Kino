package mk.ukim.finki.wp.kino.controller;

import mk.ukim.finki.wp.kino.dto.api.MediaCardDto;
import mk.ukim.finki.wp.kino.dto.api.MediaType;
import mk.ukim.finki.wp.kino.dto.api.PagedResponseDto;
import mk.ukim.finki.wp.kino.dto.api.MediaFilterDto;
import mk.ukim.finki.wp.kino.dto.tmdb.details.MediaDetailsDto;
import mk.ukim.finki.wp.kino.service.MediaDetailsService;
import mk.ukim.finki.wp.kino.service.MovieService;
import mk.ukim.finki.wp.kino.util.TestDataFactory;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.cache.CacheManager;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(MovieController.class)
class MovieControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private MovieService movieService;

    @MockitoBean
    private CacheManager cacheManager;

    @MockitoBean
    private MediaDetailsService mediaDetailsService;

    @Test
    void trending_withDefaultParams_callsServiceWithDayAndPage1() throws Exception {
        MediaCardDto card = new MediaCardDto(1L, MediaType.MOVIE, "Test Movie", "/poster.jpg", 7.5, "2024-01-15");
        PagedResponseDto<MediaCardDto> response = new PagedResponseDto<>(1, 5, List.of(card), 100);

        when(movieService.getTrendingMovies("day", 1, new MediaFilterDto())).thenReturn(response);

        mockMvc.perform(get("/api/movies/trending"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.page").value(1))
            .andExpect(jsonPath("$.totalPages").value(5))
            .andExpect(jsonPath("$.items[0].title").value("Test Movie"))
            .andExpect(jsonPath("$.items[0].mediaType").value("MOVIE"));

        verify(movieService).getTrendingMovies("day", 1, new MediaFilterDto());
    }

    @Test
    void trending_withCustomParams_passesToService() throws Exception {
        PagedResponseDto<MediaCardDto> response = new PagedResponseDto<>(2, 10, List.of(), 200);

        when(movieService.getTrendingMovies("week", 2, new MediaFilterDto())).thenReturn(response);

        mockMvc.perform(get("/api/movies/trending")
                .param("window", "week")
                .param("page", "2"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.page").value(2));

        verify(movieService).getTrendingMovies("week", 2, new MediaFilterDto());
    }

    @Test
    void popular_withDefaultPage_callsServiceWithPage1() throws Exception {
        PagedResponseDto<MediaCardDto> response = new PagedResponseDto<>(1, 5, List.of(), 100);

        when(movieService.getPopularMovies(1, new MediaFilterDto())).thenReturn(response);

        mockMvc.perform(get("/api/movies/popular"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.page").value(1));

        verify(movieService).getPopularMovies(1, new MediaFilterDto());
    }

    @Test
    void popular_withCustomPage_passesToService() throws Exception {
        PagedResponseDto<MediaCardDto> response = new PagedResponseDto<>(3, 10, List.of(), 300);

        when(movieService.getPopularMovies(3, new MediaFilterDto())).thenReturn(response);

        mockMvc.perform(get("/api/movies/popular").param("page", "3"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.page").value(3));

        verify(movieService).getPopularMovies(3, new MediaFilterDto());
    }

    @Test
    void topRated_withDefaultPage_callsServiceWithPage1() throws Exception {
        PagedResponseDto<MediaCardDto> response = new PagedResponseDto<>(1, 5, List.of(), 100);

        when(movieService.getTopRatedMovies(1, new MediaFilterDto())).thenReturn(response);

        mockMvc.perform(get("/api/movies/top-rated"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.page").value(1));

        verify(movieService).getTopRatedMovies(1, new MediaFilterDto());
    }

    @Test
    void topRated_withCustomPage_passesToService() throws Exception {
        PagedResponseDto<MediaCardDto> response = new PagedResponseDto<>(2, 8, List.of(), 200);

        when(movieService.getTopRatedMovies(2, new MediaFilterDto())).thenReturn(response);

        mockMvc.perform(get("/api/movies/top-rated").param("page", "2"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.page").value(2));

        verify(movieService).getTopRatedMovies(2, new MediaFilterDto());
    }

    @Test
    void upcoming_withDefaultPage_callsServiceWithPage1() throws Exception {
        PagedResponseDto<MediaCardDto> response = new PagedResponseDto<>(1, 5, List.of(), 100);

        when(movieService.getUpcomingMovies(1, new MediaFilterDto())).thenReturn(response);

        mockMvc.perform(get("/api/movies/upcoming"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.page").value(1));

        verify(movieService).getUpcomingMovies(1, new MediaFilterDto());
    }

    @Test
    void upcoming_withCustomPage_passesToService() throws Exception {
        PagedResponseDto<MediaCardDto> response = new PagedResponseDto<>(4, 12, List.of(), 400);

        when(movieService.getUpcomingMovies(4, new MediaFilterDto())).thenReturn(response);

        mockMvc.perform(get("/api/movies/upcoming").param("page", "4"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.page").value(4));

        verify(movieService).getUpcomingMovies(4, new MediaFilterDto());
    }

    @Test
    void details_callsMediaDetailsServiceWithId() throws Exception {
        MediaDetailsDto details = TestDataFactory.createMovieDetailsDto();

        when(mediaDetailsService.getMovieDetails(550L)).thenReturn(details);

        mockMvc.perform(get("/api/movies/550"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.title").value("Test Movie"))
            .andExpect(jsonPath("$.mediaType").value("MOVIE"));

        verify(mediaDetailsService).getMovieDetails(550L);
    }
}
