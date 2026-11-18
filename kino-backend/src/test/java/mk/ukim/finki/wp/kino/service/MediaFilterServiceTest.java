package mk.ukim.finki.wp.kino.service;

import mk.ukim.finki.wp.kino.dto.api.MediaCardDto;
import mk.ukim.finki.wp.kino.dto.api.MediaFilterDto;
import mk.ukim.finki.wp.kino.dto.api.MediaType;
import mk.ukim.finki.wp.kino.dto.api.PagedResponseDto;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MediaFilterServiceTest {

    private final MediaFilterService service = new MediaFilterService();

    private static MediaCardDto card(long id, String title, Double rating, String date) {
        return new MediaCardDto(id, MediaType.MOVIE, title, "/p" + id + ".jpg", rating, date);
    }

    private static MediaFilterDto filter(Integer yearFrom, Integer yearTo, String sortBy, String sortOrder) {
        return new MediaFilterDto(yearFrom, yearTo, sortBy, sortOrder);
    }

    @Test
    void toPage_usesTmdbPaginationMetadataAndFiltersCurrentPage() {
        List<MediaCardDto> items = List.of(
                card(1, "Old", 7.0, "2010-01-01"),
                card(2, "New", 8.0, "2021-01-01")
        );

        PagedResponseDto<MediaCardDto> result = service.toPage(
                items, filter(2020, null, null, "asc"), 2, 7, 140
        );

        assertEquals(2, result.getPage());
        assertEquals(7, result.getTotalPages());
        assertEquals(140, result.getTotalResults());
        assertEquals(1, result.getItems().size());
        assertEquals(2L, result.getItems().get(0).getId());
    }

    @Test
    void toPage_clampsInvalidMetadata() {
        PagedResponseDto<MediaCardDto> result = service.toPage(
                List.of(card(1, "A", 7.0, "2020-01-01")),
                filter(null, null, null, "asc"),
                0,
                -3,
                null
        );

        assertEquals(1, result.getPage());
        assertEquals(0, result.getTotalPages());
        assertEquals(0, result.getTotalResults());
        assertEquals(1, result.getItems().size());
    }

    @Test
    void filterAndSort_withNoFilters_keepsAllItemsInOrder() {
        List<MediaCardDto> items = List.of(
                card(1, "A", 7.0, "2020-01-01"),
                card(2, "B", 8.0, "2021-01-01"),
                card(3, "C", 9.0, "2022-01-01")
        );

        List<MediaCardDto> result = service.filterAndSort(items, filter(null, null, null, "asc"));

        assertEquals(List.of(1L, 2L, 3L), result.stream().map(MediaCardDto::getId).toList());
    }

    @Test
    void filterAndSort_handlesNullAndEmptyInput() {
        assertTrue(service.filterAndSort(null, filter(null, null, null, "asc")).isEmpty());
        assertTrue(service.filterAndSort(List.of(), filter(null, null, null, "asc")).isEmpty());
    }

    @Test
    void filterAndSort_filtersByYearFrom() {
        List<MediaCardDto> items = List.of(
                card(1, "Old", 7.0, "2010-01-01"),
                card(2, "New", 8.0, "2021-01-01")
        );

        List<MediaCardDto> result = service.filterAndSort(items, filter(2020, null, null, "asc"));

        assertEquals(1, result.size());
        assertEquals(2L, result.get(0).getId());
    }

    @Test
    void filterAndSort_filtersByYearTo() {
        List<MediaCardDto> items = List.of(
                card(1, "Old", 7.0, "2010-01-01"),
                card(2, "New", 8.0, "2021-01-01")
        );

        List<MediaCardDto> result = service.filterAndSort(items, filter(null, 2015, null, "asc"));

        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).getId());
    }

    @Test
    void filterAndSort_ignoresItemsWithUnparseableDatesWhenFiltering() {
        List<MediaCardDto> items = List.of(
                card(1, "No date", 7.0, null),
                card(2, "Short", 8.0, "20"),
                card(3, "Bad", 9.0, "abcd-01-01"),
                card(4, "Good", 6.0, "2021-01-01")
        );

        List<MediaCardDto> result = service.filterAndSort(items, filter(2020, null, null, "asc"));

        assertEquals(1, result.size());
        assertEquals(4L, result.get(0).getId());
    }

    @Test
    void filterAndSort_sortsByRatingDescending() {
        List<MediaCardDto> items = List.of(
                card(1, "A", 5.0, "2020-01-01"),
                card(2, "B", 9.0, "2020-01-01"),
                card(3, "C", 7.0, "2020-01-01")
        );

        List<MediaCardDto> result = service.filterAndSort(items, filter(null, null, "rating", "desc"));

        assertEquals(List.of(2L, 3L, 1L), result.stream().map(MediaCardDto::getId).toList());
    }

    @Test
    void filterAndSort_sortsByRatingAscendingWithNullsLast() {
        List<MediaCardDto> items = List.of(
                card(1, "A", 5.0, "2020-01-01"),
                card(2, "No rating", null, "2020-01-01"),
                card(3, "C", 7.0, "2020-01-01")
        );

        List<MediaCardDto> result = service.filterAndSort(items, filter(null, null, "rating", "asc"));

        assertEquals(List.of(1L, 3L, 2L), result.stream().map(MediaCardDto::getId).toList());
    }

    @Test
    void filterAndSort_sortsByTitleCaseInsensitive() {
        List<MediaCardDto> items = List.of(
                card(1, "banana", 5.0, "2020-01-01"),
                card(2, "Apple", 5.0, "2020-01-01"),
                card(3, "cherry", 5.0, "2020-01-01")
        );

        List<MediaCardDto> result = service.filterAndSort(items, filter(null, null, "title", "asc"));

        assertEquals(List.of(2L, 1L, 3L), result.stream().map(MediaCardDto::getId).toList());
    }

    @Test
    void filterAndSort_unknownSortBy_keepsOriginalOrder() {
        List<MediaCardDto> items = List.of(
                card(1, "A", 5.0, "2020-01-01"),
                card(2, "B", 9.0, "2020-01-01"),
                card(3, "C", 7.0, "2020-01-01")
        );

        List<MediaCardDto> result = service.filterAndSort(items, filter(null, null, "unknown", "asc"));

        assertEquals(List.of(1L, 2L, 3L), result.stream().map(MediaCardDto::getId).toList());
    }
}
