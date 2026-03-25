package mk.ukim.finki.ecommerce.ecommercelab.model.exceptions;

public class CountryNotFoundException extends RuntimeException {
    public CountryNotFoundException(Long id) {
        super("The country with ID " + id + " hasn't been found");
    }
}
