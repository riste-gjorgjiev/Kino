package mk.ukim.finki.wp.kino.controller;

import mk.ukim.finki.wp.kino.dto.api.MediaCardDto;
import mk.ukim.finki.wp.kino.dto.api.PagedResponseDto;
import mk.ukim.finki.wp.kino.dto.tmdb.details.MediaDetailsDto;
import mk.ukim.finki.wp.kino.service.MediaDetailsService;
import mk.ukim.finki.wp.kino.service.TvService;
import org.springframework.web.bind.annotation.*;

import javax.print.attribute.standard.Media;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/api/tv")
public class TvController {
    private final TvService tvService;
    private final MediaDetailsService mediaDetailsService;

    public TvController(TvService tvService, MediaDetailsService mediaDetailsService) {
        this.tvService = tvService;
        this.mediaDetailsService = mediaDetailsService;
    }

    @GetMapping("/{id}")
    public MediaDetailsDto details(@PathVariable long id){
        return mediaDetailsService.getTvDetails(id);
    }

    @GetMapping("/trending")
    public PagedResponseDto<MediaCardDto> trending(
            @RequestParam(defaultValue = "day") String window,
            @RequestParam(defaultValue = "1") int page){
        return tvService.getTrendingTvShows(window, page);
    }
    @GetMapping("/popular")
    public PagedResponseDto<MediaCardDto> popular(
            @RequestParam(defaultValue = "1") int page
    ){
        return tvService.getPopularTvShows(page);
    }
    @GetMapping("/top-rated")
    public PagedResponseDto<MediaCardDto> topRated(
            @RequestParam(defaultValue = "1") int page
    ){
        return tvService.getTopRatedTvShows(page);
    }
    @GetMapping("/on-the-air")
    public PagedResponseDto<MediaCardDto> onTheAir(
            @RequestParam(defaultValue = "1") int page
    ){
        return tvService.getAiringTvShows(page);
    }
}
