package mk.ukim.finki.wp.kino.dto.tmdb.person;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class TmdbPersonDetailsDto {
    private Long id;
    private String name;
    private String biography;
    @JsonProperty("profile_path")
    private String profilePath;
    private String birthday;
    private String deathday;
    @JsonProperty("place_of_birth")
    private String placeOfBirth;
    @JsonProperty("also_known_as")
    private List<String> alsoKnownAs;
    @JsonProperty("known_for_department")
    private String knownForDepartment;
    private String homepage;
    @JsonProperty("imdb_id")
    private String imdbId;
}
