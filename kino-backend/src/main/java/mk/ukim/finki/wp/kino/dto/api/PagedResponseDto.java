package mk.ukim.finki.wp.kino.dto.api;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor

public class PagedResponseDto<T> {
    private int page;
    private int totalPages;
    private List<T> items;
    private Integer totalResults;

    public PagedResponseDto(){}

    public PagedResponseDto(int page, int totalPages, List<T> items) {
        this.page = page;
        this.totalPages = totalPages;
        this.items = items;
    }

}
