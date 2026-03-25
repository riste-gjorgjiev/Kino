package mk.ukim.finki.ecommerce.ecommercelab.service.domain;

import mk.ukim.finki.ecommerce.ecommercelab.model.domain.Author;
import mk.ukim.finki.ecommerce.ecommercelab.model.domain.Book;
import mk.ukim.finki.ecommerce.ecommercelab.model.enums.BookCategory;
import mk.ukim.finki.ecommerce.ecommercelab.model.enums.BookState;
import mk.ukim.finki.ecommerce.ecommercelab.model.projection.BookShortProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface BookService {
    List<Book> findAll();
    Optional<Book> findById(Long id);
    Book create(String name, BookCategory category, Author author, BookState state, Integer availableCopies);
    Book update(Long id, String name, BookCategory category, Author author, BookState state, Integer availableCopies);
    void deleteById(Long id);
    Book rentBook(Long id);

}
