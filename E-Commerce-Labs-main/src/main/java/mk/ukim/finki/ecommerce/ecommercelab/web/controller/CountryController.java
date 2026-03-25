package mk.ukim.finki.ecommerce.ecommercelab.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import mk.ukim.finki.ecommerce.ecommercelab.model.dto.DisplayCountryDto;
import mk.ukim.finki.ecommerce.ecommercelab.repository.CountryRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/countries")
@RequiredArgsConstructor
@Tag(name = "Countries", description = "Endpoints for listing countries")
public class CountryController {
    private final CountryRepository countryRepository;

    @GetMapping
    @Operation(summary = "Get all countries")
    public ResponseEntity<List<DisplayCountryDto>> findAll(){
        return ResponseEntity.ok(countryRepository.findAll().stream().map(DisplayCountryDto::from).toList());
    }
}
