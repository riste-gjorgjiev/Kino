package mk.ukim.finki.wp.kino.dto.api;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class PersonDetailsDto {
    private Long id;
    private String name;
    private String biography;
    private String profileUrl;
    private String birthday;
    private String deathday;
    private String placeOfBirth;
    private List<String> alsoKnownAs;
    private String knownForDepartment;
    private List<PersonCreditDto> credits;
}
