package mk.ukim.finki.wp.kino.dto.api;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class MediaFilterDto {
    private Integer yearFrom;
    private Integer yearTo;
    private String sortBy;
    private String sortOrder;
}
