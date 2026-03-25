package mk.ukim.finki.ecommerce.ecommercelab.model.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import mk.ukim.finki.ecommerce.ecommercelab.model.enums.BookCategory;
import mk.ukim.finki.ecommerce.ecommercelab.model.enums.BookState;

public record CreateBookDto(
        @NotBlank(message = "Book name must not be blank") String name,
        @NotNull(message = "Category must not be null") BookCategory bookCategory,
        @NotNull(message = "Author ID must not be null") Long authorId,
        @NotNull(message = "State must not be null") BookState bookState,
        @NotNull(message = "Available copies must not be null")
        @Min(value = 0, message = "Available copies cannot be negative") Integer copies
) {
}