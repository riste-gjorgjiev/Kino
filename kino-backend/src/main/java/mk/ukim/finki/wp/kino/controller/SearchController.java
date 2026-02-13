package mk.ukim.finki.wp.kino.controller;

import mk.ukim.finki.wp.kino.dto.api.MediaCardDto;
import mk.ukim.finki.wp.kino.dto.api.PagedResponseDto;
import mk.ukim.finki.wp.kino.service.SearchService;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/api/search")
public class SearchController {
    private final SearchService searchService;

    public SearchController(SearchService searchService) {
        this.searchService = searchService;
    }

    @GetMapping("/movie")
    public PagedResponseDto<MediaCardDto> searchedMovies(
            @RequestParam(required = true) String query,
            @RequestParam(defaultValue = "1") int page
    ){
        return searchService.searchMovies(query, page);
    }

    @GetMapping("/tv")
    public PagedResponseDto<MediaCardDto> searchedTv(
            @RequestParam(required = true) String query,
            @RequestParam(defaultValue = "1") int page
    ){
        return searchService.searchTv(query, page);
    }

    @GetMapping("/all")
    public PagedResponseDto<MediaCardDto> searchedAll(
            @RequestParam(required = true) String query,
            @RequestParam(defaultValue = "1") int page
    ){
        return searchService.searchMulti(query, page);
    }
}
