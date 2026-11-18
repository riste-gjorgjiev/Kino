package mk.ukim.finki.wp.kino.dto.tmdb.details;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import mk.ukim.finki.wp.kino.dto.tmdb.details.misc.TmdbEpisodeDto;

import java.util.List;

@Getter
@Setter
public class TmdbSeasonDetailsDto {
    private Long id;
    private String name;
    private String overview;
    @JsonProperty("poster_path")
    private String posterPath;
    @JsonProperty("air_date")
    private String airDate;
    @JsonProperty("season_number")
    private Integer seasonNumber;
    private List<TmdbEpisodeDto> episodes;
}
