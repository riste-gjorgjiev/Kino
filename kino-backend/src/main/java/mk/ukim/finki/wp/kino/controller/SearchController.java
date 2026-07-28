package mk.ukim.finki.wp.kino.controller;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import mk.ukim.finki.wp.kino.dto.api.MediaCardDto;
import mk.ukim.finki.wp.kino.dto.api.MediaFilterDto;
import mk.ukim.finki.wp.kino.dto.api.PagedResponseDto;
import mk.ukim.finki.wp.kino.service.SearchService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Validated
@RestController
@RequestMapping("/api/search")
public class SearchController {
    private final SearchService searchService;

    public SearchController(SearchService searchService) {
        this.searchService = searchService;
    }

    @GetMapping("/movie")
    public PagedResponseDto<MediaCardDto> searchedMovies(
            @RequestParam(required = true) @NotBlank(message = "must not be blank") @Size(max = 200, message = "must be at most 200 characters") String query,
            @RequestParam(defaultValue = "1") @Min(value = 1, message = "must be at least 1") int page,
            @RequestParam(required = false) Integer yearFrom,
            @RequestParam(required = false) Integer yearTo,
            @RequestParam(required = false) String sortBy,
            @RequestParam(defaultValue = "asc") String sortOrder
    ){
        MediaFilterDto filter = new MediaFilterDto(yearFrom, yearTo, sortBy, sortOrder);
        return searchService.searchMovies(query, page, filter);
    }

    @GetMapping("/tv")
    public PagedResponseDto<MediaCardDto> searchedTv(
            @RequestParam(required = true) @NotBlank(message = "must not be blank") @Size(max = 200, message = "must be at most 200 characters") String query,
            @RequestParam(defaultValue = "1") @Min(value = 1, message = "must be at least 1") int page,
            @RequestParam(required = false) Integer yearFrom,
            @RequestParam(required = false) Integer yearTo,
            @RequestParam(required = false) String sortBy,
            @RequestParam(defaultValue = "asc") String sortOrder
    ){
        MediaFilterDto filter = new MediaFilterDto(yearFrom, yearTo, sortBy, sortOrder);
        return searchService.searchTv(query, page, filter);
    }

    @GetMapping("/all")
    public PagedResponseDto<MediaCardDto> searchedAll(
            @RequestParam(required = true) @NotBlank(message = "must not be blank") @Size(max = 200, message = "must be at most 200 characters") String query,
            @RequestParam(defaultValue = "1") @Min(value = 1, message = "must be at least 1") int page,
            @RequestParam(required = false) Integer yearFrom,
            @RequestParam(required = false) Integer yearTo,
            @RequestParam(required = false) String sortBy,
            @RequestParam(defaultValue = "asc") String sortOrder
    ){
        MediaFilterDto filter = new MediaFilterDto(yearFrom, yearTo, sortBy, sortOrder);
        return searchService.searchMulti(query, page, filter);
    }
}
