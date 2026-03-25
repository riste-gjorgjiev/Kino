package mk.ukim.finki.ecommerce.ecommercelab.model.events;

import mk.ukim.finki.ecommerce.ecommercelab.model.domain.Book;
import org.springframework.context.ApplicationEvent;

public class BookRentedEvent extends ApplicationEvent {
    private final Book book;
    public BookRentedEvent(Object source, Book book) {
        super(source);
        this.book = book;
    }
    public Book getBook(){
        return book;
    }
}
