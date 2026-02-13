package mk.ukim.finki.wp.kino.dto.tmdb.details;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import mk.ukim.finki.wp.kino.dto.tmdb.details.misc.TmdbGenreDto;

import java.util.List;

@Getter
public class TmdbMovieDetailsDto {
    private Long id;
    private String title;
    private String overview;
    private String tagline;
    @JsonProperty("poster_path")
    private String posterPath;
    @JsonProperty("backdrop_path")
    private String backdropPath;
    @JsonProperty("release_date")
    private String releaseDate;
    @JsonProperty("vote_average")
    private Double voteAverage;
    private Integer runtime;
    private String status;
    @JsonProperty("original_language")
    private String originalLanguage;
    private List<TmdbGenreDto> genres;
}
