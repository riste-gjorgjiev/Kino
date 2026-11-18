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
    private static final int PAGE_SIZE = 20;

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
        return paginate(tmdbClient.getTrendingTvShows(window, page), page, filter);
    }

    @Cacheable(cacheNames = "popularTv", key = "'page=' + #page + ':filter=' + #filter.hashCode()")
    public PagedResponseDto<MediaCardDto> getPopularTvShows(int page, MediaFilterDto filter){
        return paginate(tmdbClient.getPopularTvShows(page), page, filter);
    }

    @Cacheable(cacheNames = "topRatedTv", key = "'page=' + #page + ':filter=' + #filter.hashCode()")
    public PagedResponseDto<MediaCardDto> getTopRatedTvShows(int page, MediaFilterDto filter){
        return paginate(tmdbClient.getTopRatedTvShows(page), page, filter);
    }

    @Cacheable(cacheNames = "airingTvShows", key = "'page=' + #page + ':filter=' + #filter.hashCode()")
    public PagedResponseDto<MediaCardDto> getAiringTvShows(int page, MediaFilterDto filter){
        return paginate(tmdbClient.getAiringTvShows(page), page, filter);
    }

    private PagedResponseDto<MediaCardDto> paginate(TmdbPagedResponse<TmdbTvDto> tmdb, int page, MediaFilterDto filter) {
        List<MediaCardDto> items = tmdb.getResults().stream()
                .map(this::toMediaCard)
                .toList();
        return mediaFilterService.paginate(items, filter, page, PAGE_SIZE);
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
