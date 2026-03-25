package mk.ukim.finki.ecommerce.ecommercelab.model.dto;

import mk.ukim.finki.ecommerce.ecommercelab.model.domain.Book;
import mk.ukim.finki.ecommerce.ecommercelab.model.enums.BookCategory;
import mk.ukim.finki.ecommerce.ecommercelab.model.enums.BookState;

public record DisplayBookShortDto(Long id, String name, BookCategory category, BookState state, Integer availableCopies) {
    public static DisplayBookShortDto from(Book book){
        return new DisplayBookShortDto(book.getId(), book.getName(), book.getCategory(), book.getState(), book.getAvailableCopies());
    }
}
