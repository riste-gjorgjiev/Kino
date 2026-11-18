package mk.ukim.finki.wp.kino.dto.tmdb.details.misc;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TmdbEpisodeDto {
    private Long id;
    private String name;
    private String overview;
    @JsonProperty("still_path")
    private String stillPath;
    @JsonProperty("air_date")
    private String airDate;
    @JsonProperty("episode_number")
    private Integer episodeNumber;
    @JsonProperty("season_number")
    private Integer seasonNumber;
    private Integer runtime;
    @JsonProperty("vote_average")
    private Double voteAverage;
    @JsonProperty("vote_count")
    private Integer voteCount;
}
