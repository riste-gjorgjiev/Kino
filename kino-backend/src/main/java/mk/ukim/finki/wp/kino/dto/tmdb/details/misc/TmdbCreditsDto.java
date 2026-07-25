package mk.ukim.finki.wp.kino.dto.tmdb.details.misc;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class TmdbCreditsDto {
    private List<TmdbCastDto> cast;
    private List<TmdbCrewDto> crew;
}
