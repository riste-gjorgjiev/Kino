package mk.ukim.finki.wp.kino.controller;

import jakarta.validation.constraints.Positive;
import mk.ukim.finki.wp.kino.dto.api.PersonDetailsDto;
import mk.ukim.finki.wp.kino.service.PersonService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/person")
@Validated
public class PersonController {

    private final PersonService personService;

    public PersonController(PersonService personService) {
        this.personService = personService;
    }

    @GetMapping("/{id}")
    public PersonDetailsDto getPerson(@PathVariable @Positive long id) {
        return personService.getPersonDetails(id);
    }
}
