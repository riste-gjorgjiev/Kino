package mk.ukim.finki.wp.kino.service;

import mk.ukim.finki.wp.kino.dto.api.MediaCardDto;
import mk.ukim.finki.wp.kino.dto.api.MediaFilterDto;
import mk.ukim.finki.wp.kino.dto.api.PagedResponseDto;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class MediaFilterService {

    /**
     * Filters and sorts the items of a single TMDB page and wraps them with the
     * pagination metadata reported by TMDB.
     *
     * <p>Filters and sorting deliberately operate on the current page only: the
     * application does not fetch the whole TMDB result set. Keeping TMDB's
     * {@code totalPages}/{@code totalResults} makes page navigation coherent and
     * the limitation is surfaced in the UI.</p>
     */
    public PagedResponseDto<MediaCardDto> toPage(
            List<MediaCardDto> items,
            MediaFilterDto filter,
            int page,
            int totalPages,
            Integer totalResults
    ) {
        int safePage = Math.max(page, 1);
        int safeTotalPages = Math.max(totalPages, 0);
        return new PagedResponseDto<>(
                safePage,
                safeTotalPages,
                filterAndSort(items, filter),
                totalResults == null ? 0 : Math.max(totalResults, 0)
        );
    }

    public List<MediaCardDto> filterAndSort(List<MediaCardDto> items, MediaFilterDto filter) {
        if (items == null || items.isEmpty()) {
            return List.of();
        }
        List<MediaCardDto> filtered = applyFilters(items, filter).collect(Collectors.toList());
        sortInPlace(filtered, filter);
        return filtered;
    }

    private Stream<MediaCardDto> applyFilters(List<MediaCardDto> items, MediaFilterDto filter) {
        Stream<MediaCardDto> stream = items.stream();

        if (filter.getYearFrom() != null) {
            stream = stream.filter(item -> {
                Integer year = extractYear(item.getDate());
                return year != null && year >= filter.getYearFrom();
            });
        }
        if (filter.getYearTo() != null) {
            stream = stream.filter(item -> {
                Integer year = extractYear(item.getDate());
                return year != null && year <= filter.getYearTo();
            });
        }
        return stream;
    }

    private void sortInPlace(List<MediaCardDto> items, MediaFilterDto filter) {
        if (filter.getSortBy() == null || filter.getSortBy().isBlank()) {
            return;
        }
        Comparator<MediaCardDto> comparator = getComparator(filter.getSortBy());
        if (comparator == null) {
            return;
        }
        if ("desc".equalsIgnoreCase(filter.getSortOrder())) {
            comparator = comparator.reversed();
        }
        items.sort(comparator);
    }

    private Integer extractYear(String date) {
        if (date == null || date.length() < 4) return null;
        try {
            return Integer.parseInt(date.substring(0, 4));
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private Comparator<MediaCardDto> getComparator(String sortBy) {
        return switch (sortBy.toLowerCase()) {
            case "rating" -> Comparator.comparing(
                    MediaCardDto::getRating,
                    Comparator.nullsLast(Comparator.naturalOrder())
            );
            case "date" -> Comparator.comparing(
                    MediaCardDto::getDate,
                    Comparator.nullsLast(Comparator.naturalOrder())
            );
            case "title" -> Comparator.comparing(
                    MediaCardDto::getTitle,
                    Comparator.nullsLast(String.CASE_INSENSITIVE_ORDER)
            );
            default -> null;
        };
    }
}
