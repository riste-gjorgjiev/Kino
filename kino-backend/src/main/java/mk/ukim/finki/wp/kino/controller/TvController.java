package mk.ukim.finki.wp.kino.controller;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import mk.ukim.finki.wp.kino.dto.api.MediaCardDto;
import mk.ukim.finki.wp.kino.dto.api.MediaFilterDto;
import mk.ukim.finki.wp.kino.dto.api.PagedResponseDto;
import mk.ukim.finki.wp.kino.dto.api.MediaDetailsDto;
import mk.ukim.finki.wp.kino.dto.api.EpisodeDto;
import mk.ukim.finki.wp.kino.dto.api.SeasonDetailsDto;
import mk.ukim.finki.wp.kino.dto.api.SeasonDto;
import mk.ukim.finki.wp.kino.service.MediaDetailsService;
import mk.ukim.finki.wp.kino.service.TvSeasonService;
import mk.ukim.finki.wp.kino.service.TvService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Validated
@RestController
@RequestMapping("/api/tv")
public class TvController {
    private final TvService tvService;
    private final MediaDetailsService mediaDetailsService;
    private final TvSeasonService tvSeasonService;

    public TvController(TvService tvService, MediaDetailsService mediaDetailsService, TvSeasonService tvSeasonService) {
        this.tvService = tvService;
        this.mediaDetailsService = mediaDetailsService;
        this.tvSeasonService = tvSeasonService;
    }

    @GetMapping("/{id}")
    public MediaDetailsDto details(@PathVariable @Positive long id){
        return mediaDetailsService.getTvDetails(id);
    }

    @GetMapping("/{id}/seasons")
    public List<SeasonDto> seasons(@PathVariable @Positive long id){
        return tvSeasonService.getSeasons(id);
    }

    @GetMapping("/{id}/seasons/{seasonNumber}")
    public SeasonDetailsDto seasonDetails(
            @PathVariable @Positive long id,
            @PathVariable @Min(value = 0, message = "must be at least 0") int seasonNumber
    ){
        return tvSeasonService.getSeasonDetails(id, seasonNumber);
    }

    @GetMapping("/{id}/seasons/{seasonNumber}/episodes")
    public List<EpisodeDto> episodes(
            @PathVariable @Positive long id,
            @PathVariable @Min(value = 0, message = "must be at least 0") int seasonNumber
    ){
        return tvSeasonService.getEpisodes(id, seasonNumber);
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
        return tvService.getTrendingTvShows(window, page, filter);
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
        return tvService.getPopularTvShows(page, filter);
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
        return tvService.getTopRatedTvShows(page, filter);
    }

    @GetMapping("/on-the-air")
    public PagedResponseDto<MediaCardDto> onTheAir(
            @RequestParam(defaultValue = "1") @Min(value = 1, message = "must be at least 1") int page,
            @RequestParam(required = false) Integer yearFrom,
            @RequestParam(required = false) Integer yearTo,
            @RequestParam(required = false) String sortBy,
            @RequestParam(defaultValue = "asc") String sortOrder
    ){
        MediaFilterDto filter = new MediaFilterDto(yearFrom, yearTo, sortBy, sortOrder);
        return tvService.getAiringTvShows(page, filter);
    }
}
