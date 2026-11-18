package mk.ukim.finki.wp.kino.dto.tmdb.details.misc;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TmdbSeasonDto {
    private Long id;
    private String name;
    private String overview;
    @JsonProperty("poster_path")
    private String posterPath;
    @JsonProperty("air_date")
    private String airDate;
    @JsonProperty("season_number")
    private Integer seasonNumber;
    @JsonProperty("episode_count")
    private Integer episodeCount;
}
