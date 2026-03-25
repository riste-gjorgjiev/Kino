package mk.ukim.finki.ecommerce.ecommercelab.service.domain;

import mk.ukim.finki.ecommerce.ecommercelab.model.domain.Author;
import mk.ukim.finki.ecommerce.ecommercelab.model.domain.Country;

import java.util.List;
import java.util.Optional;

public interface AuthorService {
    List<Author> findAll();
    Optional<Author> findById(Long id);
    Author create (String name, String surname, Country country);
}
