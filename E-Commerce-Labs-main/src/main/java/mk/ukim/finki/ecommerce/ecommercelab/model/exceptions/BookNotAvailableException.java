package mk.ukim.finki.ecommerce.ecommercelab.model.exceptions;

public class BookNotAvailableException extends RuntimeException {
    public BookNotAvailableException(Long id) {
        super("Book with ID " + id + " isn't available");
    }
}
