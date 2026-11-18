package mk.ukim.finki.wp.kino.service;

import mk.ukim.finki.wp.kino.dto.api.MediaCardDto;
import mk.ukim.finki.wp.kino.dto.api.MediaFilterDto;
import mk.ukim.finki.wp.kino.dto.api.MediaType;
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
    void applyFiltersAndSort_withNoFilters_returnsItemsOnRequestedPage() {
        List<MediaCardDto> items = List.of(
                card(1, "A", 7.0, "2020-01-01"),
                card(2, "B", 8.0, "2021-01-01"),
                card(3, "C", 9.0, "2022-01-01")
        );

        List<MediaCardDto> result = service.applyFiltersAndSort(items, filter(null, null, null, "asc"), 1, 2);

        assertEquals(2, result.size());
        assertEquals(1L, result.get(0).getId());
        assertEquals(2L, result.get(1).getId());
    }

    @Test
    void applyFiltersAndSort_secondPage_returnsRemainingItems() {
        List<MediaCardDto> items = List.of(
                card(1, "A", 7.0, "2020-01-01"),
                card(2, "B", 8.0, "2021-01-01"),
                card(3, "C", 9.0, "2022-01-01")
        );

        List<MediaCardDto> result = service.applyFiltersAndSort(items, filter(null, null, null, "asc"), 2, 2);

        assertEquals(1, result.size());
        assertEquals(3L, result.get(0).getId());
    }

    @Test
    void applyFiltersAndSort_pageBeyondRange_returnsEmpty() {
        List<MediaCardDto> items = List.of(card(1, "A", 7.0, "2020-01-01"));

        List<MediaCardDto> result = service.applyFiltersAndSort(items, filter(null, null, null, "asc"), 5, 20);

        assertTrue(result.isEmpty());
    }

    @Test
    void applyFiltersAndSort_filtersByYearFrom() {
        List<MediaCardDto> items = List.of(
                card(1, "Old", 7.0, "2010-01-01"),
                card(2, "New", 8.0, "2021-01-01")
        );

        List<MediaCardDto> result = service.applyFiltersAndSort(items, filter(2020, null, null, "asc"), 1, 20);

        assertEquals(1, result.size());
        assertEquals(2L, result.get(0).getId());
    }

    @Test
    void applyFiltersAndSort_filtersByYearTo() {
        List<MediaCardDto> items = List.of(
                card(1, "Old", 7.0, "2010-01-01"),
                card(2, "New", 8.0, "2021-01-01")
        );

        List<MediaCardDto> result = service.applyFiltersAndSort(items, filter(null, 2015, null, "asc"), 1, 20);

        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).getId());
    }

    @Test
    void applyFiltersAndSort_ignoresItemsWithUnparseableDatesWhenFiltering() {
        List<MediaCardDto> items = List.of(
                card(1, "No date", 7.0, null),
                card(2, "Short", 8.0, "20"),
                card(3, "Bad", 9.0, "abcd-01-01"),
                card(4, "Good", 6.0, "2021-01-01")
        );

        List<MediaCardDto> result = service.applyFiltersAndSort(items, filter(2020, null, null, "asc"), 1, 20);

        assertEquals(1, result.size());
        assertEquals(4L, result.get(0).getId());
    }

    @Test
    void applyFiltersAndSort_sortsByRatingDescending() {
        List<MediaCardDto> items = List.of(
                card(1, "A", 5.0, "2020-01-01"),
                card(2, "B", 9.0, "2020-01-01"),
                card(3, "C", 7.0, "2020-01-01")
        );

        List<MediaCardDto> result = service.applyFiltersAndSort(items, filter(null, null, "rating", "desc"), 1, 20);

        assertEquals(List.of(2L, 3L, 1L), result.stream().map(MediaCardDto::getId).toList());
    }

    @Test
    void applyFiltersAndSort_sortsByRatingAscendingWithNullsLast() {
        List<MediaCardDto> items = List.of(
                card(1, "A", 5.0, "2020-01-01"),
                card(2, "No rating", null, "2020-01-01"),
                card(3, "C", 7.0, "2020-01-01")
        );

        List<MediaCardDto> result = service.applyFiltersAndSort(items, filter(null, null, "rating", "asc"), 1, 20);

        assertEquals(List.of(1L, 3L, 2L), result.stream().map(MediaCardDto::getId).toList());
    }

    @Test
    void applyFiltersAndSort_sortsByTitleCaseInsensitive() {
        List<MediaCardDto> items = List.of(
                card(1, "banana", 5.0, "2020-01-01"),
                card(2, "Apple", 5.0, "2020-01-01"),
                card(3, "cherry", 5.0, "2020-01-01")
        );

        List<MediaCardDto> result = service.applyFiltersAndSort(items, filter(null, null, "title", "asc"), 1, 20);

        assertEquals(List.of(2L, 1L, 3L), result.stream().map(MediaCardDto::getId).toList());
    }

    @Test
    void applyFiltersAndSort_unknownSortBy_keepsOriginalOrder() {
        List<MediaCardDto> items = List.of(
                card(1, "A", 5.0, "2020-01-01"),
                card(2, "B", 9.0, "2020-01-01"),
                card(3, "C", 7.0, "2020-01-01")
        );

        List<MediaCardDto> result = service.applyFiltersAndSort(items, filter(null, null, "unknown", "asc"), 1, 20);

        assertEquals(List.of(1L, 2L, 3L), result.stream().map(MediaCardDto::getId).toList());
    }

    @Test
    void countAfterFilters_countsOnlyMatchingItems() {
        List<MediaCardDto> items = List.of(
                card(1, "Old", 7.0, "2010-01-01"),
                card(2, "New", 8.0, "2021-01-01"),
                card(3, "Newer", 9.0, "2022-01-01")
        );

        int count = service.countAfterFilters(items, filter(2020, null, null, "asc"));

        assertEquals(2, count);
    }

    @Test
    void countAfterFilters_withNoFilters_countsEverything() {
        List<MediaCardDto> items = List.of(
                card(1, "A", 7.0, "2010-01-01"),
                card(2, "B", 8.0, "2021-01-01")
        );

        assertEquals(2, service.countAfterFilters(items, filter(null, null, null, "asc")));
    }
}
