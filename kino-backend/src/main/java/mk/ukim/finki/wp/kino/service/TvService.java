package mk.ukim.finki.wp.kino.service;

import mk.ukim.finki.wp.kino.dto.api.MediaCardDto;
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

    public TvService(TmdbClient tmdbClient, @Value("${tmdb.image-base-url}") String imageBaseUrl) {
        this.tmdbClient = tmdbClient;
        this.imageBaseUrl = imageBaseUrl;
    }
    @Cacheable(cacheNames = "trendingTv", key = "#window + ':' + #page")
    public PagedResponseDto<MediaCardDto> getTrendingTvShows(String window, int page){
        TmdbPagedResponse<TmdbTvDto> tmdb = tmdbClient.getTrendingTvShows(window, page);

        List<MediaCardDto> items = tmdb.getResults().stream()
                .map(this::toMediaCard)
                .toList();
        return new PagedResponseDto<>(
                tmdb.getPage(),
                tmdb.getTotalPages(),
                items,
                tmdb.getTotalResults()
        );
    }
    @Cacheable(cacheNames = "trendingTv", key = "'page=' + #page")
    public PagedResponseDto<MediaCardDto> getPopularTvShows(int page){
        TmdbPagedResponse<TmdbTvDto> tmdb = tmdbClient.getPopularTvShows(page);

        List<MediaCardDto> items = tmdb.getResults().stream()
                .map(this::toMediaCard)
                .toList();
        return new PagedResponseDto<>(
                tmdb.getPage(),
                tmdb.getTotalPages(),
                items,
                tmdb.getTotalResults()
        );
    }
    @Cacheable(cacheNames = "topRatedTv", key = "'page=' + #page")
    public PagedResponseDto<MediaCardDto> getTopRatedTvShows(int page){
        TmdbPagedResponse<TmdbTvDto> tmdb = tmdbClient.getTopRatedTvShows(page);

        List<MediaCardDto> items = tmdb.getResults().stream()
                .map(this::toMediaCard)
                .toList();
        return new PagedResponseDto<>(
                tmdb.getPage(),
                tmdb.getTotalPages(),
                items,
                tmdb.getTotalResults()
        );
    }
    @Cacheable(cacheNames = "airingTvShows", key = "'page=' + #page")
    public PagedResponseDto<MediaCardDto> getAiringTvShows(int page){
        TmdbPagedResponse<TmdbTvDto> tmdb = tmdbClient.getAiringTvShows(page);

        List<MediaCardDto> items = tmdb.getResults().stream()
                .map(this::toMediaCard)
                .toList();
        return new PagedResponseDto<>(
                tmdb.getPage(),
                tmdb.getTotalPages(),
                items,
                tmdb.getTotalResults()
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
