package mk.ukim.finki.wp.kino.dto.tmdb;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class TmdbMultiSearchDto {
    private Long id;
    @JsonProperty("media_type")
    private String mediaType;
    private String title;
    private String name;
    @JsonProperty("first_air_date")
    private String firstAirDate;
    @JsonProperty("poster_path")
    private String posterPath;
    private Double popularity;
    @JsonProperty("release_date")
    private String releaseDate;
    @JsonProperty("vote_average")
    private Double voteAverage;
}
