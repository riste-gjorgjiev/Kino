package mk.ukim.finki.ecommerce.ecommercelab.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import mk.ukim.finki.ecommerce.ecommercelab.model.dto.CreateBookDto;
import mk.ukim.finki.ecommerce.ecommercelab.model.dto.DisplayBookDto;
import mk.ukim.finki.ecommerce.ecommercelab.model.dto.DisplayBookExtendedDto;
import mk.ukim.finki.ecommerce.ecommercelab.model.dto.DisplayPopularBookDto;
import mk.ukim.finki.ecommerce.ecommercelab.model.enums.BookCategory;
import mk.ukim.finki.ecommerce.ecommercelab.model.enums.BookState;
import mk.ukim.finki.ecommerce.ecommercelab.model.projection.BookShortProjection;
import mk.ukim.finki.ecommerce.ecommercelab.model.views.BookCategoryStats;
import mk.ukim.finki.ecommerce.ecommercelab.model.views.BookView;
import mk.ukim.finki.ecommerce.ecommercelab.repository.ActivityLogRepository;
import mk.ukim.finki.ecommerce.ecommercelab.repository.BookCategoryStatsRepository;
import mk.ukim.finki.ecommerce.ecommercelab.repository.BookViewRepository;
import mk.ukim.finki.ecommerce.ecommercelab.service.application.BookApplicationService;
import org.apache.coyote.Response;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/books")
@Tag(name = "Books", description = "Endpoints for managing library books")
public class BookController {
    private final BookApplicationService bookApplicationService;
    private final BookViewRepository bookViewRepository;
    private final BookCategoryStatsRepository bookCategoryStatsRepository;
    private final ActivityLogRepository activityLogRepository;

    public BookController(BookApplicationService bookApplicationService, BookViewRepository bookViewRepository, BookCategoryStatsRepository bookCategoryStatsRepository, ActivityLogRepository activityLogRepository) {
        this.bookApplicationService = bookApplicationService;
        this.bookViewRepository = bookViewRepository;
        this.bookCategoryStatsRepository = bookCategoryStatsRepository;
        this.activityLogRepository = activityLogRepository;
    }

    @GetMapping("/paged")
    @Operation(summary = "List books with pagination, sorting and filtering")
    public ResponseEntity<Page<BookShortProjection>> findAllPaged(
            @RequestParam(required = false) BookCategory category,
            @RequestParam(required = false) BookState state,
            @RequestParam(required = false) Long authorId,
            @RequestParam(defaultValue = "false") boolean availableOnly,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "name") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir
            ){
        Sort sort = sortDir.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        return ResponseEntity.ok(bookApplicationService.findAllFiltered(category, state, authorId, availableOnly, PageRequest.of(page, size, sort)));
    }
    @GetMapping("/extended")
    @Operation(summary = "Get all books with author and country info")
    public ResponseEntity<List<DisplayBookExtendedDto>> findAllExtended(){
        return ResponseEntity.ok(bookApplicationService.findAllExtended());
    }
    @GetMapping("/popular")
    @Operation(summary = "Get most popular books by rent count")
    public ResponseEntity<List<DisplayPopularBookDto>> findMostPopular(@RequestParam (defaultValue = "5") int limit){
        return ResponseEntity.ok(
                activityLogRepository.findMostPopularBooks(PageRequest.of(0, limit)).stream().map(DisplayPopularBookDto::from).toList()
        );
    }

    @GetMapping("/view")
    @Operation(summary = "Get books from database view")
    public ResponseEntity<List<BookView>> findAllFromView() {
        return ResponseEntity.ok(bookViewRepository.findAll());
    }
    @GetMapping("/stats")
    @Operation(summary = "Get books from database view")
    public ResponseEntity<List<BookCategoryStats>> getCategoryStats(){
        return ResponseEntity.ok(bookCategoryStatsRepository.findAll());
    }
    @GetMapping("/{id}")
    @Operation(summary = "Get book by ID")
    public ResponseEntity<DisplayBookDto> findById(@PathVariable Long id){
        return ResponseEntity.ok(bookApplicationService.findById(id));
    }

    @Operation(summary = "Add a new book")
    public ResponseEntity<DisplayBookDto> create(@Valid @RequestBody CreateBookDto dto){
        return ResponseEntity.status(HttpStatus.CREATED).body(bookApplicationService.create(dto));
    }

    @PutMapping("/edit/{id}")
    @Operation(summary = "Update an existing book")
    public ResponseEntity<DisplayBookDto> update(@PathVariable Long id, @Valid @RequestBody CreateBookDto dto){
        return ResponseEntity.ok(bookApplicationService.update(id, dto));
    }

    @DeleteMapping("/delete/{id}")
    @Operation(summary = "Delete a book that is no longer in good condition")
    public ResponseEntity<Void> delete(@PathVariable Long id){
        bookApplicationService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/rent/{id}")
    @Operation(summary = "Mark a book as rented (decreases available copies by 1)")
    public ResponseEntity<DisplayBookDto> rent(@PathVariable Long id){
        return ResponseEntity.ok(bookApplicationService.rent(id));
    }
}
