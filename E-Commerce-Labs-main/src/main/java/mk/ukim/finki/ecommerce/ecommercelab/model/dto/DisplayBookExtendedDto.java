package mk.ukim.finki.ecommerce.ecommercelab.model.dto;

import mk.ukim.finki.ecommerce.ecommercelab.model.domain.Book;
import mk.ukim.finki.ecommerce.ecommercelab.model.enums.BookCategory;
import mk.ukim.finki.ecommerce.ecommercelab.model.enums.BookState;

public record DisplayBookExtendedDto(Long id, String name, BookCategory category,
                                     BookState state, Integer availableCopies,
                                     String authorName, String authorSurname, String authorCountry) {
    public static DisplayBookExtendedDto from(Book book){
        return new DisplayBookExtendedDto(book.getId(), book.getName(), book.getCategory(), book.getState(), book.getAvailableCopies(),
                book.getAuthor().getName(), book.getAuthor().getSurname(), book.getAuthor().getCountry().getName());
    }
}
