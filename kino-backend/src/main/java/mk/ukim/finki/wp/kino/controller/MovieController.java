package mk.ukim.finki.wp.kino.controller;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import mk.ukim.finki.wp.kino.dto.api.MediaCardDto;
import mk.ukim.finki.wp.kino.dto.api.MediaFilterDto;
import mk.ukim.finki.wp.kino.dto.api.PagedResponseDto;
import mk.ukim.finki.wp.kino.dto.tmdb.details.MediaDetailsDto;
import mk.ukim.finki.wp.kino.service.MediaDetailsService;
import mk.ukim.finki.wp.kino.service.MovieService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Validated
@RestController
@RequestMapping("/api/movies")
public class MovieController {
    private final MovieService movieService;
    private final MediaDetailsService mediaDetailsService;

    public MovieController(MovieService movieService, MediaDetailsService mediaDetailsService) {
        this.movieService = movieService;
        this.mediaDetailsService = mediaDetailsService;
    }

    @GetMapping("/{id}")
    public MediaDetailsDto details(@PathVariable @Positive long id){
        return mediaDetailsService.getMovieDetails(id);
    }

    @GetMapping("/trending")
    public PagedResponseDto<MediaCardDto> trending(
            @RequestParam(defaultValue = "day") @Pattern(regexp = "day|week", message = "must be 'day' or 'week'") String window,
            @RequestParam(defaultValue = "1") @Min(value = 1, message = "must be at least 1") int page,
            @RequestParam(required = false) Integer yearFrom,
            @RequestParam(required = false) Integer yearTo,
            @RequestParam(required = false) String sortBy,
            @RequestParam(defaultValue = "asc") String sortOrder
    ){
        MediaFilterDto filter = new MediaFilterDto(yearFrom, yearTo, sortBy, sortOrder);
        return movieService.getTrendingMovies(window, page, filter);
    }

    @GetMapping("/popular")
    public PagedResponseDto<MediaCardDto> popular(
            @RequestParam(defaultValue = "1") @Min(value = 1, message = "must be at least 1") int page,
            @RequestParam(required = false) Integer yearFrom,
            @RequestParam(required = false) Integer yearTo,
            @RequestParam(required = false) String sortBy,
            @RequestParam(defaultValue = "asc") String sortOrder
    ){
        MediaFilterDto filter = new MediaFilterDto(yearFrom, yearTo, sortBy, sortOrder);
        return movieService.getPopularMovies(page, filter);
    }

    @GetMapping("/top-rated")
    public PagedResponseDto<MediaCardDto> topRated(
            @RequestParam(defaultValue = "1") @Min(value = 1, message = "must be at least 1") int page,
            @RequestParam(required = false) Integer yearFrom,
            @RequestParam(required = false) Integer yearTo,
            @RequestParam(required = false) String sortBy,
            @RequestParam(defaultValue = "asc") String sortOrder
    ){
        MediaFilterDto filter = new MediaFilterDto(yearFrom, yearTo, sortBy, sortOrder);
        return movieService.getTopRatedMovies(page, filter);
    }

    @GetMapping("/upcoming")
    public PagedResponseDto<MediaCardDto> upcoming(
            @RequestParam(defaultValue = "1") @Min(value = 1, message = "must be at least 1") int page,
            @RequestParam(required = false) Integer yearFrom,
            @RequestParam(required = false) Integer yearTo,
            @RequestParam(required = false) String sortBy,
            @RequestParam(defaultValue = "asc") String sortOrder

    ){
        MediaFilterDto filter = new MediaFilterDto(yearFrom, yearTo, sortBy, sortOrder);
        return movieService.getUpcomingMovies(page, filter);
    }
}
