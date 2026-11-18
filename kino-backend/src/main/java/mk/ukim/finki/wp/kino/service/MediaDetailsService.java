package mk.ukim.finki.wp.kino.service;

import mk.ukim.finki.wp.kino.dto.api.MediaCardDto;
import mk.ukim.finki.wp.kino.dto.api.MediaType;
import mk.ukim.finki.wp.kino.dto.tmdb.TmdbMovieDto;
import mk.ukim.finki.wp.kino.dto.tmdb.TmdbPagedResponse;
import mk.ukim.finki.wp.kino.dto.tmdb.TmdbTvDto;
import mk.ukim.finki.wp.kino.dto.api.CastDto;
import mk.ukim.finki.wp.kino.dto.api.KeywordDto;
import mk.ukim.finki.wp.kino.dto.api.MediaDetailsDto;
import mk.ukim.finki.wp.kino.dto.tmdb.details.TmdbMovieDetailsDto;
import mk.ukim.finki.wp.kino.dto.tmdb.details.TmdbTvDetailsDto;
import mk.ukim.finki.wp.kino.dto.api.VideoDto;
import mk.ukim.finki.wp.kino.dto.tmdb.details.misc.TmdbCreatorDto;
import mk.ukim.finki.wp.kino.dto.tmdb.details.misc.TmdbCreditsDto;
import mk.ukim.finki.wp.kino.dto.tmdb.details.misc.TmdbCrewDto;
import mk.ukim.finki.wp.kino.dto.tmdb.details.misc.TmdbGenreDto;
import mk.ukim.finki.wp.kino.dto.tmdb.details.misc.TmdbKeywordDto;
import mk.ukim.finki.wp.kino.dto.tmdb.details.misc.TmdbKeywordsDto;
import mk.ukim.finki.wp.kino.dto.tmdb.details.misc.TmdbVideoDto;
import mk.ukim.finki.wp.kino.dto.tmdb.details.misc.TmdbVideoResponseDto;
import mk.ukim.finki.wp.kino.tmdb.TmdbClient;
import mk.ukim.finki.wp.kino.util.ImageUrlBuilder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;

@Service

public class MediaDetailsService {
    private static final int RECOMMENDATIONS_LIMIT = 12;

    private final TmdbClient tmdbClient;
    private final ImageUrlBuilder imageUrlBuilder;

    public MediaDetailsService(TmdbClient tmdbClient, ImageUrlBuilder imageUrlBuilder) {
        this.tmdbClient = tmdbClient;
        this.imageUrlBuilder = imageUrlBuilder;
    }


    public MediaDetailsDto getMovieDetails(long id){
        TmdbMovieDetailsDto movie = tmdbClient.getMovieDetails(id);
        TmdbCreditsDto credits = tmdbClient.getMovieCredits(id);
        TmdbVideoResponseDto videos = tmdbClient.getMovieVideos(id);
        TmdbKeywordsDto keywords = safeCall(() -> tmdbClient.getMovieKeywords(id));
        TmdbPagedResponse<TmdbMovieDto> recommendations = safeCall(() -> tmdbClient.getMovieRecommendations(id, 1));
        TmdbPagedResponse<TmdbMovieDto> similar = safeCall(() -> tmdbClient.getMovieSimilar(id, 1));

        List<CastDto> topCast = mapCast(credits, 12);
        List<String> directors = extractDirectors(credits);
        List<VideoDto> mappedVideos = mapVideos(videos);
        List<KeywordDto> mappedKeywords = mapKeywords(keywords);
        List<MediaCardDto> mappedRecommendations = buildMovieRecommendations(recommendations, similar);

        MediaDetailsDto dto = new MediaDetailsDto();

        dto.setId(movie.getId());
        dto.setMediaType(MediaType.MOVIE);
        dto.setTitle(movie.getTitle());
        dto.setTagline(blankToNull(movie.getTagline()));
        dto.setOverview(blankToNull(movie.getOverview()));
        dto.setPosterUrl(imageUrlBuilder.build(movie.getPosterPath()));
        dto.setBackdropUrl(imageUrlBuilder.build(movie.getBackdropPath()));
        dto.setRating(movie.getVoteAverage());
        dto.setDate(blankToNull(movie.getReleaseDate()));
        dto.setGenres(mapGenres(movie.getGenres()));
        dto.setRuntimeMinutes(movie.getRuntime());
        dto.setStatus(blankToNull(movie.getStatus()));
        dto.setOriginalLanguage(blankToNull(movie.getOriginalLanguage()));
        dto.setCreator(null);
        dto.setDirectors(directors);
        dto.setCast(topCast);
        dto.setVideos(mappedVideos);
        dto.setKeywords(mappedKeywords);
        dto.setRecommendations(mappedRecommendations);

        return dto;
    }

    public MediaDetailsDto getTvDetails(long id){
        TmdbTvDetailsDto tv = tmdbClient.getTvDetails(id);
        TmdbCreditsDto credits = tmdbClient.getTvCredits(id);
        TmdbVideoResponseDto videos = tmdbClient.getTvVideos(id);
        TmdbKeywordsDto keywords = safeCall(() -> tmdbClient.getTvKeywords(id));
        TmdbPagedResponse<TmdbTvDto> recommendations = safeCall(() -> tmdbClient.getTvRecommendations(id, 1));
        TmdbPagedResponse<TmdbTvDto> similar = safeCall(() -> tmdbClient.getTvSimilar(id, 1));

        List<CastDto> topCast = mapCast(credits, 12);
        String creator = extractFirstCreator(tv.getCreatedBy());
        Integer runtime = extractFirstRuntime(tv.getEpisodeRunTime());
        List<VideoDto> mappedVideos = mapVideos(videos);
        List<KeywordDto> mappedKeywords = mapKeywords(keywords);
        List<MediaCardDto> mappedRecommendations = buildTvRecommendations(recommendations, similar);

        MediaDetailsDto dto = new MediaDetailsDto();

        dto.setId(tv.getId());
        dto.setMediaType(MediaType.TV);
        dto.setTitle(tv.getName());
        dto.setTagline(blankToNull(tv.getTagline()));
        dto.setOverview(blankToNull(tv.getOverview()));
        dto.setPosterUrl(imageUrlBuilder.build(tv.getPosterPath()));
        dto.setBackdropUrl(imageUrlBuilder.build(tv.getBackdropPath()));
        dto.setRating(tv.getVoteAverage());
        dto.setDate(blankToNull(tv.getFirstAirDate()));
        dto.setGenres(mapGenres(tv.getGenres()));
        dto.setRuntimeMinutes(runtime);
        dto.setStatus(blankToNull(tv.getStatus()));
        dto.setOriginalLanguage(blankToNull(tv.getOriginalLanguage()));
        dto.setCreator(creator);
        dto.setDirectors(List.of());
        dto.setCast(topCast);
        dto.setVideos(mappedVideos);
        dto.setKeywords(mappedKeywords);
        dto.setRecommendations(mappedRecommendations);

        return dto;
    }

    private List<String> mapGenres(List<TmdbGenreDto> genres){
        if (genres == null) return List.of();
        return genres.stream()
                .map(TmdbGenreDto::getName)
                .filter(Objects::nonNull)
                .toList();
    }

    private List<String> extractDirectors(TmdbCreditsDto credits) {
        if (credits == null || credits.getCrew() == null) return List.of();

        return credits.getCrew().stream()
                .filter(c -> c.getJob() != null && c.getJob().equalsIgnoreCase("Director"))
                .map(TmdbCrewDto::getName)
                .filter(Objects::nonNull)
                .distinct()
                .toList();
    }

    private List<CastDto> mapCast(TmdbCreditsDto credits, int i) {
        if (credits == null || credits.getCast() == null) return List.of();

        return credits.getCast().stream()
                .limit(i)
                .map(c -> new CastDto(
                        c.getId(),
                        c.getName(),
                        c.getCharacter(),
                        imageUrlBuilder.build(c.getProfilePath())
                ))
                .toList();
    }

    private String extractFirstCreator(List<TmdbCreatorDto> creators){
        if (creators == null || creators.isEmpty()) return null;
        return blankToNull(creators.get(0).getName());
    }

    private Integer extractFirstRuntime(List<Integer> runtimes){
        if (runtimes == null || runtimes.isEmpty()) return null;
        return runtimes.getFirst();
    }

    private List<VideoDto> mapVideos(TmdbVideoResponseDto response) {
        if (response == null || response.getResults() == null) {
            return List.of();
        }

        return response.getResults().stream()
                .filter(v -> Objects.nonNull(v.getSite())
                        && v.getSite().equalsIgnoreCase("YouTube")
                        && Objects.nonNull(v.getType())
                        && isAllowedVideoType(v.getType()))
                .sorted(Comparator.comparing(TmdbVideoDto::isOfficial).reversed()
                        .thenComparing(Comparator.comparingInt((TmdbVideoDto v) -> videoTypePriority(v.getType())))
                        .thenComparing(TmdbVideoDto::getPublishedAt, Comparator.nullsLast(Comparator.reverseOrder())))
                .limit(12)
                .map(v -> new VideoDto(
                        v.getId(),
                        v.getKey(),
                        v.getName(),
                        v.getSite(),
                        v.getType(),
                        v.isOfficial(),
                        v.getPublishedAt()
                ))
                .toList();
    }

    private boolean isAllowedVideoType(String type) {
        return type.equalsIgnoreCase("Trailer")
                || type.equalsIgnoreCase("Teaser")
                || type.equalsIgnoreCase("Clip")
                || type.equalsIgnoreCase("Featurette");
    }

    private int videoTypePriority(String type) {
        if (type == null) return Integer.MAX_VALUE;
        return switch (type.toLowerCase()) {
            case "trailer" -> 0;
            case "teaser" -> 1;
            case "featurette" -> 2;
            case "clip" -> 3;
            default -> Integer.MAX_VALUE;
        };
    }

    private String blankToNull(String s){
        if (s == null) return null;
        String t = s.trim();
        return t.isEmpty() ? null : t;
    }

    private <T> T safeCall(Supplier<T> call) {
        try {
            return call.get();
        } catch (Exception e) {
            return null;
        }
    }

    private List<KeywordDto> mapKeywords(TmdbKeywordsDto response) {
        if (response == null) return List.of();
        return response.getAllKeywords().stream()
                .filter(k -> k.getName() != null && !k.getName().isBlank())
                .map(k -> {
                    KeywordDto dto = new KeywordDto();
                    dto.setId(k.getId());
                    dto.setName(k.getName().trim());
                    return dto;
                })
                .toList();
    }

    private List<MediaCardDto> buildMovieRecommendations(TmdbPagedResponse<TmdbMovieDto> recommendations,
                                                         TmdbPagedResponse<TmdbMovieDto> similar) {
        return mergeRecommendations(
                recommendations,
                similar,
                MediaType.MOVIE,
                TmdbMovieDto::getId,
                TmdbMovieDto::getTitle,
                TmdbMovieDto::getVoteAverage,
                TmdbMovieDto::getPosterPath,
                TmdbMovieDto::getReleaseDate
        );
    }

    private List<MediaCardDto> buildTvRecommendations(TmdbPagedResponse<TmdbTvDto> recommendations,
                                                      TmdbPagedResponse<TmdbTvDto> similar) {
        return mergeRecommendations(
                recommendations,
                similar,
                MediaType.TV,
                TmdbTvDto::getId,
                TmdbTvDto::getName,
                TmdbTvDto::getVoteAverage,
                TmdbTvDto::getPosterPath,
                TmdbTvDto::getFirstAirDate
        );
    }

    /**
     * Merges TMDB recommendations and "similar" titles into a single de-duplicated
     * list, with recommendations taking priority over similar titles.
     */
    private <T> List<MediaCardDto> mergeRecommendations(
            TmdbPagedResponse<T> recommendations,
            TmdbPagedResponse<T> similar,
            MediaType mediaType,
            Function<T, Long> id,
            Function<T, String> title,
            Function<T, Double> rating,
            Function<T, String> posterPath,
            Function<T, String> date
    ) {
        List<MediaCardDto> merged = new ArrayList<>();
        Set<Long> seen = new HashSet<>();

        for (TmdbPagedResponse<T> page : new TmdbPagedResponse[]{recommendations, similar}) {
            if (page == null || page.getResults() == null) continue;
            page.getResults().stream()
                    .filter(t -> id.apply(t) != null)
                    .filter(t -> seen.add(id.apply(t)))
                    .map(t -> new MediaCardDto(
                            id.apply(t),
                            mediaType,
                            title.apply(t),
                            imageUrlBuilder.build(posterPath.apply(t)),
                            rating.apply(t),
                            date.apply(t)))
                    .forEach(merged::add);
        }

        return merged.stream().limit(RECOMMENDATIONS_LIMIT).toList();
    }
}
