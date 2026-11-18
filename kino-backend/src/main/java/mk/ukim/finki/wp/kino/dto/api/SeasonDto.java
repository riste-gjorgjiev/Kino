package mk.ukim.finki.wp.kino.dto.api;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SeasonDto {
    private Long id;
    private Integer seasonNumber;
    private String name;
    private String overview;
    private String posterUrl;
    private String airDate;
    private Integer episodeCount;
}
