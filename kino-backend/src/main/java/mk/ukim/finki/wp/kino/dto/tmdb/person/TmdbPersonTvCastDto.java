package mk.ukim.finki.wp.kino.dto.tmdb.person;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TmdbPersonTvCastDto {
    private Long id;
    private String name;
    @JsonProperty("poster_path")
    private String posterPath;
    @JsonProperty("first_air_date")
    private String firstAirDate;
    @JsonProperty("vote_average")
    private Double voteAverage;
    private String character;
    @JsonProperty("credit_id")
    private String creditId;
    private Integer order;
}
