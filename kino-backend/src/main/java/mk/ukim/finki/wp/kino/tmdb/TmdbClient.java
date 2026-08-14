package mk.ukim.finki.wp.kino.tmdb;

import mk.ukim.finki.wp.kino.dto.tmdb.TmdbMovieDto;
import mk.ukim.finki.wp.kino.dto.tmdb.TmdbMultiSearchDto;
import mk.ukim.finki.wp.kino.dto.tmdb.TmdbPagedResponse;
import mk.ukim.finki.wp.kino.dto.tmdb.TmdbTvDto;
import mk.ukim.finki.wp.kino.dto.tmdb.details.TmdbMovieDetailsDto;
import mk.ukim.finki.wp.kino.dto.tmdb.details.TmdbTvDetailsDto;
import mk.ukim.finki.wp.kino.dto.tmdb.details.misc.TmdbCreditsDto;
import mk.ukim.finki.wp.kino.dto.tmdb.details.misc.TmdbKeywordsDto;
import mk.ukim.finki.wp.kino.dto.tmdb.details.misc.TmdbVideoResponseDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class TmdbClient {
    private static final ParameterizedTypeReference<TmdbPagedResponse<TmdbMovieDto>> MOVIE_PAGE_TYPE =
            new ParameterizedTypeReference<>() {};
    private static final ParameterizedTypeReference<TmdbPagedResponse<TmdbTvDto>> TV_PAGE_TYPE =
            new ParameterizedTypeReference<>() {};
    private static final ParameterizedTypeReference<TmdbPagedResponse<TmdbMultiSearchDto>> TMDB_MULTI_SEARCH =
            new ParameterizedTypeReference<>() {};
    private static final ParameterizedTypeReference<TmdbVideoResponseDto> TMDB_VIDEO_RESPONSE =
            new ParameterizedTypeReference<>() {};
    private static final ParameterizedTypeReference<TmdbKeywordsDto> TMDB_KEYWORDS_TYPE =
            new ParameterizedTypeReference<>() {};

    private final RestClient restClient;
    private final String apiKey;
    private final String language;

    public TmdbClient(RestClient restClient, @Value("${tmdb.api-key}") String apiKey, @Value("${tmdb.language}") String language) {
        this.restClient = restClient;
        this.apiKey = apiKey;
        this.language = language;
    }

    public TmdbPagedResponse<TmdbMovieDto> getTrendingMovies(String window, int page){
        if (window == null || (!window.equals("day") && !window.equals("week"))){
            window = "day";
        }
        if (page < 1) page = 1;

        int finalPage = page;
        String finalWindow = window;
        return restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/trending/movie/{window}")
                        .queryParam("api_key", apiKey)
                        .queryParam("language", language)
                        .queryParam("page", finalPage)
                        .build(finalWindow))
                .retrieve()
                .body(MOVIE_PAGE_TYPE);
    }
    public TmdbPagedResponse<TmdbTvDto> getTrendingTvShows(String window, int page){
        if (window == null || (!window.equals("day") && !window.equals("week"))){
            window = "day";
        }
        if (page < 1) page = 1;

        int finalPage = page;
        String finalWindow = window;
        return restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/trending/tv/{window}")
                        .queryParam("api_key", apiKey)
                        .queryParam("language", language)
                        .queryParam("page", finalPage)
                        .build(finalWindow))
                .retrieve()
                .body(TV_PAGE_TYPE);
    }

    public TmdbPagedResponse<TmdbMovieDto> getPopularMovies(int page){
        if (page < 1) page = 1;

        int finalPage = page;
        return restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/movie/popular")
                        .queryParam("api_key", apiKey)
                        .queryParam("language", language)
                        .queryParam("page", finalPage)
                        .build())
                .retrieve()
                .body(MOVIE_PAGE_TYPE);
    }
    public TmdbPagedResponse<TmdbTvDto> getPopularTvShows(int page){
        if (page < 1) page = 1;

        int finalPage = page;
        return restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/tv/popular")
                        .queryParam("api_key", apiKey)
                        .queryParam("language", language)
                        .queryParam("page", finalPage)
                        .build())
                .retrieve()
                .body(TV_PAGE_TYPE);
    }

    public TmdbPagedResponse<TmdbMovieDto> getTopRatedMovies(int page){
        if (page < 1) page = 1;

        int finalPage = page;
        return restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/movie/top_rated")
                        .queryParam("api_key", apiKey)
                        .queryParam("language", language)
                        .queryParam("page", finalPage)
                        .build())
                .retrieve()
                .body(MOVIE_PAGE_TYPE);
    }

    public TmdbPagedResponse<TmdbTvDto> getTopRatedTvShows(int page){
        if (page < 1) page = 1;

        int finalPage = page;
        return restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/tv/top_rated")
                        .queryParam("api_key", apiKey)
                        .queryParam("language", language)
                        .queryParam("page", finalPage)
                        .build())
                .retrieve()
                .body(TV_PAGE_TYPE);
    }

    public TmdbPagedResponse<TmdbMovieDto> getUpcomingMovies(int page){
        if (page < 1) page = 1;

        int finalPage = page;
        return restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/movie/upcoming")
                        .queryParam("api_key", apiKey)
                        .queryParam("language", language)
                        .queryParam("page", finalPage)
                        .build())
                .retrieve()
                .body(MOVIE_PAGE_TYPE);
    }

    public TmdbPagedResponse<TmdbTvDto> getAiringTvShows(int page){
        if (page < 1) page = 1;

        int finalPage = page;
        return restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/tv/on_the_air")
                        .queryParam("api_key", apiKey)
                        .queryParam("language", language)
                        .queryParam("page", finalPage)
                        .build())
                .retrieve()
                .body(TV_PAGE_TYPE);
    }



    public TmdbPagedResponse<TmdbMovieDto> searchMovies(String query, int page){
        if (page < 1) page = 1;

        int finalPage = page;
        return restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/search/movie")
                        .queryParam("api_key", apiKey)
                        .queryParam("language", language)
                        .queryParam("query", query)
                        .queryParam("page", finalPage)
                        .build())
                .retrieve()
                .body(MOVIE_PAGE_TYPE);
    }

    public TmdbPagedResponse<TmdbTvDto> searchTvShows(String query, int page){
        if (page < 1) page = 1;

        int finalPage = page;
        return restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/search/tv")
                        .queryParam("api_key", apiKey)
                        .queryParam("language", language)
                        .queryParam("query", query)
                        .queryParam("page", finalPage)
                        .build())
                .retrieve()
                .body(TV_PAGE_TYPE);
    }

    public TmdbPagedResponse<TmdbMultiSearchDto> searchMulti(String query, int page){
        if (page < 1) page = 1;

        int finalPage = page;
        return restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/search/multi")
                        .queryParam("api_key", apiKey)
                        .queryParam("language", language)
                        .queryParam("query", query)
                        .queryParam("page", finalPage)
                        .build())
                .retrieve()
                .body(TMDB_MULTI_SEARCH);
    }

    public TmdbMovieDetailsDto getMovieDetails(long id){
        return restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/movie/{id}")
                        .queryParam("api_key", apiKey)
                        .queryParam("language", language)
                        .build(id))
                .retrieve()
                .body(TmdbMovieDetailsDto.class);
    }

    public TmdbTvDetailsDto getTvDetails(long id){
        return restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/tv/{id}")
                        .queryParam("api_key", apiKey)
                        .queryParam("language", language)
                        .build(id))
                .retrieve()
                .body(TmdbTvDetailsDto.class);
    }
    public TmdbCreditsDto getMovieCredits(long id){
        return restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/movie/{id}/credits")
                        .queryParam("api_key", apiKey)
                        .queryParam("language", language)
                        .build(id))
                .retrieve()
                .body(TmdbCreditsDto.class);
    }
    public TmdbCreditsDto getTvCredits(long id){
        return restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/tv/{id}/credits")
                        .queryParam("api_key", apiKey)
                        .queryParam("language", language)
                        .build(id))
                .retrieve()
                .body(TmdbCreditsDto.class);
    }
    public TmdbVideoResponseDto getMovieVideos(long id){
        return restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/movie/{id}/videos")
                        .queryParam("api_key", apiKey)
                        .queryParam("language", language)
                        .build(id))
                .retrieve()
                .body(TMDB_VIDEO_RESPONSE);
    }
    public TmdbVideoResponseDto getTvVideos(long id){
        return restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/tv/{id}/videos")
                        .queryParam("api_key", apiKey)
                        .queryParam("language", language)
                        .build(id))
                .retrieve()
                .body(TMDB_VIDEO_RESPONSE);
    }

    public TmdbKeywordsDto getMovieKeywords(long id){
        return restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/movie/{id}/keywords")
                        .queryParam("api_key", apiKey)
                        .queryParam("language", language)
                        .build(id))
                .retrieve()
                .body(TMDB_KEYWORDS_TYPE);
    }

    public TmdbKeywordsDto getTvKeywords(long id){
        return restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/tv/{id}/keywords")
                        .queryParam("api_key", apiKey)
                        .queryParam("language", language)
                        .build(id))
                .retrieve()
                .body(TMDB_KEYWORDS_TYPE);
    }

    public TmdbPagedResponse<TmdbMovieDto> getMovieRecommendations(long id, int page){
        if (page < 1) page = 1;
        int finalPage = page;
        return restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/movie/{id}/recommendations")
                        .queryParam("api_key", apiKey)
                        .queryParam("language", language)
                        .queryParam("page", finalPage)
                        .build(id))
                .retrieve()
                .body(MOVIE_PAGE_TYPE);
    }

    public TmdbPagedResponse<TmdbMovieDto> getMovieSimilar(long id, int page){
        if (page < 1) page = 1;
        int finalPage = page;
        return restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/movie/{id}/similar")
                        .queryParam("api_key", apiKey)
                        .queryParam("language", language)
                        .queryParam("page", finalPage)
                        .build(id))
                .retrieve()
                .body(MOVIE_PAGE_TYPE);
    }

    public TmdbPagedResponse<TmdbTvDto> getTvRecommendations(long id, int page){
        if (page < 1) page = 1;
        int finalPage = page;
        return restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/tv/{id}/recommendations")
                        .queryParam("api_key", apiKey)
                        .queryParam("language", language)
                        .queryParam("page", finalPage)
                        .build(id))
                .retrieve()
                .body(TV_PAGE_TYPE);
    }

    public TmdbPagedResponse<TmdbTvDto> getTvSimilar(long id, int page){
        if (page < 1) page = 1;
        int finalPage = page;
        return restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/tv/{id}/similar")
                        .queryParam("api_key", apiKey)
                        .queryParam("language", language)
                        .queryParam("page", finalPage)
                        .build(id))
                .retrieve()
                .body(TV_PAGE_TYPE);
    }
}
