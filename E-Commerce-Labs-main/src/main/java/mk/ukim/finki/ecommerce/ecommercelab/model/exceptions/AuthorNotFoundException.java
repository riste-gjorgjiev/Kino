package mk.ukim.finki.ecommerce.ecommercelab.model.exceptions;

public class AuthorNotFoundException extends RuntimeException {
    public AuthorNotFoundException(Long id) {
        super("Author with ID " + id + " hasn't been found");
    }
}
