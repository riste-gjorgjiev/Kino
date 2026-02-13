package mk.ukim.finki.wp.kino.dto.tmdb.details.misc;

import lombok.Getter;

import java.util.List;

@Getter
public class TmdbCreditsDto {
    private List<TmdbCastDto> cast;
    private List<TmdbCrewDto> crew;
}
