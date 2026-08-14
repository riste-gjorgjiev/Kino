package mk.ukim.finki.wp.kino.dto.tmdb.details.misc;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TmdbVideoDto {
    private String id;
    private String key;
    private String name;
    private String site;
    private String type;
    private boolean official;

    @JsonProperty("published_at")
    private String publishedAt;
}
