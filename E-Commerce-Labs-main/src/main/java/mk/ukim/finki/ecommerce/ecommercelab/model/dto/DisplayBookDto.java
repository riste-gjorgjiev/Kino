package mk.ukim.finki.ecommerce.ecommercelab.model.dto;

import mk.ukim.finki.ecommerce.ecommercelab.model.domain.Book;
import mk.ukim.finki.ecommerce.ecommercelab.model.dto.DisplayAuthorDto;
import mk.ukim.finki.ecommerce.ecommercelab.model.enums.BookCategory;
import mk.ukim.finki.ecommerce.ecommercelab.model.enums.BookState;

import java.time.LocalDateTime;

public record DisplayBookDto(Long id, LocalDateTime createdAt, LocalDateTime updatedAt,
                             String name, BookCategory bookCategory, mk.ukim.finki.ecommerce.ecommercelab.model.dto.DisplayAuthorDto authorDto,
                             BookState state, Integer availableCopies) {
    public static mk.ukim.finki.ecommerce.ecommercelab.model.dto.DisplayBookDto from(Book book){
        return new mk.ukim.finki.ecommerce.ecommercelab.model.dto.DisplayBookDto(
                book.getId(), book.getCreatedAt(), book.getUpdatedAt(),
                book.getName(), book.getCategory(),
                DisplayAuthorDto.from(book.getAuthor()),
                book.getState(), book.getAvailableCopies()
        );
    }
}
