package mk.ukim.finki.wp.kino.service;

import mk.ukim.finki.wp.kino.dto.api.MediaType;
import mk.ukim.finki.wp.kino.dto.api.PersonCreditDto;
import mk.ukim.finki.wp.kino.dto.api.PersonDetailsDto;
import mk.ukim.finki.wp.kino.dto.tmdb.person.TmdbPersonMovieCastDto;
import mk.ukim.finki.wp.kino.dto.tmdb.person.TmdbPersonTvCastDto;
import mk.ukim.finki.wp.kino.tmdb.TmdbClient;
import mk.ukim.finki.wp.kino.util.ImageUrlBuilder;
import mk.ukim.finki.wp.kino.util.TestDataFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PersonServiceTest {

    @Mock
    private TmdbClient tmdbClient;

    private PersonService personService;

    @BeforeEach
    void setUp() {
        personService = new PersonService(
                tmdbClient,
                new ImageUrlBuilder("https://image.tmdb.org/t/p/w500")
        );
    }

    @Test
    void getPersonDetails_mapsBasicFields() {
        when(tmdbClient.getPersonDetails(anyLong())).thenReturn(TestDataFactory.createPersonDetails());
        when(tmdbClient.getPersonMovieCredits(anyLong())).thenReturn(null);
        when(tmdbClient.getPersonTvCredits(anyLong())).thenReturn(null);

        PersonDetailsDto result = personService.getPersonDetails(1000L);

        assertEquals(1000L, result.getId());
        assertEquals("Test Actor", result.getName());
        assertEquals("A test actor biography", result.getBiography());
        assertEquals("https://image.tmdb.org/t/p/w500/actor.jpg", result.getProfileUrl());
        assertEquals("1980-05-15", result.getBirthday());
        assertEquals("Los Angeles, California, USA", result.getPlaceOfBirth());
        assertEquals("Acting", result.getKnownForDepartment());
        assertEquals(2, result.getAlsoKnownAs().size());
    }

    @Test
    void getPersonDetails_normalizesBlankStrings() {
        var person = TestDataFactory.createPersonDetails();
        person.setBiography("   ");
        person.setDeathday("");
        person.setPlaceOfBirth(null);
        when(tmdbClient.getPersonDetails(anyLong())).thenReturn(person);
        when(tmdbClient.getPersonMovieCredits(anyLong())).thenReturn(null);
        when(tmdbClient.getPersonTvCredits(anyLong())).thenReturn(null);

        PersonDetailsDto result = personService.getPersonDetails(1000L);

        assertNull(result.getBiography());
        assertNull(result.getDeathday());
        assertNull(result.getPlaceOfBirth());
    }

    @Test
    void getPersonDetails_mergesMovieAndTvCredits() {
        when(tmdbClient.getPersonDetails(anyLong())).thenReturn(TestDataFactory.createPersonDetails());
        when(tmdbClient.getPersonMovieCredits(anyLong())).thenReturn(
                TestDataFactory.createPersonMovieCredits(List.of(
                        TestDataFactory.createPersonMovieCast(100L, "Movie One", "2024-01-15", 7.5, "Character A"),
                        TestDataFactory.createPersonMovieCast(101L, "Movie Two", "2023-06-20", 8.0, "Character B")
                ))
        );
        when(tmdbClient.getPersonTvCredits(anyLong())).thenReturn(
                TestDataFactory.createPersonTvCredits(List.of(
                        TestDataFactory.createPersonTvCast(200L, "TV Show One", "2024-04-10", 8.5, "Character C")
                ))
        );

        PersonDetailsDto result = personService.getPersonDetails(1000L);

        assertEquals(3, result.getCredits().size());
        // Sorted by date descending: 2024-04-10, 2024-01-15, 2023-06-20
        assertEquals("TV Show One", result.getCredits().get(0).getTitle());
        assertEquals(MediaType.TV, result.getCredits().get(0).getMediaType());
        assertEquals("Movie One", result.getCredits().get(1).getTitle());
        assertEquals(MediaType.MOVIE, result.getCredits().get(1).getMediaType());
        assertEquals("Movie Two", result.getCredits().get(2).getTitle());
    }

    @Test
    void getPersonDetails_deduplicatesByMediaTypeAndId() {
        when(tmdbClient.getPersonDetails(anyLong())).thenReturn(TestDataFactory.createPersonDetails());
        // Same movie ID appears twice in movie credits
        when(tmdbClient.getPersonMovieCredits(anyLong())).thenReturn(
                TestDataFactory.createPersonMovieCredits(List.of(
                        TestDataFactory.createPersonMovieCast(100L, "Movie One", "2024-01-15", 7.5, "Character A"),
                        TestDataFactory.createPersonMovieCast(100L, "Movie One", "2024-01-15", 7.5, "Character A Duplicate")
                ))
        );
        when(tmdbClient.getPersonTvCredits(anyLong())).thenReturn(null);

        PersonDetailsDto result = personService.getPersonDetails(1000L);

        assertEquals(1, result.getCredits().size());
    }

    @Test
    void getPersonDetails_capsCreditsAt50() {
        when(tmdbClient.getPersonDetails(anyLong())).thenReturn(TestDataFactory.createPersonDetails());
        List<TmdbPersonMovieCastDto> manyMovies = new ArrayList<>();
        for (int i = 0; i < 60; i++) {
            manyMovies.add(TestDataFactory.createPersonMovieCast(
                    (long) (100 + i), "Movie " + i, "2024-01-" + String.format("%02d", (i % 28) + 1), 7.0, "Character " + i));
        }
        when(tmdbClient.getPersonMovieCredits(anyLong())).thenReturn(
                TestDataFactory.createPersonMovieCredits(manyMovies)
        );
        when(tmdbClient.getPersonTvCredits(anyLong())).thenReturn(null);

        PersonDetailsDto result = personService.getPersonDetails(1000L);

        assertEquals(50, result.getCredits().size());
    }

    @Test
    void getPersonDetails_handlesNullCreditsGracefully() {
        when(tmdbClient.getPersonDetails(anyLong())).thenReturn(TestDataFactory.createPersonDetails());
        when(tmdbClient.getPersonMovieCredits(anyLong())).thenReturn(null);
        when(tmdbClient.getPersonTvCredits(anyLong())).thenReturn(null);

        PersonDetailsDto result = personService.getPersonDetails(1000L);

        assertNotNull(result.getCredits());
        assertTrue(result.getCredits().isEmpty());
    }

    @Test
    void getPersonDetails_handlesCreditFetchFailureGracefully() {
        when(tmdbClient.getPersonDetails(anyLong())).thenReturn(TestDataFactory.createPersonDetails());
        when(tmdbClient.getPersonMovieCredits(anyLong())).thenThrow(new RuntimeException("TMDB down"));
        when(tmdbClient.getPersonTvCredits(anyLong())).thenThrow(new RuntimeException("TMDB down"));

        PersonDetailsDto result = personService.getPersonDetails(1000L);

        assertNotNull(result);
        assertEquals("Test Actor", result.getName());
        assertTrue(result.getCredits().isEmpty());
    }

    @Test
    void getPersonDetails_sortsByRatingWhenDatesAreEqual() {
        when(tmdbClient.getPersonDetails(anyLong())).thenReturn(TestDataFactory.createPersonDetails());
        when(tmdbClient.getPersonMovieCredits(anyLong())).thenReturn(
                TestDataFactory.createPersonMovieCredits(List.of(
                        TestDataFactory.createPersonMovieCast(100L, "Movie Low", "2024-01-15", 5.0, "Character A"),
                        TestDataFactory.createPersonMovieCast(101L, "Movie High", "2024-01-15", 9.0, "Character B")
                ))
        );
        when(tmdbClient.getPersonTvCredits(anyLong())).thenReturn(null);

        PersonDetailsDto result = personService.getPersonDetails(1000L);

        assertEquals(2, result.getCredits().size());
        assertEquals("Movie High", result.getCredits().get(0).getTitle());
        assertEquals("Movie Low", result.getCredits().get(1).getTitle());
    }

    @Test
    void getPersonDetails_handlesNullDatesGracefully() {
        when(tmdbClient.getPersonDetails(anyLong())).thenReturn(TestDataFactory.createPersonDetails());
        when(tmdbClient.getPersonMovieCredits(anyLong())).thenReturn(
                TestDataFactory.createPersonMovieCredits(List.of(
                        TestDataFactory.createPersonMovieCast(100L, "Movie No Date", null, 7.0, "Character A"),
                        TestDataFactory.createPersonMovieCast(101L, "Movie With Date", "2024-01-15", 8.0, "Character B")
                ))
        );
        when(tmdbClient.getPersonTvCredits(anyLong())).thenReturn(null);

        PersonDetailsDto result = personService.getPersonDetails(1000L);

        assertEquals(2, result.getCredits().size());
        // Movie with date should come first
        assertEquals("Movie With Date", result.getCredits().get(0).getTitle());
        assertNull(result.getCredits().get(1).getDate());
    }
}
