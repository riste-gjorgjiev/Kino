package mk.ukim.finki.ecommerce.ecommercelab.model.exceptions;

public class BookNotFoundException extends RuntimeException {
    public BookNotFoundException(Long id) {
        super("Book with ID " + id + " hasn't been found");
    }
}
