package mk.ukim.finki.ecommerce.ecommercelab.service.application;

import mk.ukim.finki.ecommerce.ecommercelab.model.domain.Author;
import mk.ukim.finki.ecommerce.ecommercelab.model.domain.Book;
import mk.ukim.finki.ecommerce.ecommercelab.model.dto.CreateBookDto;
import mk.ukim.finki.ecommerce.ecommercelab.model.dto.DisplayBookDto;
import mk.ukim.finki.ecommerce.ecommercelab.model.dto.DisplayBookExtendedDto;
import mk.ukim.finki.ecommerce.ecommercelab.model.dto.DisplayBookShortDto;
import mk.ukim.finki.ecommerce.ecommercelab.model.enums.BookCategory;
import mk.ukim.finki.ecommerce.ecommercelab.model.enums.BookState;
import mk.ukim.finki.ecommerce.ecommercelab.model.exceptions.AuthorNotFoundException;
import mk.ukim.finki.ecommerce.ecommercelab.model.exceptions.BookNotFoundException;
import mk.ukim.finki.ecommerce.ecommercelab.model.projection.BookShortProjection;
import mk.ukim.finki.ecommerce.ecommercelab.model.views.BookCategoryStats;
import mk.ukim.finki.ecommerce.ecommercelab.model.views.BookView;
import mk.ukim.finki.ecommerce.ecommercelab.repository.BookRepository;
import mk.ukim.finki.ecommerce.ecommercelab.service.domain.AuthorService;
import mk.ukim.finki.ecommerce.ecommercelab.service.domain.BookService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BookApplicationServiceImpl implements BookApplicationService{
    private final BookService bookService;
    private final AuthorService authorService;
    private final BookRepository bookRepository;

    public BookApplicationServiceImpl(BookService bookService, AuthorService authorService, BookRepository bookRepository) {
        this.bookService = bookService;
        this.authorService = authorService;
        this.bookRepository = bookRepository;
    }

    @Override
    public DisplayBookDto findById(Long id) {
        return bookService.findById(id)
                .map(DisplayBookDto::from)
                .orElseThrow(() -> new BookNotFoundException(id));
    }

    @Override
    public DisplayBookDto create(CreateBookDto dto) {
        Author author = authorService.findById(dto.authorId())
                .orElseThrow(() -> new AuthorNotFoundException(dto.authorId()));
        Book book = bookService.create(dto.name(), dto.bookCategory(), author, dto.bookState(), dto.copies());
        return DisplayBookDto.from(book);
    }

    @Override
    public DisplayBookDto update(Long id, CreateBookDto dto) {
        Author author = authorService.findById(dto.authorId())
                .orElseThrow(() -> new AuthorNotFoundException(dto.authorId()));
        Book book = bookService.update(id, dto.name(), dto.bookCategory(), author, dto.bookState(), dto.copies());
        return DisplayBookDto.from(book);
    }

    @Override
    public void delete(Long id) {
        bookService.deleteById(id);
    }

    @Override
    public DisplayBookDto rent(Long id) {
        return DisplayBookDto.from(bookService.rentBook(id));
    }

    @Override
    public Page<BookShortProjection> findAllFiltered(BookCategory category, BookState state, Long authorId, boolean availableOnly, Pageable pageable) {
        return bookRepository.findAllFiltered(category, state, authorId, availableOnly, pageable);
    }


    @Override
    public List<DisplayBookExtendedDto> findAllExtended() {
        return bookRepository.findAllExtendedProjectedBy().stream()
                .map(p -> new DisplayBookExtendedDto(
                        p.getId(),
                        p.getName(),
                        p.getCategory(),
                        p.getState(),
                        p.getAvailableCopies(),
                        p.getAuthor().getName(),
                        p.getAuthor().getSurname(),
                        p.getAuthor().getCountry().getName()
                )).toList();
    }
}
