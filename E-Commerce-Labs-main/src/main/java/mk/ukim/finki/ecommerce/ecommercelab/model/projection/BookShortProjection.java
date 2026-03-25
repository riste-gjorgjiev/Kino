package mk.ukim.finki.ecommerce.ecommercelab.model.projection;

import mk.ukim.finki.ecommerce.ecommercelab.model.enums.BookCategory;
import mk.ukim.finki.ecommerce.ecommercelab.model.enums.BookState;

public interface BookShortProjection {
    Long getId();
    String getName();
    BookCategory getCategory();
    BookState getState();
    Integer getAvailableCopies();
}
