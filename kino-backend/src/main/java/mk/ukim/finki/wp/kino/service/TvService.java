package mk.ukim.finki.wp.kino.service;

import mk.ukim.finki.wp.kino.dto.api.MediaCardDto;
import mk.ukim.finki.wp.kino.dto.api.MediaFilterDto;
import mk.ukim.finki.wp.kino.dto.api.PagedResponseDto;
import mk.ukim.finki.wp.kino.dto.tmdb.TmdbPagedResponse;
import mk.ukim.finki.wp.kino.dto.tmdb.TmdbTvDto;
import mk.ukim.finki.wp.kino.tmdb.TmdbClient;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class TvService {

    private final TmdbClient tmdbClient;
    private final MediaCardMapper mediaCardMapper;
    private final MediaFilterService mediaFilterService;

    public TvService(TmdbClient tmdbClient, MediaCardMapper mediaCardMapper, MediaFilterService mediaFilterService) {
        this.tmdbClient = tmdbClient;
        this.mediaCardMapper = mediaCardMapper;
        this.mediaFilterService = mediaFilterService;
    }

    @Cacheable(cacheNames = "trendingTv", key = "#window + ':' + #page + ':filter=' + #filter.hashCode()")
    public PagedResponseDto<MediaCardDto> getTrendingTvShows(String window, int page, MediaFilterDto filter){
        return toPage(tmdbClient.getTrendingTvShows(window, page), filter);
    }

    @Cacheable(cacheNames = "popularTv", key = "'page=' + #page + ':filter=' + #filter.hashCode()")
    public PagedResponseDto<MediaCardDto> getPopularTvShows(int page, MediaFilterDto filter){
        return toPage(tmdbClient.getPopularTvShows(page), filter);
    }

    @Cacheable(cacheNames = "topRatedTv", key = "'page=' + #page + ':filter=' + #filter.hashCode()")
    public PagedResponseDto<MediaCardDto> getTopRatedTvShows(int page, MediaFilterDto filter){
        return toPage(tmdbClient.getTopRatedTvShows(page), filter);
    }

    @Cacheable(cacheNames = "airingTvShows", key = "'page=' + #page + ':filter=' + #filter.hashCode()")
    public PagedResponseDto<MediaCardDto> getAiringTvShows(int page, MediaFilterDto filter){
        return toPage(tmdbClient.getAiringTvShows(page), filter);
    }

    private PagedResponseDto<MediaCardDto> toPage(TmdbPagedResponse<TmdbTvDto> tmdb, MediaFilterDto filter) {
        List<MediaCardDto> items = tmdb.getResults() == null
                ? List.of()
                : tmdb.getResults().stream().map(mediaCardMapper::toCard).toList();
        return mediaFilterService.toPage(
                items,
                filter,
                Math.max(tmdb.getPage(), 1),
                tmdb.getTotalPages(),
                tmdb.getTotalResults()
        );
    }
}
