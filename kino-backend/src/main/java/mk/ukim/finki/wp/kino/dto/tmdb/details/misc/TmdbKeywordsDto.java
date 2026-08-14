package mk.ukim.finki.wp.kino.dto.tmdb.details.misc;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class TmdbKeywordsDto {
    @JsonProperty("keywords")
    private List<TmdbKeywordDto> keywords;

    @JsonProperty("results")
    private List<TmdbKeywordDto> results;

    public List<TmdbKeywordDto> getAllKeywords() {
        if (keywords != null) return keywords;
        return results != null ? results : List.of();
    }
}
