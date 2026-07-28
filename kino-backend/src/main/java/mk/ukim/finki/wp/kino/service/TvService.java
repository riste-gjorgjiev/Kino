package mk.ukim.finki.wp.kino.service;

import mk.ukim.finki.wp.kino.dto.api.MediaCardDto;
import mk.ukim.finki.wp.kino.dto.api.MediaFilterDto;
import mk.ukim.finki.wp.kino.dto.api.MediaType;
import mk.ukim.finki.wp.kino.dto.api.PagedResponseDto;
import mk.ukim.finki.wp.kino.dto.tmdb.TmdbPagedResponse;
import mk.ukim.finki.wp.kino.dto.tmdb.TmdbTvDto;
import mk.ukim.finki.wp.kino.tmdb.TmdbClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class TvService {
    private final TmdbClient tmdbClient;
    private final String imageBaseUrl;
    private final MediaFilterService mediaFilterService;

    public TvService(TmdbClient tmdbClient, @Value("${tmdb.image-base-url}") String imageBaseUrl, MediaFilterService mediaFilterService) {
        this.tmdbClient = tmdbClient;
        this.imageBaseUrl = imageBaseUrl;
        this.mediaFilterService = mediaFilterService;
    }
    @Cacheable(cacheNames = "trendingTv", key = "#window + ':' + #page + ':filter=' + #filter.hashCode()")
    public PagedResponseDto<MediaCardDto> getTrendingTvShows(String window, int page, MediaFilterDto filter){
        TmdbPagedResponse<TmdbTvDto> tmdb = tmdbClient.getTrendingTvShows(window, page);

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
    @Cacheable(cacheNames = "popularTv", key = "'page=' + #page + ':filter=' + #filter.hashCode()")
    public PagedResponseDto<MediaCardDto> getPopularTvShows(int page, MediaFilterDto filter){
        TmdbPagedResponse<TmdbTvDto> tmdb = tmdbClient.getPopularTvShows(page);

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
    @Cacheable(cacheNames = "topRatedTv", key = "'page=' + #page + ':filter=' + #filter.hashCode()")
    public PagedResponseDto<MediaCardDto> getTopRatedTvShows(int page, MediaFilterDto filter){
        TmdbPagedResponse<TmdbTvDto> tmdb = tmdbClient.getTopRatedTvShows(page);

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
    @Cacheable(cacheNames = "airingTvShows", key = "'page=' + #page + ':filter=' + #filter.hashCode()")
    public PagedResponseDto<MediaCardDto> getAiringTvShows(int page, MediaFilterDto filter){
        TmdbPagedResponse<TmdbTvDto> tmdb = tmdbClient.getAiringTvShows(page);

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

    private MediaCardDto toMediaCard(TmdbTvDto t){
        return new MediaCardDto(
                t.getId(),
                MediaType.TV,
                t.getName(),
                fullImageUrl(t.getPosterPath()),
                t.getVoteAverage(),
                t.getFirstAirDate()
        );
    }

    private String fullImageUrl(String path){
        if (path == null || path.isBlank()) return null;
        return imageBaseUrl + path;
    }
}
