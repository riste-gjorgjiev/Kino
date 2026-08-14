package mk.ukim.finki.wp.kino.dto.tmdb.details;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class MediaDetailsDto {
    private Long id;
    private String mediaType;
    private String title;
    private String tagline;
    private String overview;
    private String posterUrl;
    private String backdropUrl;
    private Double rating;
    private String date;
    private List<String> genres;
    private Integer runtimeMinutes;
    private String status;
    private String originalLanguage;
    private String creator;
    private List<CastDto> cast;
    private List<String> directors;
    private List<VideoDto> videos;

}
