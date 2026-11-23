package mk.ukim.finki.wp.kino.dto.tmdb.person;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class TmdbPersonMovieCreditDto {
    private List<TmdbPersonMovieCastDto> cast;
}
