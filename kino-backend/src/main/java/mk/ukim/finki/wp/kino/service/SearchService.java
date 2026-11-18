package mk.ukim.finki.wp.kino.service;

import mk.ukim.finki.wp.kino.dto.api.MediaCardDto;
import mk.ukim.finki.wp.kino.dto.api.MediaFilterDto;
import mk.ukim.finki.wp.kino.dto.api.PagedResponseDto;
import mk.ukim.finki.wp.kino.dto.tmdb.TmdbMovieDto;
import mk.ukim.finki.wp.kino.dto.tmdb.TmdbMultiSearchDto;
import mk.ukim.finki.wp.kino.dto.tmdb.TmdbPagedResponse;
import mk.ukim.finki.wp.kino.dto.tmdb.TmdbTvDto;
import mk.ukim.finki.wp.kino.tmdb.TmdbClient;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SearchService {

    private final TmdbClient tmdbClient;
    private final MediaCardMapper mediaCardMapper;
    private final MediaFilterService mediaFilterService;

    public SearchService(TmdbClient tmdbClient, MediaCardMapper mediaCardMapper, MediaFilterService mediaFilterService) {
        this.tmdbClient = tmdbClient;
        this.mediaCardMapper = mediaCardMapper;
        this.mediaFilterService = mediaFilterService;
    }

    public PagedResponseDto<MediaCardDto> searchMovies(String query, int page, MediaFilterDto filter){
        if (isBlankQuery(query)) {
            return emptyResponse();
        }
        if (page < 1) page = 1;

        TmdbPagedResponse<TmdbMovieDto> tmdb = tmdbClient.searchMovies(query, page);
        List<MediaCardDto> items = mapMovies(tmdb);
        return toPage(items, tmdb, filter);
    }

    public PagedResponseDto<MediaCardDto> searchTv(String query, int page, MediaFilterDto filter){
        if (isBlankQuery(query)) {
            return emptyResponse();
        }
        if (page < 1) page = 1;

        TmdbPagedResponse<TmdbTvDto> tmdb = tmdbClient.searchTvShows(query, page);
        List<MediaCardDto> items = mapTv(tmdb);
        return toPage(items, tmdb, filter);
    }

    public PagedResponseDto<MediaCardDto> searchMulti(String query, int page, MediaFilterDto filter){
        if (isBlankQuery(query)) {
            return emptyResponse();
        }
        if (page < 1) page = 1;

        TmdbPagedResponse<TmdbMultiSearchDto> tmdb = tmdbClient.searchMulti(query, page);
        List<MediaCardDto> items = tmdb.getResults() == null
                ? List.of()
                : tmdb.getResults().stream()
                        .filter(r -> "movie".equals(r.getMediaType()) || "tv".equals(r.getMediaType()))
                        .map(mediaCardMapper::toCard)
                        .toList();
        return toPage(items, tmdb, filter);
    }

    private boolean isBlankQuery(String query) {
        return query == null || query.isBlank();
    }

    private PagedResponseDto<MediaCardDto> emptyResponse() {
        return new PagedResponseDto<>(1, 0, List.of(), 0);
    }

    private List<MediaCardDto> mapMovies(TmdbPagedResponse<TmdbMovieDto> tmdb) {
        if (tmdb.getResults() == null) return List.of();
        return tmdb.getResults().stream().map(mediaCardMapper::toCard).toList();
    }

    private List<MediaCardDto> mapTv(TmdbPagedResponse<TmdbTvDto> tmdb) {
        if (tmdb.getResults() == null) return List.of();
        return tmdb.getResults().stream().map(mediaCardMapper::toCard).toList();
    }

    private PagedResponseDto<MediaCardDto> toPage(List<MediaCardDto> items, TmdbPagedResponse<?> tmdb, MediaFilterDto filter) {
        return mediaFilterService.toPage(
                items,
                filter,
                Math.max(tmdb.getPage(), 1),
                tmdb.getTotalPages(),
                tmdb.getTotalResults()
        );
    }
}
