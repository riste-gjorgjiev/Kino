package mk.ukim.finki.ecommerce.ecommercelab.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import mk.ukim.finki.ecommerce.ecommercelab.model.dto.DisplayActivityLogDto;
import mk.ukim.finki.ecommerce.ecommercelab.repository.ActivityLogRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/activity-logs")
@Tag(name = "Activity Logs", description = "Endpoint for viewing rental activity logs")
@RequiredArgsConstructor
public class ActivityLogController {
    private final ActivityLogRepository activityLogRepository;

    @GetMapping
    @Operation(summary = "Get activity logs with pagination")
    public ResponseEntity<Page<DisplayActivityLogDto>> findAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ){
        return ResponseEntity.ok(
                activityLogRepository.findAll(PageRequest.of(page, size, Sort.by("eventTime").descending())).map(DisplayActivityLogDto::from)
        );
    }
}
