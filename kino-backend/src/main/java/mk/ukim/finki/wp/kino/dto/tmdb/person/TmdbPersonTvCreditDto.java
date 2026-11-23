package mk.ukim.finki.wp.kino.dto.tmdb.person;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class TmdbPersonTvCreditDto {
    private List<TmdbPersonTvCastDto> cast;
}
