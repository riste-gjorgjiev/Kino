package mk.ukim.finki.wp.kino.dto.tmdb.details;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class CastDto {
    private Long id;
    private String name;
    private String character;
    private String profileUrl;

    public CastDto() {}

    public CastDto(Long id, String name, String character, String profileUrl) {
        this.id = id;
        this.name = name;
        this.character = character;
        this.profileUrl = profileUrl;
    }
}
