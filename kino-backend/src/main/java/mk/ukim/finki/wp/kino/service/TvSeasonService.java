package mk.ukim.finki.wp.kino.service;

import mk.ukim.finki.wp.kino.dto.api.EpisodeDto;
import mk.ukim.finki.wp.kino.dto.api.SeasonDetailsDto;
import mk.ukim.finki.wp.kino.dto.api.SeasonDto;
import mk.ukim.finki.wp.kino.dto.tmdb.details.TmdbSeasonDetailsDto;
import mk.ukim.finki.wp.kino.dto.tmdb.details.TmdbTvDetailsDto;
import mk.ukim.finki.wp.kino.dto.tmdb.details.misc.TmdbEpisodeDto;
import mk.ukim.finki.wp.kino.dto.tmdb.details.misc.TmdbSeasonDto;
import mk.ukim.finki.wp.kino.tmdb.TmdbClient;
import mk.ukim.finki.wp.kino.util.ImageUrlBuilder;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;

@Service
public class TvSeasonService {

    private final TmdbClient tmdbClient;
    private final ImageUrlBuilder imageUrlBuilder;

    public TvSeasonService(TmdbClient tmdbClient, ImageUrlBuilder imageUrlBuilder) {
        this.tmdbClient = tmdbClient;
        this.imageUrlBuilder = imageUrlBuilder;
    }

    @Cacheable(cacheNames = "tvSeasons", key = "#tvId")
    public List<SeasonDto> getSeasons(long tvId) {
        TmdbTvDetailsDto tv = tmdbClient.getTvDetails(tvId);
        if (tv == null || tv.getSeasons() == null) {
            return List.of();
        }
        return tv.getSeasons().stream()
                .filter(Objects::nonNull)
                .filter(season -> season.getSeasonNumber() != null)
                .sorted(Comparator.comparing(TmdbSeasonDto::getSeasonNumber))
                .map(this::toSeasonDto)
                .toList();
    }

    @Cacheable(cacheNames = "tvSeasonDetails", key = "#tvId + ':' + #seasonNumber")
    public SeasonDetailsDto getSeasonDetails(long tvId, int seasonNumber) {
        TmdbSeasonDetailsDto season = tmdbClient.getTvSeasonDetails(tvId, seasonNumber);
        List<EpisodeDto> episodes = mapEpisodes(season == null ? null : season.getEpisodes());

        SeasonDetailsDto dto = new SeasonDetailsDto();
        dto.setId(season == null ? null : season.getId());
        dto.setSeasonNumber(season == null || season.getSeasonNumber() == null
                ? seasonNumber
                : season.getSeasonNumber());
        dto.setName(season == null ? null : blankToNull(season.getName()));
        dto.setOverview(season == null ? null : blankToNull(season.getOverview()));
        dto.setPosterUrl(season == null ? null : imageUrlBuilder.build(season.getPosterPath()));
        dto.setAirDate(season == null ? null : blankToNull(season.getAirDate()));
        dto.setEpisodeCount(episodes.size());
        dto.setEpisodes(episodes);
        return dto;
    }

    @Cacheable(cacheNames = "tvSeasonEpisodes", key = "#tvId + ':' + #seasonNumber")
    public List<EpisodeDto> getEpisodes(long tvId, int seasonNumber) {
        return getSeasonDetails(tvId, seasonNumber).getEpisodes();
    }

    private List<EpisodeDto> mapEpisodes(List<TmdbEpisodeDto> episodes) {
        if (episodes == null) {
            return List.of();
        }
        return episodes.stream()
                .filter(Objects::nonNull)
                .filter(episode -> episode.getEpisodeNumber() != null)
                .sorted(Comparator.comparing(TmdbEpisodeDto::getEpisodeNumber))
                .map(this::toEpisodeDto)
                .toList();
    }

    private SeasonDto toSeasonDto(TmdbSeasonDto season) {
        return new SeasonDto(
                season.getId(),
                season.getSeasonNumber(),
                blankToNull(season.getName()),
                blankToNull(season.getOverview()),
                imageUrlBuilder.build(season.getPosterPath()),
                blankToNull(season.getAirDate()),
                season.getEpisodeCount()
        );
    }

    private EpisodeDto toEpisodeDto(TmdbEpisodeDto episode) {
        return new EpisodeDto(
                episode.getId(),
                episode.getSeasonNumber(),
                episode.getEpisodeNumber(),
                blankToNull(episode.getName()),
                blankToNull(episode.getOverview()),
                imageUrlBuilder.build(episode.getStillPath()),
                blankToNull(episode.getAirDate()),
                episode.getRuntime(),
                episode.getVoteAverage(),
                episode.getVoteCount()
        );
    }

    private String blankToNull(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }
}
