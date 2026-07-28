package mk.ukim.finki.wp.kino.service;

import mk.ukim.finki.wp.kino.dto.api.MediaCardDto;
import mk.ukim.finki.wp.kino.dto.api.MediaFilterDto;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class MediaFilterService {
    public List<MediaCardDto> applyFiltersAndSort(
            List<MediaCardDto> items,
            MediaFilterDto filter,
            int page,
            int pageSize
    ) {
        var stream = items.stream();

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

        List<MediaCardDto> filtered = stream.collect(Collectors.toList());

        if (filter.getSortBy() != null && !filter.getSortBy().isBlank()) {
            Comparator<MediaCardDto> comparator = getComparator(filter.getSortBy());
            if (comparator != null) {
                if ("desc".equalsIgnoreCase(filter.getSortOrder())) {
                    comparator = comparator.reversed();
                }
                filtered.sort(comparator);
            }
        }
        int totalItems = filtered.size();
        int fromIndex = (page - 1) * pageSize;
        int toIndex = Math.min(fromIndex + pageSize, totalItems);

        if (fromIndex >= totalItems) {
            return List.of();
        }
        return filtered.subList(fromIndex, toIndex);
    }

    public int countAfterFilters(List<MediaCardDto> items, MediaFilterDto filter) {
        var stream = items.stream();

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
        return (int) stream.count();
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
