package mk.ukim.finki.ecommerce.ecommercelab.model.dto;

import mk.ukim.finki.ecommerce.ecommercelab.model.domain.ActivityLog;

import java.time.LocalDateTime;

public record DisplayActivityLogDto(Long id, String bookName, LocalDateTime eventTime, String eventType) {
    public static DisplayActivityLogDto from(ActivityLog activityLog){
        return new DisplayActivityLogDto(activityLog.getId(), activityLog.getBookName(), activityLog.getEventTime(), activityLog.getEventType());
    }
}
