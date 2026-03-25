package mk.ukim.finki.ecommerce.ecommercelab.model.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "activity_logs")
public class ActivityLog {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false) private String bookName;
    @Column(nullable = false) private LocalDateTime eventTime;
    @Column(nullable = false) private String eventType;

    public ActivityLog(String bookName, LocalDateTime eventTime, String eventType) {
        this.bookName = bookName;
        this.eventTime = eventTime;
        this.eventType = eventType;
    }
}
