package mk.ukim.finki.wp.kino.dto.tmdb.details.misc;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TmdbCastDto {
    private Long id;
    private String name;
    private String character;
    @JsonProperty("profile_path")
    private String profilePath;
}
