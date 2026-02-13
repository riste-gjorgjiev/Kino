package mk.ukim.finki.wp.kino.controller;

import mk.ukim.finki.wp.kino.dto.api.MediaCardDto;
import mk.ukim.finki.wp.kino.dto.api.PagedResponseDto;
import mk.ukim.finki.wp.kino.dto.tmdb.details.MediaDetailsDto;
import mk.ukim.finki.wp.kino.service.MediaDetailsService;
import mk.ukim.finki.wp.kino.service.MovieService;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "http://localhost:4200")
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
    public MediaDetailsDto details(@PathVariable long id){
        return mediaDetailsService.getMovieDetails(id);
    }

    @GetMapping("/trending")
    public PagedResponseDto<MediaCardDto> trending(
            @RequestParam(defaultValue = "day") String window,
            @RequestParam(defaultValue = "1") int page
    ){
        return movieService.getTrendingMovies(window, page);
    }

    @GetMapping("/popular")
    public PagedResponseDto<MediaCardDto> popular(
            @RequestParam(defaultValue = "1") int page
    ){
        return movieService.getPopularMovies(page);
    }
    @GetMapping("/top-rated")
    public PagedResponseDto<MediaCardDto> topRated(
            @RequestParam(defaultValue = "1") int page
    ){
        return movieService.getTopRatedMovies(page);
    }
    @GetMapping("/upcoming")
    public PagedResponseDto<MediaCardDto> upcoming(
            @RequestParam(defaultValue = "1") int page
    ){
        return movieService.getUpcomingMovies(page);
    }
}
