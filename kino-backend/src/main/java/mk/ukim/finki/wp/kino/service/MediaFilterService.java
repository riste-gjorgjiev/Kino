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
     * Applies the filter/sort rules and wraps the result in a page, computing the
     * total number of matches so callers do not repeat this bookkeeping.
     */
    public PagedResponseDto<MediaCardDto> paginate(List<MediaCardDto> items, MediaFilterDto filter, int page, int pageSize) {
        List<MediaCardDto> filteredItems = applyFiltersAndSort(items, filter, 1, pageSize);
        int totalResults = countAfterFilters(items, filter);
        int totalPages = (int) Math.ceil((double) totalResults / pageSize);
        return new PagedResponseDto<>(page, totalPages, filteredItems, totalResults);
    }

    public List<MediaCardDto> applyFiltersAndSort(
            List<MediaCardDto> items,
            MediaFilterDto filter,
            int page,
            int pageSize
    ) {
        List<MediaCardDto> filtered = applyFilters(items, filter).collect(Collectors.toList());
        sortInPlace(filtered, filter);
        return slice(filtered, page, pageSize);
    }

    public int countAfterFilters(List<MediaCardDto> items, MediaFilterDto filter) {
        return (int) applyFilters(items, filter).count();
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

    private List<MediaCardDto> slice(List<MediaCardDto> items, int page, int pageSize) {
        int fromIndex = (page - 1) * pageSize;
        if (fromIndex >= items.size()) {
            return List.of();
        }
        int toIndex = Math.min(fromIndex + pageSize, items.size());
        return items.subList(fromIndex, toIndex);
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
