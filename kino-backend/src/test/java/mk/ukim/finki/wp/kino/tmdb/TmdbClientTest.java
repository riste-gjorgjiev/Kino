package mk.ukim.finki.wp.kino.tmdb;

import tools.jackson.databind.ObjectMapper;
import mk.ukim.finki.wp.kino.dto.tmdb.*;
import mk.ukim.finki.wp.kino.dto.tmdb.details.*;
import mk.ukim.finki.wp.kino.dto.tmdb.details.misc.*;
import mk.ukim.finki.wp.kino.util.TestDataFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpRequest;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.test.web.client.RequestMatcher;
import org.springframework.web.client.RestClient;

import java.net.URI;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.queryParam;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

class TmdbClientTest {

    private MockRestServiceServer mockServer;
    private TmdbClient tmdbClient;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        RestClient.Builder builder = RestClient.builder();
        mockServer = MockRestServiceServer.bindTo(builder).build();
        RestClient restClient = builder.baseUrl("https://api.themoviedb.org/3").build();
        tmdbClient = new TmdbClient(restClient, "test-api-key", "en-US");
        objectMapper = new ObjectMapper();
    }

    private RequestMatcher requestToPath(String expectedPath) {
        return (ClientHttpRequest request) -> {
            URI uri = request.getURI();
            String actualPath = uri.getPath();
            assertEquals(expectedPath, actualPath,
                "Expected path: " + expectedPath + " but was: " + actualPath);
        };
    }

    // ===== Trending Movies =====

    @Test
    void getTrendingMovies_withValidWindowAndPage_callsCorrectUrl() throws Exception {
        TmdbPagedResponse<TmdbMovieDto> expected = TestDataFactory.createPagedResponse(
            List.of(TestDataFactory.createMovie())
        );
        String json = objectMapper.writeValueAsString(expected);

        mockServer.expect(requestToPath("/3/trending/movie/day"))
            .andExpect(queryParam("api_key", "test-api-key"))
            .andExpect(queryParam("language", "en-US"))
            .andExpect(queryParam("page", "1"))
            .andRespond(withSuccess(json, MediaType.APPLICATION_JSON));

        TmdbPagedResponse<TmdbMovieDto> result = tmdbClient.getTrendingMovies("day", 1, new MediaFilterDto());

        assertNotNull(result);
        assertEquals(1, result.getPage());
        assertEquals(1, result.getResults().size());
        assertEquals("Test Movie", result.getResults().get(0).getTitle());
        mockServer.verify();
    }

    @Test
    void getTrendingMovies_withInvalidWindow_defaultsToDay() throws Exception {
        TmdbPagedResponse<TmdbMovieDto> expected = TestDataFactory.createPagedResponse(List.of());
        String json = objectMapper.writeValueAsString(expected);

        mockServer.expect(requestToPath("/3/trending/movie/day"))
            .andRespond(withSuccess(json, MediaType.APPLICATION_JSON));

        tmdbClient.getTrendingMovies("invalid", 1, new MediaFilterDto());
        mockServer.verify();
    }

    @Test
    void getTrendingMovies_withNullWindow_defaultsToDay() throws Exception {
        TmdbPagedResponse<TmdbMovieDto> expected = TestDataFactory.createPagedResponse(List.of());
        String json = objectMapper.writeValueAsString(expected);

        mockServer.expect(requestToPath("/3/trending/movie/day"))
            .andRespond(withSuccess(json, MediaType.APPLICATION_JSON));

        tmdbClient.getTrendingMovies(null, 1, new MediaFilterDto());
        mockServer.verify();
    }

    @Test
    void getTrendingMovies_withPageLessThanOne_defaultsToOne() throws Exception {
        TmdbPagedResponse<TmdbMovieDto> expected = TestDataFactory.createPagedResponse(List.of());
        String json = objectMapper.writeValueAsString(expected);

        mockServer.expect(requestToPath("/3/trending/movie/day"))
            .andExpect(queryParam("page", "1"))
            .andRespond(withSuccess(json, MediaType.APPLICATION_JSON));

        tmdbClient.getTrendingMovies("day", 0, new MediaFilterDto());
        mockServer.verify();
    }

    @Test
    void getTrendingMovies_withWeekWindow_callsCorrectUrl() throws Exception {
        TmdbPagedResponse<TmdbMovieDto> expected = TestDataFactory.createPagedResponse(List.of());
        String json = objectMapper.writeValueAsString(expected);

        mockServer.expect(requestToPath("/3/trending/movie/week"))
            .andExpect(queryParam("page", "2"))
            .andRespond(withSuccess(json, MediaType.APPLICATION_JSON));

        tmdbClient.getTrendingMovies("week", 2, new MediaFilterDto());
        mockServer.verify();
    }

    // ===== Popular Movies =====

    @Test
    void getPopularMovies_callsCorrectUrl() throws Exception {
        TmdbPagedResponse<TmdbMovieDto> expected = TestDataFactory.createPagedResponse(
            List.of(TestDataFactory.createMovie())
        );
        String json = objectMapper.writeValueAsString(expected);

        mockServer.expect(requestToPath("/3/movie/popular"))
            .andExpect(queryParam("api_key", "test-api-key"))
            .andExpect(queryParam("language", "en-US"))
            .andExpect(queryParam("page", "1"))
            .andRespond(withSuccess(json, MediaType.APPLICATION_JSON));

        TmdbPagedResponse<TmdbMovieDto> result = tmdbClient.getPopularMovies(1, new MediaFilterDto());

        assertNotNull(result);
        assertEquals(1, result.getResults().size());
        mockServer.verify();
    }

    @Test
    void getPopularMovies_withPageLessThanOne_defaultsToOne() throws Exception {
        TmdbPagedResponse<TmdbMovieDto> expected = TestDataFactory.createPagedResponse(List.of());
        String json = objectMapper.writeValueAsString(expected);

        mockServer.expect(requestToPath("/3/movie/popular"))
            .andExpect(queryParam("page", "1"))
            .andRespond(withSuccess(json, MediaType.APPLICATION_JSON));

        tmdbClient.getPopularMovies(-5, new MediaFilterDto());
        mockServer.verify();
    }

    // ===== Top Rated Movies =====

    @Test
    void getTopRatedMovies_callsCorrectUrl() throws Exception {
        TmdbPagedResponse<TmdbMovieDto> expected = TestDataFactory.createPagedResponse(List.of());
        String json = objectMapper.writeValueAsString(expected);

        mockServer.expect(requestToPath("/3/movie/top_rated"))
            .andExpect(queryParam("page", "3"))
            .andRespond(withSuccess(json, MediaType.APPLICATION_JSON));

        tmdbClient.getTopRatedMovies(3, new MediaFilterDto());
        mockServer.verify();
    }

    @Test
    void getTopRatedMovies_withPageLessThanOne_defaultsToOne() throws Exception {
        TmdbPagedResponse<TmdbMovieDto> expected = TestDataFactory.createPagedResponse(List.of());
        String json = objectMapper.writeValueAsString(expected);

        mockServer.expect(requestToPath("/3/movie/top_rated"))
            .andExpect(queryParam("page", "1"))
            .andRespond(withSuccess(json, MediaType.APPLICATION_JSON));

        tmdbClient.getTopRatedMovies(0, new MediaFilterDto());
        mockServer.verify();
    }

    // ===== Upcoming Movies =====

    @Test
    void getUpcomingMovies_callsCorrectUrl() throws Exception {
        TmdbPagedResponse<TmdbMovieDto> expected = TestDataFactory.createPagedResponse(List.of());
        String json = objectMapper.writeValueAsString(expected);

        mockServer.expect(requestToPath("/3/movie/upcoming"))
            .andExpect(queryParam("page", "1"))
            .andRespond(withSuccess(json, MediaType.APPLICATION_JSON));

        tmdbClient.getUpcomingMovies(1, new MediaFilterDto());
        mockServer.verify();
    }

    @Test
    void getUpcomingMovies_withPageLessThanOne_defaultsToOne() throws Exception {
        TmdbPagedResponse<TmdbMovieDto> expected = TestDataFactory.createPagedResponse(List.of());
        String json = objectMapper.writeValueAsString(expected);

        mockServer.expect(requestToPath("/3/movie/upcoming"))
            .andExpect(queryParam("page", "1"))
            .andRespond(withSuccess(json, MediaType.APPLICATION_JSON));

        tmdbClient.getUpcomingMovies(-1, new MediaFilterDto());
        mockServer.verify();
    }

    // ===== Trending TV Shows =====

    @Test
    void getTrendingTvShows_withValidWindowAndPage_callsCorrectUrl() throws Exception {
        TmdbPagedResponse<TmdbTvDto> expected = TestDataFactory.createPagedResponse(
            List.of(TestDataFactory.createTvShow())
        );
        String json = objectMapper.writeValueAsString(expected);

        mockServer.expect(requestToPath("/3/trending/tv/day"))
            .andExpect(queryParam("api_key", "test-api-key"))
            .andExpect(queryParam("language", "en-US"))
            .andExpect(queryParam("page", "1"))
            .andRespond(withSuccess(json, MediaType.APPLICATION_JSON));

        TmdbPagedResponse<TmdbTvDto> result = tmdbClient.getTrendingTvShows("day", 1, new MediaFilterDto());

        assertNotNull(result);
        assertEquals(1, result.getResults().size());
        assertEquals("Test TV Show", result.getResults().get(0).getName());
        mockServer.verify();
    }

    @Test
    void getTrendingTvShows_withInvalidWindow_defaultsToDay() throws Exception {
        TmdbPagedResponse<TmdbTvDto> expected = TestDataFactory.createPagedResponse(List.of());
        String json = objectMapper.writeValueAsString(expected);

        mockServer.expect(requestToPath("/3/trending/tv/day"))
            .andRespond(withSuccess(json, MediaType.APPLICATION_JSON));

        tmdbClient.getTrendingTvShows("bad", 1, new MediaFilterDto());
        mockServer.verify();
    }

    @Test
    void getTrendingTvShows_withNullWindow_defaultsToDay() throws Exception {
        TmdbPagedResponse<TmdbTvDto> expected = TestDataFactory.createPagedResponse(List.of());
        String json = objectMapper.writeValueAsString(expected);

        mockServer.expect(requestToPath("/3/trending/tv/day"))
            .andRespond(withSuccess(json, MediaType.APPLICATION_JSON));

        tmdbClient.getTrendingTvShows(null, 1, new MediaFilterDto());
        mockServer.verify();
    }

    @Test
    void getTrendingTvShows_withPageLessThanOne_defaultsToOne() throws Exception {
        TmdbPagedResponse<TmdbTvDto> expected = TestDataFactory.createPagedResponse(List.of());
        String json = objectMapper.writeValueAsString(expected);

        mockServer.expect(requestToPath("/3/trending/tv/day"))
            .andExpect(queryParam("page", "1"))
            .andRespond(withSuccess(json, MediaType.APPLICATION_JSON));

        tmdbClient.getTrendingTvShows("day", 0, new MediaFilterDto());
        mockServer.verify();
    }

    @Test
    void getTrendingTvShows_withWeekWindow_callsCorrectUrl() throws Exception {
        TmdbPagedResponse<TmdbTvDto> expected = TestDataFactory.createPagedResponse(List.of());
        String json = objectMapper.writeValueAsString(expected);

        mockServer.expect(requestToPath("/3/trending/tv/week"))
            .andExpect(queryParam("page", "2"))
            .andRespond(withSuccess(json, MediaType.APPLICATION_JSON));

        tmdbClient.getTrendingTvShows("week", 2, new MediaFilterDto());
        mockServer.verify();
    }

    // ===== Popular TV Shows =====

    @Test
    void getPopularTvShows_callsCorrectUrl() throws Exception {
        TmdbPagedResponse<TmdbTvDto> expected = TestDataFactory.createPagedResponse(List.of());
        String json = objectMapper.writeValueAsString(expected);

        mockServer.expect(requestToPath("/3/tv/popular"))
            .andExpect(queryParam("page", "1"))
            .andRespond(withSuccess(json, MediaType.APPLICATION_JSON));

        tmdbClient.getPopularTvShows(1, new MediaFilterDto());
        mockServer.verify();
    }

    @Test
    void getPopularTvShows_withPageLessThanOne_defaultsToOne() throws Exception {
        TmdbPagedResponse<TmdbTvDto> expected = TestDataFactory.createPagedResponse(List.of());
        String json = objectMapper.writeValueAsString(expected);

        mockServer.expect(requestToPath("/3/tv/popular"))
            .andExpect(queryParam("page", "1"))
            .andRespond(withSuccess(json, MediaType.APPLICATION_JSON));

        tmdbClient.getPopularTvShows(-10, new MediaFilterDto());
        mockServer.verify();
    }

    // ===== Top Rated TV Shows =====

    @Test
    void getTopRatedTvShows_callsCorrectUrl() throws Exception {
        TmdbPagedResponse<TmdbTvDto> expected = TestDataFactory.createPagedResponse(List.of());
        String json = objectMapper.writeValueAsString(expected);

        mockServer.expect(requestToPath("/3/tv/top_rated"))
            .andExpect(queryParam("page", "4"))
            .andRespond(withSuccess(json, MediaType.APPLICATION_JSON));

        tmdbClient.getTopRatedTvShows(4, new MediaFilterDto());
        mockServer.verify();
    }

    @Test
    void getTopRatedTvShows_withPageLessThanOne_defaultsToOne() throws Exception {
        TmdbPagedResponse<TmdbTvDto> expected = TestDataFactory.createPagedResponse(List.of());
        String json = objectMapper.writeValueAsString(expected);

        mockServer.expect(requestToPath("/3/tv/top_rated"))
            .andExpect(queryParam("page", "1"))
            .andRespond(withSuccess(json, MediaType.APPLICATION_JSON));

        tmdbClient.getTopRatedTvShows(0, new MediaFilterDto());
        mockServer.verify();
    }

    // ===== Airing TV Shows =====

    @Test
    void getAiringTvShows_callsCorrectUrl() throws Exception {
        TmdbPagedResponse<TmdbTvDto> expected = TestDataFactory.createPagedResponse(List.of());
        String json = objectMapper.writeValueAsString(expected);

        mockServer.expect(requestToPath("/3/tv/on_the_air"))
            .andExpect(queryParam("page", "1"))
            .andRespond(withSuccess(json, MediaType.APPLICATION_JSON));

        tmdbClient.getAiringTvShows(1, new MediaFilterDto());
        mockServer.verify();
    }

    @Test
    void getAiringTvShows_withPageLessThanOne_defaultsToOne() throws Exception {
        TmdbPagedResponse<TmdbTvDto> expected = TestDataFactory.createPagedResponse(List.of());
        String json = objectMapper.writeValueAsString(expected);

        mockServer.expect(requestToPath("/3/tv/on_the_air"))
            .andExpect(queryParam("page", "1"))
            .andRespond(withSuccess(json, MediaType.APPLICATION_JSON));

        tmdbClient.getAiringTvShows(-1, new MediaFilterDto());
        mockServer.verify();
    }

    // ===== Search Movies =====

    @Test
    void searchMovies_callsCorrectUrlWithQuery() throws Exception {
        TmdbPagedResponse<TmdbMovieDto> expected = TestDataFactory.createPagedResponse(
            List.of(TestDataFactory.createMovie())
        );
        String json = objectMapper.writeValueAsString(expected);

        mockServer.expect(requestToPath("/3/search/movie"))
            .andExpect(queryParam("api_key", "test-api-key"))
            .andExpect(queryParam("language", "en-US"))
            .andExpect(queryParam("query", "batman"))
            .andExpect(queryParam("page", "1"))
            .andRespond(withSuccess(json, MediaType.APPLICATION_JSON));

        TmdbPagedResponse<TmdbMovieDto> result = tmdbClient.searchMovies("batman", 1, new MediaFilterDto());

        assertNotNull(result);
        assertEquals(1, result.getResults().size());
        mockServer.verify();
    }

    @Test
    void searchMovies_withPageLessThanOne_defaultsToOne() throws Exception {
        TmdbPagedResponse<TmdbMovieDto> expected = TestDataFactory.createPagedResponse(List.of());
        String json = objectMapper.writeValueAsString(expected);

        mockServer.expect(requestToPath("/3/search/movie"))
            .andExpect(queryParam("page", "1"))
            .andRespond(withSuccess(json, MediaType.APPLICATION_JSON));

        tmdbClient.searchMovies("test", 0, new MediaFilterDto());
        mockServer.verify();
    }

    // ===== Search TV Shows =====

    @Test
    void searchTvShows_callsCorrectUrlWithQuery() throws Exception {
        TmdbPagedResponse<TmdbTvDto> expected = TestDataFactory.createPagedResponse(
            List.of(TestDataFactory.createTvShow())
        );
        String json = objectMapper.writeValueAsString(expected);

        mockServer.expect(requestToPath("/3/search/tv"))
            .andExpect(queryParam("api_key", "test-api-key"))
            .andExpect(queryParam("language", "en-US"))
            .andExpect(queryParam("query", "breaking%20bad"))
            .andExpect(queryParam("page", "1"))
            .andRespond(withSuccess(json, MediaType.APPLICATION_JSON));

        TmdbPagedResponse<TmdbTvDto> result = tmdbClient.searchTvShows("breaking bad", 1);

        assertNotNull(result);
        assertEquals(1, result.getResults().size());
        mockServer.verify();
    }

    @Test
    void searchTvShows_withPageLessThanOne_defaultsToOne() throws Exception {
        TmdbPagedResponse<TmdbTvDto> expected = TestDataFactory.createPagedResponse(List.of());
        String json = objectMapper.writeValueAsString(expected);

        mockServer.expect(requestToPath("/3/search/tv"))
            .andExpect(queryParam("page", "1"))
            .andRespond(withSuccess(json, MediaType.APPLICATION_JSON));

        tmdbClient.searchTvShows("test", -5);
        mockServer.verify();
    }

    // ===== Search Multi =====

    @Test
    void searchMulti_callsCorrectUrlWithQuery() throws Exception {
        TmdbPagedResponse<TmdbMultiSearchDto> expected = TestDataFactory.createPagedResponse(
            List.of(TestDataFactory.createMultiSearchMovie(), TestDataFactory.createMultiSearchTv())
        );
        String json = objectMapper.writeValueAsString(expected);

        mockServer.expect(requestToPath("/3/search/multi"))
            .andExpect(queryParam("api_key", "test-api-key"))
            .andExpect(queryParam("language", "en-US"))
            .andExpect(queryParam("query", "star%20wars"))
            .andExpect(queryParam("page", "1"))
            .andRespond(withSuccess(json, MediaType.APPLICATION_JSON));

        TmdbPagedResponse<TmdbMultiSearchDto> result = tmdbClient.searchMulti("star wars", 1, new MediaFilterDto());

        assertNotNull(result);
        assertEquals(2, result.getResults().size());
        mockServer.verify();
    }

    @Test
    void searchMulti_withPageLessThanOne_defaultsToOne() throws Exception {
        TmdbPagedResponse<TmdbMultiSearchDto> expected = TestDataFactory.createPagedResponse(List.of());
        String json = objectMapper.writeValueAsString(expected);

        mockServer.expect(requestToPath("/3/search/multi"))
            .andExpect(queryParam("page", "1"))
            .andRespond(withSuccess(json, MediaType.APPLICATION_JSON));

        tmdbClient.searchMulti("test", 0, new MediaFilterDto());
        mockServer.verify();
    }

    // ===== Movie Details =====

    @Test
    void getMovieDetails_callsCorrectUrl() throws Exception {
        TmdbMovieDetailsDto expected = TestDataFactory.createMovieDetails();
        String json = objectMapper.writeValueAsString(expected);

        mockServer.expect(requestToPath("/3/movie/550"))
            .andExpect(queryParam("api_key", "test-api-key"))
            .andExpect(queryParam("language", "en-US"))
            .andRespond(withSuccess(json, MediaType.APPLICATION_JSON));

        TmdbMovieDetailsDto result = tmdbClient.getMovieDetails(550);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Test Movie", result.getTitle());
        mockServer.verify();
    }

    // ===== TV Details =====

    @Test
    void getTvDetails_callsCorrectUrl() throws Exception {
        TmdbTvDetailsDto expected = TestDataFactory.createTvDetails();
        String json = objectMapper.writeValueAsString(expected);

        mockServer.expect(requestToPath("/3/tv/1399"))
            .andExpect(queryParam("api_key", "test-api-key"))
            .andExpect(queryParam("language", "en-US"))
            .andRespond(withSuccess(json, MediaType.APPLICATION_JSON));

        TmdbTvDetailsDto result = tmdbClient.getTvDetails(1399);

        assertNotNull(result);
        assertEquals(2L, result.getId());
        assertEquals("Test TV Show", result.getName());
        mockServer.verify();
    }

    // ===== Movie Credits =====

    @Test
    void getMovieCredits_callsCorrectUrl() throws Exception {
        TmdbCreditsDto expected = TestDataFactory.createCredits();
        String json = objectMapper.writeValueAsString(expected);

        mockServer.expect(requestToPath("/3/movie/550/credits"))
            .andExpect(queryParam("api_key", "test-api-key"))
            .andExpect(queryParam("language", "en-US"))
            .andRespond(withSuccess(json, MediaType.APPLICATION_JSON));

        TmdbCreditsDto result = tmdbClient.getMovieCredits(550);

        assertNotNull(result);
        assertEquals(2, result.getCast().size());
        assertEquals(1, result.getCrew().size());
        mockServer.verify();
    }

    // ===== TV Credits =====

    @Test
    void getTvCredits_callsCorrectUrl() throws Exception {
        TmdbCreditsDto expected = TestDataFactory.createCredits();
        String json = objectMapper.writeValueAsString(expected);

        mockServer.expect(requestToPath("/3/tv/1399/credits"))
            .andExpect(queryParam("api_key", "test-api-key"))
            .andExpect(queryParam("language", "en-US"))
            .andRespond(withSuccess(json, MediaType.APPLICATION_JSON));

        TmdbCreditsDto result = tmdbClient.getTvCredits(1399);

        assertNotNull(result);
        assertEquals(2, result.getCast().size());
        mockServer.verify();
    }

    // ===== Response Deserialization =====

    @Test
    void getPopularMovies_deserializesResponseCorrectly() throws Exception {
        TmdbMovieDto movie1 = TestDataFactory.createMovie(1L, "Movie One", "/p1.jpg");
        TmdbMovieDto movie2 = TestDataFactory.createMovie(2L, "Movie Two", "/p2.jpg");
        TmdbPagedResponse<TmdbMovieDto> expected = TestDataFactory.createPagedResponse(
            List.of(movie1, movie2), 1, 5, 100
        );
        String json = objectMapper.writeValueAsString(expected);

        mockServer.expect(requestToPath("/3/movie/popular"))
            .andRespond(withSuccess(json, MediaType.APPLICATION_JSON));

        TmdbPagedResponse<TmdbMovieDto> result = tmdbClient.getPopularMovies(1, new MediaFilterDto());

        assertEquals(1, result.getPage());
        assertEquals(5, result.getTotalPages());
        assertEquals(100, result.getTotalResults());
        assertEquals(2, result.getResults().size());
        assertEquals("Movie One", result.getResults().get(0).getTitle());
        assertEquals("Movie Two", result.getResults().get(1).getTitle());
        assertEquals(7.5, result.getResults().get(0).getVoteAverage());
        mockServer.verify();
    }
}
