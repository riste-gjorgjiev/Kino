package mk.ukim.finki.wp.kino.controller;

import mk.ukim.finki.wp.kino.dto.api.MediaCardDto;
import mk.ukim.finki.wp.kino.dto.api.MediaFilterDto;
import mk.ukim.finki.wp.kino.dto.api.MediaType;
import mk.ukim.finki.wp.kino.dto.api.PagedResponseDto;
import mk.ukim.finki.wp.kino.service.SearchService;
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

@WebMvcTest(SearchController.class)
class SearchControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private SearchService searchService;

    @MockitoBean
    private CacheManager cacheManager;

    @Test
    void searchMovie_withQuery_callsService() throws Exception {
        MediaCardDto card = new MediaCardDto(1L, MediaType.MOVIE, "Batman", "/batman.jpg", 7.5, "2024-01-15");
        PagedResponseDto<MediaCardDto> response = new PagedResponseDto<>(1, 5, List.of(card), 100);

        when(searchService.searchMovies(eq("batman"), eq(1), any(MediaFilterDto.class))).thenReturn(response);

        mockMvc.perform(get("/api/search/movie").param("query", "batman"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.page").value(1))
            .andExpect(jsonPath("$.items[0].title").value("Batman"))
            .andExpect(jsonPath("$.items[0].mediaType").value("MOVIE"));

        verify(searchService).searchMovies(eq("batman"), eq(1), any(MediaFilterDto.class));
    }

    @Test
    void searchMovie_withCustomPage_passesToService() throws Exception {
        PagedResponseDto<MediaCardDto> response = new PagedResponseDto<>(2, 10, List.of(), 200);

        when(searchService.searchMovies(eq("test"), eq(2), any(MediaFilterDto.class))).thenReturn(response);

        mockMvc.perform(get("/api/search/movie")
                .param("query", "test")
                .param("page", "2"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.page").value(2));

        verify(searchService).searchMovies(eq("test"), eq(2), any(MediaFilterDto.class));
    }

    @Test
    void searchMovie_withoutQuery_returnsBadRequest() throws Exception {
        mockMvc.perform(get("/api/search/movie"))
            .andExpect(status().isBadRequest());

        verifyNoInteractions(searchService);
    }

    @Test
    void searchTv_withQuery_callsService() throws Exception {
        MediaCardDto card = new MediaCardDto(2L, MediaType.TV, "Breaking Bad", "/bb.jpg", 9.5, "2008-01-20");
        PagedResponseDto<MediaCardDto> response = new PagedResponseDto<>(1, 5, List.of(card), 100);

        when(searchService.searchTv(eq("breaking bad"), eq(1), any(MediaFilterDto.class))).thenReturn(response);

        mockMvc.perform(get("/api/search/tv").param("query", "breaking bad"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.page").value(1))
            .andExpect(jsonPath("$.items[0].title").value("Breaking Bad"))
            .andExpect(jsonPath("$.items[0].mediaType").value("TV"));

        verify(searchService).searchTv(eq("breaking bad"), eq(1), any(MediaFilterDto.class));
    }

    @Test
    void searchTv_withCustomPage_passesToService() throws Exception {
        PagedResponseDto<MediaCardDto> response = new PagedResponseDto<>(3, 8, List.of(), 300);

        when(searchService.searchTv(eq("test"), eq(3), any(MediaFilterDto.class))).thenReturn(response);

        mockMvc.perform(get("/api/search/tv")
                .param("query", "test")
                .param("page", "3"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.page").value(3));

        verify(searchService).searchTv(eq("test"), eq(3), any(MediaFilterDto.class));
    }

    @Test
    void searchTv_withoutQuery_returnsBadRequest() throws Exception {
        mockMvc.perform(get("/api/search/tv"))
            .andExpect(status().isBadRequest());

        verifyNoInteractions(searchService);
    }

    @Test
    void searchAll_withQuery_callsService() throws Exception {
        MediaCardDto movie = new MediaCardDto(1L, MediaType.MOVIE, "Star Wars", "/sw.jpg", 8.5, "1977-05-25");
        MediaCardDto tv = new MediaCardDto(2L, MediaType.TV, "The Mandalorian", "/mando.jpg", 8.7, "2019-11-12");
        PagedResponseDto<MediaCardDto> response = new PagedResponseDto<>(1, 5, List.of(movie, tv), 200);

        when(searchService.searchMulti(eq("star wars"), eq(1), any(MediaFilterDto.class))).thenReturn(response);

        mockMvc.perform(get("/api/search/all").param("query", "star wars"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.page").value(1))
            .andExpect(jsonPath("$.items[0].title").value("Star Wars"))
            .andExpect(jsonPath("$.items[0].mediaType").value("MOVIE"))
            .andExpect(jsonPath("$.items[1].title").value("The Mandalorian"))
            .andExpect(jsonPath("$.items[1].mediaType").value("TV"));

        verify(searchService).searchMulti(eq("star wars"), eq(1), any(MediaFilterDto.class));
    }

    @Test
    void searchAll_withCustomPage_passesToService() throws Exception {
        PagedResponseDto<MediaCardDto> response = new PagedResponseDto<>(4, 12, List.of(), 400);

        when(searchService.searchMulti(eq("test"), eq(4), any(MediaFilterDto.class))).thenReturn(response);

        mockMvc.perform(get("/api/search/all")
                .param("query", "test")
                .param("page", "4"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.page").value(4));

        verify(searchService).searchMulti(eq("test"), eq(4), any(MediaFilterDto.class));
    }

    @Test
    void searchAll_withoutQuery_returnsBadRequest() throws Exception {
        mockMvc.perform(get("/api/search/all"))
            .andExpect(status().isBadRequest());

        verifyNoInteractions(searchService);
    }
}
