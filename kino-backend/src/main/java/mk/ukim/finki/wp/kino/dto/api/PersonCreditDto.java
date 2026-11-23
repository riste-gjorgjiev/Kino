package mk.ukim.finki.wp.kino.dto.api;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PersonCreditDto {
    private Long id;
    private MediaType mediaType;
    private String title;
    private String posterUrl;
    private Double rating;
    private String date;
    private String character;

    public PersonCreditDto() {}

    public PersonCreditDto(Long id, MediaType mediaType, String title, String posterUrl,
                           Double rating, String date, String character) {
        this.id = id;
        this.mediaType = mediaType;
        this.title = title;
        this.posterUrl = posterUrl;
        this.rating = rating;
        this.date = date;
        this.character = character;
    }
}
