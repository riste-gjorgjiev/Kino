package mk.ukim.finki.wp.kino.service;

import mk.ukim.finki.wp.kino.dto.api.MediaCardDto;
import mk.ukim.finki.wp.kino.dto.api.MediaFilterDto;
import mk.ukim.finki.wp.kino.dto.api.PagedResponseDto;
import mk.ukim.finki.wp.kino.dto.tmdb.TmdbMovieDto;
import mk.ukim.finki.wp.kino.dto.tmdb.TmdbPagedResponse;
import mk.ukim.finki.wp.kino.tmdb.TmdbClient;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MovieService {

    private final TmdbClient tmdbClient;
    private final MediaCardMapper mediaCardMapper;
    private final MediaFilterService mediaFilterService;

    public MovieService(TmdbClient tmdbClient, MediaCardMapper mediaCardMapper, MediaFilterService mediaFilterService) {
        this.tmdbClient = tmdbClient;
        this.mediaCardMapper = mediaCardMapper;
        this.mediaFilterService = mediaFilterService;
    }

    @Cacheable(cacheNames = "trendingMovies", key = "#window + ':' + #page + ':filter=' + #filter.hashCode()")
    public PagedResponseDto<MediaCardDto> getTrendingMovies(String window, int page, MediaFilterDto filter){
        return toPage(tmdbClient.getTrendingMovies(window, page), filter);
    }

    @Cacheable(cacheNames = "popularMovies", key = "'page=' + #page + ':filter=' + #filter.hashCode()")
    public PagedResponseDto<MediaCardDto> getPopularMovies(int page, MediaFilterDto filter){
        return toPage(tmdbClient.getPopularMovies(page), filter);
    }

    @Cacheable(cacheNames = "topRatedMovies", key = "'page=' + #page + ':filter=' + #filter.hashCode()")
    public PagedResponseDto<MediaCardDto> getTopRatedMovies(int page, MediaFilterDto filter){
        return toPage(tmdbClient.getTopRatedMovies(page), filter);
    }

    @Cacheable(cacheNames = "upcomingMovies", key = "'page=' + #page + ':filter=' + #filter.hashCode()")
    public PagedResponseDto<MediaCardDto> getUpcomingMovies(int page, MediaFilterDto filter){
        return toPage(tmdbClient.getUpcomingMovies(page), filter);
    }

    private PagedResponseDto<MediaCardDto> toPage(TmdbPagedResponse<TmdbMovieDto> tmdb, MediaFilterDto filter) {
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
