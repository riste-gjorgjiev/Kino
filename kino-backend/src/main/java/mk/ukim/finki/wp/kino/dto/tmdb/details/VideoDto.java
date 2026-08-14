package mk.ukim.finki.wp.kino.dto.tmdb.details;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VideoDto {
    private String id;
    private String key;
    private String name;
    private String site;
    private String type;
    private boolean official;
    private String publishedAt;

    public VideoDto() {
    }

    public VideoDto(String id, String key, String name, String site, String type, boolean official, String publishedAt) {
        this.id = id;
        this.key = key;
        this.name = name;
        this.site = site;
        this.type = type;
        this.official = official;
        this.publishedAt = publishedAt;
    }
}
