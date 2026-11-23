package mk.ukim.finki.wp.kino.service;

import mk.ukim.finki.wp.kino.dto.api.MediaType;
import mk.ukim.finki.wp.kino.dto.api.PersonCreditDto;
import mk.ukim.finki.wp.kino.dto.api.PersonDetailsDto;
import mk.ukim.finki.wp.kino.dto.tmdb.person.TmdbPersonDetailsDto;
import mk.ukim.finki.wp.kino.dto.tmdb.person.TmdbPersonMovieCastDto;
import mk.ukim.finki.wp.kino.dto.tmdb.person.TmdbPersonMovieCreditDto;
import mk.ukim.finki.wp.kino.dto.tmdb.person.TmdbPersonTvCastDto;
import mk.ukim.finki.wp.kino.dto.tmdb.person.TmdbPersonTvCreditDto;
import mk.ukim.finki.wp.kino.tmdb.TmdbClient;
import mk.ukim.finki.wp.kino.util.ImageUrlBuilder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Supplier;

@Service
public class PersonService {
    private static final int CREDITS_LIMIT = 50;

    private final TmdbClient tmdbClient;
    private final ImageUrlBuilder imageUrlBuilder;

    public PersonService(TmdbClient tmdbClient, ImageUrlBuilder imageUrlBuilder) {
        this.tmdbClient = tmdbClient;
        this.imageUrlBuilder = imageUrlBuilder;
    }

    public PersonDetailsDto getPersonDetails(long id) {
        TmdbPersonDetailsDto person = tmdbClient.getPersonDetails(id);
        TmdbPersonMovieCreditDto movieCredits = safeCall(() -> tmdbClient.getPersonMovieCredits(id));
        TmdbPersonTvCreditDto tvCredits = safeCall(() -> tmdbClient.getPersonTvCredits(id));

        List<PersonCreditDto> credits = buildCredits(movieCredits, tvCredits);

        PersonDetailsDto dto = new PersonDetailsDto();
        dto.setId(person.getId());
        dto.setName(person.getName());
        dto.setBiography(blankToNull(person.getBiography()));
        dto.setProfileUrl(imageUrlBuilder.build(person.getProfilePath()));
        dto.setBirthday(blankToNull(person.getBirthday()));
        dto.setDeathday(blankToNull(person.getDeathday()));
        dto.setPlaceOfBirth(blankToNull(person.getPlaceOfBirth()));
        dto.setAlsoKnownAs(person.getAlsoKnownAs() != null ? person.getAlsoKnownAs() : List.of());
        dto.setKnownForDepartment(blankToNull(person.getKnownForDepartment()));
        dto.setCredits(credits);

        return dto;
    }

    private List<PersonCreditDto> buildCredits(TmdbPersonMovieCreditDto movieCredits,
                                                TmdbPersonTvCreditDto tvCredits) {
        List<PersonCreditDto> all = new ArrayList<>();
        Set<String> seen = new HashSet<>();

        if (movieCredits != null && movieCredits.getCast() != null) {
            for (TmdbPersonMovieCastDto movie : movieCredits.getCast()) {
                if (movie.getId() == null) continue;
                String key = MediaType.MOVIE + ":" + movie.getId();
                if (!seen.add(key)) continue;

                all.add(new PersonCreditDto(
                        movie.getId(),
                        MediaType.MOVIE,
                        blankToNull(movie.getTitle()),
                        imageUrlBuilder.build(movie.getPosterPath()),
                        movie.getVoteAverage(),
                        blankToNull(movie.getReleaseDate()),
                        blankToNull(movie.getCharacter())
                ));
            }
        }

        if (tvCredits != null && tvCredits.getCast() != null) {
            for (TmdbPersonTvCastDto tv : tvCredits.getCast()) {
                if (tv.getId() == null) continue;
                String key = MediaType.TV + ":" + tv.getId();
                if (!seen.add(key)) continue;

                all.add(new PersonCreditDto(
                        tv.getId(),
                        MediaType.TV,
                        blankToNull(tv.getName()),
                        imageUrlBuilder.build(tv.getPosterPath()),
                        tv.getVoteAverage(),
                        blankToNull(tv.getFirstAirDate()),
                        blankToNull(tv.getCharacter())
                ));
            }
        }

        return all.stream()
                .sorted(Comparator.comparing(PersonCreditDto::getDate, Comparator.nullsLast(Comparator.reverseOrder()))
                        .thenComparing(Comparator.comparing(PersonCreditDto::getRating, Comparator.nullsLast(Comparator.reverseOrder()))))
                .limit(CREDITS_LIMIT)
                .toList();
    }

    private String blankToNull(String s) {
        if (s == null) return null;
        String t = s.trim();
        return t.isEmpty() ? null : t;
    }

    private <T> T safeCall(Supplier<T> call) {
        try {
            return call.get();
        } catch (Exception e) {
            return null;
        }
    }
}
