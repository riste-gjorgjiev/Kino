package mk.ukim.finki.wp.kino.controller;

import mk.ukim.finki.wp.kino.dto.api.MediaType;
import mk.ukim.finki.wp.kino.dto.api.PersonCreditDto;
import mk.ukim.finki.wp.kino.dto.api.PersonDetailsDto;
import mk.ukim.finki.wp.kino.service.PersonService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.cache.CacheManager;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PersonController.class)
class PersonControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PersonService personService;

    @MockitoBean
    private CacheManager cacheManager;

    @Test
    void getPerson_withValidId_returnsPersonDetails() throws Exception {
        PersonDetailsDto person = new PersonDetailsDto();
        person.setId(1000L);
        person.setName("Test Actor");
        person.setBiography("A test biography");
        person.setProfileUrl("https://image.tmdb.org/t/p/w500/actor.jpg");
        person.setBirthday("1980-05-15");
        person.setPlaceOfBirth("Los Angeles, USA");
        person.setKnownForDepartment("Acting");
        person.setCredits(List.of(
                new PersonCreditDto(100L, MediaType.MOVIE, "Test Movie", "/poster.jpg", 7.5, "2024-01-15", "Character A")
        ));

        when(personService.getPersonDetails(1000L)).thenReturn(person);

        mockMvc.perform(get("/api/person/1000"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1000))
                .andExpect(jsonPath("$.name").value("Test Actor"))
                .andExpect(jsonPath("$.biography").value("A test biography"))
                .andExpect(jsonPath("$.credits[0].title").value("Test Movie"))
                .andExpect(jsonPath("$.credits[0].mediaType").value("MOVIE"));
    }

    @Test
    void getPerson_withZeroId_returnsBadRequest() throws Exception {
        mockMvc.perform(get("/api/person/0"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getPerson_withNegativeId_returnsBadRequest() throws Exception {
        mockMvc.perform(get("/api/person/-1"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getPerson_withNoCredits_returnsEmptyCreditsList() throws Exception {
        PersonDetailsDto person = new PersonDetailsDto();
        person.setId(1000L);
        person.setName("Test Actor");
        person.setCredits(List.of());

        when(personService.getPersonDetails(1000L)).thenReturn(person);

        mockMvc.perform(get("/api/person/1000"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.credits").isEmpty());
    }
}
