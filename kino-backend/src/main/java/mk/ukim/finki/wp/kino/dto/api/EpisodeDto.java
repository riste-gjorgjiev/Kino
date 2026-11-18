package mk.ukim.finki.wp.kino.dto.api;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class EpisodeDto {
    private Long id;
    private Integer seasonNumber;
    private Integer episodeNumber;
    private String name;
    private String overview;
    private String stillUrl;
    private String airDate;
    private Integer runtimeMinutes;
    private Double rating;
    private Integer voteCount;
}
