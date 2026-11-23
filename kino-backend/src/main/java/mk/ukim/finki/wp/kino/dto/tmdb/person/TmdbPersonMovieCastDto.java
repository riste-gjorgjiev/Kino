package mk.ukim.finki.wp.kino.dto.tmdb.person;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TmdbPersonMovieCastDto {
    private Long id;
    private String title;
    @JsonProperty("poster_path")
    private String posterPath;
    @JsonProperty("release_date")
    private String releaseDate;
    @JsonProperty("vote_average")
    private Double voteAverage;
    private String character;
    @JsonProperty("credit_id")
    private String creditId;
    private Integer order;
}
