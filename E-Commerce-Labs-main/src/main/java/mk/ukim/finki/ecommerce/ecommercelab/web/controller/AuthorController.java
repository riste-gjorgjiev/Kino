package mk.ukim.finki.ecommerce.ecommercelab.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.Table;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import mk.ukim.finki.ecommerce.ecommercelab.model.dto.CreateAuthorDto;
import mk.ukim.finki.ecommerce.ecommercelab.model.dto.DisplayAuthorDto;
import mk.ukim.finki.ecommerce.ecommercelab.model.dto.DisplayPopularAuthorDto;
import mk.ukim.finki.ecommerce.ecommercelab.model.dto.DisplayPopularBookDto;
import mk.ukim.finki.ecommerce.ecommercelab.repository.ActivityLogRepository;
import mk.ukim.finki.ecommerce.ecommercelab.service.application.AuthorApplicationService;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/authors")
@RequiredArgsConstructor
@Tag(name = "Authors", description = "Endpoints for managing book authors")
public class AuthorController {
    private final AuthorApplicationService authorApplicationService;
    private final ActivityLogRepository activityLogRepository;

    @GetMapping
    @Operation(summary = "Get all authors")
    public ResponseEntity<List<DisplayAuthorDto>> findAll(){
        return ResponseEntity.ok(authorApplicationService.findAll());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get an author by ID")
    public ResponseEntity<DisplayAuthorDto> findById(@PathVariable Long id){
        return ResponseEntity.ok(authorApplicationService.findById(id));
    }

    @PostMapping("/add")
    @Operation(summary = "Add a new author")
    public ResponseEntity<DisplayAuthorDto> create(@Valid @RequestBody CreateAuthorDto dto){
        return ResponseEntity.status(HttpStatus.CREATED).body(authorApplicationService.create(dto));
    }
    @GetMapping("/popular")
    @Operation(summary = "Get most popular authors by total rent count")
    public ResponseEntity<List<DisplayPopularAuthorDto>> findMostPopular(@RequestParam (defaultValue = "5") int limit){
        return ResponseEntity.ok(
                activityLogRepository.findMostPopularAuthors(PageRequest.of(0, limit)).stream().map(DisplayPopularAuthorDto::from).toList()
        );
    }
}
