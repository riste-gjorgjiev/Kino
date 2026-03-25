package mk.ukim.finki.ecommerce.ecommercelab.model.listener;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mk.ukim.finki.ecommerce.ecommercelab.model.domain.ActivityLog;
import mk.ukim.finki.ecommerce.ecommercelab.model.domain.Book;
import mk.ukim.finki.ecommerce.ecommercelab.model.events.BookRentedEvent;
import mk.ukim.finki.ecommerce.ecommercelab.repository.ActivityLogRepository;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Slf4j
@Component
@RequiredArgsConstructor
public class BookRentedEventListener {
    private final ActivityLogRepository activityLogRepository;

    @EventListener
    public void handleBookRented(BookRentedEvent event){
        Book book = event.getBook();
        log.info("Book rented: '{}' (id={}), available copies now: {}", book.getName(), book.getId(), book.getAvailableCopies());

        activityLogRepository.save(new ActivityLog(book.getName(), LocalDateTime.now(), "BOOK_RENTED"));

        if (book.getAvailableCopies() == 0){
            log.warn("Book '{}' (id={}) is now UNAVAILABLE - no copies left.", book.getName(), book.getId());
            activityLogRepository.save(new ActivityLog(book.getName(), LocalDateTime.now(), "BOOK_UNAVAILABLE"));
        }
    }
}
