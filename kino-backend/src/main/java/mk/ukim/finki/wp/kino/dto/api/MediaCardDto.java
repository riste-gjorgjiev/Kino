package mk.ukim.finki.wp.kino.dto.api;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MediaCardDto {
    private Long id;
    private MediaType mediaType;
    private String title;
    private String posterUrl;
    private Double rating;
    private String date;
}
