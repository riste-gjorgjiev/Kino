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
    private static final int PAGE_SIZE = 20;

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
        return paginate(tmdbClient.getTrendingMovies(window, page), page, filter);
    }

    @Cacheable(cacheNames = "popularMovies", key = "'page=' + #page + ':filter=' + #filter.hashCode()")
    public PagedResponseDto<MediaCardDto> getPopularMovies(int page, MediaFilterDto filter){
        return paginate(tmdbClient.getPopularMovies(page), page, filter);
    }

    @Cacheable(cacheNames = "topRatedMovies", key = "'page=' + #page + ':filter=' + #filter.hashCode()")
    public PagedResponseDto<MediaCardDto> getTopRatedMovies(int page, MediaFilterDto filter){
        return paginate(tmdbClient.getTopRatedMovies(page), page, filter);
    }

    @Cacheable(cacheNames = "upcomingMovies", key = "'page=' + #page + ':filter=' + #filter.hashCode()")
    public PagedResponseDto<MediaCardDto> getUpcomingMovies(int page, MediaFilterDto filter){
        return paginate(tmdbClient.getUpcomingMovies(page), page, filter);
    }

    private PagedResponseDto<MediaCardDto> paginate(TmdbPagedResponse<TmdbMovieDto> tmdb, int page, MediaFilterDto filter) {
        List<MediaCardDto> items = tmdb.getResults().stream()
                .map(this::toMediaCard)
                .toList();
        return mediaFilterService.paginate(items, filter, page, PAGE_SIZE);
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
