package mk.ukim.finki.ecommerce.ecommercelab.service.domain;

import lombok.RequiredArgsConstructor;
import mk.ukim.finki.ecommerce.ecommercelab.model.domain.Author;
import mk.ukim.finki.ecommerce.ecommercelab.model.domain.Book;
import mk.ukim.finki.ecommerce.ecommercelab.model.enums.BookCategory;
import mk.ukim.finki.ecommerce.ecommercelab.model.enums.BookState;
import mk.ukim.finki.ecommerce.ecommercelab.model.events.BookRentedEvent;
import mk.ukim.finki.ecommerce.ecommercelab.model.exceptions.BookNotAvailableException;
import mk.ukim.finki.ecommerce.ecommercelab.model.exceptions.BookNotFoundException;
import mk.ukim.finki.ecommerce.ecommercelab.model.projection.BookShortProjection;
import mk.ukim.finki.ecommerce.ecommercelab.repository.BookRepository;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BookServiceImpl implements BookService{
    private final BookRepository bookRepository;
    private final ApplicationEventPublisher eventPublisher;
    @Override
    public List<Book> findAll() {
        return bookRepository.findAll();
    }

    @Override
    public Optional<Book> findById(Long id) {
        return bookRepository.findById(id);
    }

    @Override
    public Book create(String name, BookCategory category, Author author, BookState state, Integer availableCopies) {
        return bookRepository.save(new Book(name, category, author, state, availableCopies));
    }

    @Override
    public Book update(Long id, String name, BookCategory category, Author author, BookState state, Integer availableCopies) {
        Book book = bookRepository.findById(id).orElseThrow(() -> new BookNotFoundException(id));
        book.setName(name);
        book.setCategory(category);
        book.setAuthor(author);
        book.setState(state);
        book.setAvailableCopies(availableCopies);
        return bookRepository.save(book);
    }

    @Override
    public void deleteById(Long id) {
        if (!bookRepository.existsById(id)){
            throw new BookNotFoundException(id);
        }
        bookRepository.deleteById(id);
    }

    @Override
    public Book rentBook(Long id) {
        Book book = bookRepository.findById(id).orElseThrow(() -> new BookNotFoundException(id));
        if (book.getState() == BookState.BAD || book.getAvailableCopies() <= 0){
            throw new BookNotAvailableException(id);
        }
        book.setAvailableCopies(book.getAvailableCopies() - 1);
        Book saved = bookRepository.save(book);
        eventPublisher.publishEvent(new BookRentedEvent(this, saved));
        return saved;
    }
}
