package mk.ukim.finki.wp.kino.service;

import mk.ukim.finki.wp.kino.dto.api.MediaCardDto;
import mk.ukim.finki.wp.kino.dto.api.MediaType;
import mk.ukim.finki.wp.kino.dto.tmdb.TmdbMovieDto;
import mk.ukim.finki.wp.kino.dto.tmdb.TmdbMultiSearchDto;
import mk.ukim.finki.wp.kino.dto.tmdb.TmdbTvDto;
import mk.ukim.finki.wp.kino.util.ImageUrlBuilder;
import org.springframework.stereotype.Component;

/**
 * Maps TMDB list items to the public {@link MediaCardDto} shape.
 */
@Component
public class MediaCardMapper {

    private final ImageUrlBuilder imageUrlBuilder;

    public MediaCardMapper(ImageUrlBuilder imageUrlBuilder) {
        this.imageUrlBuilder = imageUrlBuilder;
    }

    public MediaCardDto toCard(TmdbMovieDto movie) {
        return new MediaCardDto(
                movie.getId(),
                MediaType.MOVIE,
                movie.getTitle(),
                imageUrlBuilder.build(movie.getPosterPath()),
                movie.getVoteAverage(),
                movie.getReleaseDate()
        );
    }

    public MediaCardDto toCard(TmdbTvDto tv) {
        return new MediaCardDto(
                tv.getId(),
                MediaType.TV,
                tv.getName(),
                imageUrlBuilder.build(tv.getPosterPath()),
                tv.getVoteAverage(),
                tv.getFirstAirDate()
        );
    }

    public MediaCardDto toCard(TmdbMultiSearchDto multi) {
        if ("movie".equals(multi.getMediaType())) {
            return new MediaCardDto(
                    multi.getId(),
                    MediaType.MOVIE,
                    multi.getTitle(),
                    imageUrlBuilder.build(multi.getPosterPath()),
                    multi.getVoteAverage(),
                    multi.getReleaseDate()
            );
        }
        return new MediaCardDto(
                multi.getId(),
                MediaType.TV,
                multi.getName(),
                imageUrlBuilder.build(multi.getPosterPath()),
                multi.getVoteAverage(),
                multi.getFirstAirDate()
        );
    }
}
