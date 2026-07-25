package mk.ukim.finki.wp.kino.dto.tmdb.details;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import mk.ukim.finki.wp.kino.dto.tmdb.details.misc.TmdbCreatorDto;
import mk.ukim.finki.wp.kino.dto.tmdb.details.misc.TmdbGenreDto;

import java.util.List;

@Getter
@Setter
public class TmdbTvDetailsDto {
    private Long id;
    private String name;
    private String overview;
    private String tagline;
    @JsonProperty("poster_path")
    private String posterPath;
    @JsonProperty("backdrop_path")
    private String backdropPath;
    @JsonProperty("first_air_date")
    private String firstAirDate;
    @JsonProperty("vote_average")
    private Double voteAverage;
    @JsonProperty("episode_run_time")
    private List<Integer> episodeRunTime;
    @JsonProperty("original_language")
    private String originalLanguage;
    private String status;
    private List<TmdbGenreDto> genres;
    @JsonProperty("created_by")
    private List<TmdbCreatorDto> createdBy;
}
