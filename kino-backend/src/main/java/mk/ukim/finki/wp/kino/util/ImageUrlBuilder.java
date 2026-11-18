package mk.ukim.finki.wp.kino.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Builds absolute image URLs from the relative paths returned by TMDB.
 * Centralised so services do not each carry their own copy of the logic.
 */
@Component
public class ImageUrlBuilder {

    private final String imageBaseUrl;

    public ImageUrlBuilder(@Value("${tmdb.image-base-url}") String imageBaseUrl) {
        this.imageBaseUrl = imageBaseUrl;
    }

    public String build(String path) {
        if (path == null || path.isBlank()) {
            return null;
        }
        return imageBaseUrl + path;
    }
}
