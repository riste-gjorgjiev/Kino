package mk.ukim.finki.wp.kino.service;

import mk.ukim.finki.wp.kino.dto.api.MediaCardDto;
import mk.ukim.finki.wp.kino.dto.api.MediaType;
import mk.ukim.finki.wp.kino.dto.api.PagedResponseDto;
import mk.ukim.finki.wp.kino.dto.tmdb.TmdbMovieDto;
import mk.ukim.finki.wp.kino.dto.tmdb.TmdbMultiSearchDto;
import mk.ukim.finki.wp.kino.dto.tmdb.TmdbPagedResponse;
import mk.ukim.finki.wp.kino.dto.tmdb.TmdbTvDto;
import mk.ukim.finki.wp.kino.tmdb.TmdbClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SearchService {
    private final TmdbClient tmdbClient;
    private final String imageBaseUrl;

    public SearchService(TmdbClient tmdbClient, @Value("${tmdb.image-base-url}") String imageBaseUrl) {
        this.tmdbClient = tmdbClient;
        this.imageBaseUrl = imageBaseUrl;
    }

    public PagedResponseDto<MediaCardDto> searchMovies(String query, int page){
        if (query == null || query.isBlank()){
            return new  PagedResponseDto<>(1, 0, java.util.List.of(), 0);
        }
        if (page < 1) page = 1;
        TmdbPagedResponse<TmdbMovieDto> tmdb = tmdbClient.searchMovies(query, page);

        List<MediaCardDto> items = tmdb.getResults().stream()
                .map(this::toMediaCardMovies)
                .toList();
        return new PagedResponseDto<>(
                tmdb.getPage(),
                tmdb.getTotalPages(),
                items,
                tmdb.getTotalResults()
        );
    }
    public PagedResponseDto<MediaCardDto> searchTv(String query, int page){
        if (query == null || query.isBlank()){
            return new  PagedResponseDto<>(1, 0, java.util.List.of(), 0);
        }
        if (page < 1) page = 1;

        TmdbPagedResponse<TmdbTvDto> tmdb = tmdbClient.searchTvShows(query, page);

        List<MediaCardDto> items = tmdb.getResults().stream()
                .map(this::toMediaCardTv)
                .toList();
        return new PagedResponseDto<>(
                tmdb.getPage(),
                tmdb.getTotalPages(),
                items,
                tmdb.getTotalResults()
        );
    }

    public PagedResponseDto<MediaCardDto> searchMulti(String query, int page){
        if (query == null || query.isBlank()){
            return new  PagedResponseDto<>(1, 0, java.util.List.of(), 0);
        }
        if (page < 1) page = 1;

        TmdbPagedResponse<TmdbMultiSearchDto> tmdb = tmdbClient.searchMulti(query, page);

        List<MediaCardDto> items = tmdb.getResults().stream()
                .filter(r -> "movie".equals(r.getMediaType()) || "tv".equals(r.getMediaType()))
                .map(this::toMultiMediaCard)
                .toList();
        return new PagedResponseDto<>(
                tmdb.getPage(),
                tmdb.getTotalPages(),
                items,
                tmdb.getTotalResults()
        );
    }

    private MediaCardDto toMediaCardMovies(TmdbMovieDto m){
        return new MediaCardDto(
                m.getId(),
                MediaType.MOVIE,
                m.getTitle(),
                fullImageUrl(m.getPosterPath()),
                m.getVoteAverage(),
                m.getReleaseDate()
        );
    }

    private MediaCardDto toMediaCardTv(TmdbTvDto t){
        return new MediaCardDto(
                t.getId(),
                MediaType.TV,
                t.getName(),
                fullImageUrl(t.getPosterPath()),
                t.getVoteAverage(),
                t.getFirstAirDate()
        );
    }
    private MediaCardDto toMultiMediaCard(TmdbMultiSearchDto m){
        if ("movie".equals(m.getMediaType())){
            return new MediaCardDto(
                    m.getId(),
                    MediaType.MOVIE,
                    m.getTitle(),
                    fullImageUrl(m.getPosterPath()),
                    m.getVoteAverage(),
                    m.getReleaseDate()
            );
        }
        return new MediaCardDto(
                m.getId(),
                MediaType.TV,
                m.getName(),
                fullImageUrl(m.getPosterPath()),
                m.getVoteAverage(),
                m.getFirstAirDate()
        );

    }
    private String fullImageUrl(String path){
        if (path == null || path.isBlank()) return null;
        return imageBaseUrl + path;
    }
}
