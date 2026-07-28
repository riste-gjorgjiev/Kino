package mk.ukim.finki.wp.kino.service;

import mk.ukim.finki.wp.kino.dto.api.MediaCardDto;
import mk.ukim.finki.wp.kino.dto.api.MediaFilterDto;
import mk.ukim.finki.wp.kino.dto.api.MediaType;
import mk.ukim.finki.wp.kino.dto.api.PagedResponseDto;
import mk.ukim.finki.wp.kino.dto.tmdb.TmdbMovieDto;
import mk.ukim.finki.wp.kino.dto.tmdb.TmdbPagedResponse;
import mk.ukim.finki.wp.kino.tmdb.TmdbClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MovieService {
    private final TmdbClient tmdbClient;
    private final String imageBaseUrl;
    private final MediaFilterService mediaFilterService;

    public MovieService(TmdbClient tmdbClient, @Value("${tmdb.image-base-url}") String imageBaseUrl, MediaFilterService mediaFilterService) {
        this.tmdbClient = tmdbClient;
        this.imageBaseUrl = imageBaseUrl;
        this.mediaFilterService = mediaFilterService;
    }

    @Cacheable(cacheNames = "trendingMovies", key = "#window + ':' + #page + ':filter=' + #filter.hashCode()")
    public PagedResponseDto<MediaCardDto> getTrendingMovies(String window, int page, MediaFilterDto filter){
        TmdbPagedResponse<TmdbMovieDto> tmdb = tmdbClient.getTrendingMovies(window, page);

        List<MediaCardDto> items = tmdb.getResults().stream()
                .map(this::toMediaCard)
                .toList();

        int pageSize = 20;
        List<MediaCardDto> filteredItems = mediaFilterService.applyFiltersAndSort(
                items, filter, 1, pageSize
        );
        int totalResults = mediaFilterService.countAfterFilters(items, filter);
        int totalPages = (int) Math.ceil((double) totalResults / pageSize);

        return new PagedResponseDto<>(
                page,
                totalPages,
                filteredItems,
                totalResults
        );
    }
    @Cacheable(cacheNames = "popularMovies", key = "'page=' + #page + ':filter=' + #filter.hashCode()")
    public PagedResponseDto<MediaCardDto> getPopularMovies(int page, MediaFilterDto filter){
        TmdbPagedResponse<TmdbMovieDto> tmdb = tmdbClient.getPopularMovies(page);

        List<MediaCardDto> items = tmdb.getResults().stream()
                .map(this::toMediaCard)
                .toList();

        int pageSize = 20;
        List<MediaCardDto> filteredItems = mediaFilterService.applyFiltersAndSort(
                items, filter, 1, pageSize
        );
        int totalResults = mediaFilterService.countAfterFilters(items, filter);
        int totalPages = (int) Math.ceil((double) totalResults / pageSize);

        return new PagedResponseDto<>(
                page,
                totalPages,
                filteredItems,
                totalResults
        );
    }
    @Cacheable(cacheNames = "topRatedMovies", key = "'page=' + #page + ':filter=' + #filter.hashCode()")
    public PagedResponseDto<MediaCardDto> getTopRatedMovies(int page, MediaFilterDto filter){
        TmdbPagedResponse<TmdbMovieDto> tmdb = tmdbClient.getTopRatedMovies(page);

        List<MediaCardDto> items = tmdb.getResults().stream()
                .map(this::toMediaCard)
                .toList();

        int pageSize = 20;
        List<MediaCardDto> filteredItems = mediaFilterService.applyFiltersAndSort(
                items, filter, 1, pageSize
        );
        int totalResults = mediaFilterService.countAfterFilters(items, filter);
        int totalPages = (int) Math.ceil((double) totalResults / pageSize);

        return new PagedResponseDto<>(
                page,
                totalPages,
                filteredItems,
                totalResults
        );
    }

    @Cacheable(cacheNames = "upcomingMovies", key = "'page=' + #page + ':filter=' + #filter.hashCode()")
    public PagedResponseDto<MediaCardDto> getUpcomingMovies(int page, MediaFilterDto filter){
        TmdbPagedResponse<TmdbMovieDto> tmdb = tmdbClient.getUpcomingMovies(page);

        List<MediaCardDto> items = tmdb.getResults().stream()
                .map(this::toMediaCard)
                .toList();

        int pageSize = 20;
        List<MediaCardDto> filteredItems = mediaFilterService.applyFiltersAndSort(
                items, filter, 1, pageSize
        );
        int totalResults = mediaFilterService.countAfterFilters(items, filter);
        int totalPages = (int) Math.ceil((double) totalResults / pageSize);

        return new PagedResponseDto<>(
                page,
                totalPages,
                filteredItems,
                totalResults
        );
    }



    private MediaCardDto toMediaCard(TmdbMovieDto m){
        return new MediaCardDto(
                m.getId(),
                MediaType.MOVIE,
                m.getTitle(),
                fullImageUrl(m.getPosterPath()),
                m.getVoteAverage(),
                m.getReleaseDate()
        );
    }

    private String fullImageUrl(String path){
        if (path == null || path.isBlank()) return null;
        return imageBaseUrl + path;
    }
}
