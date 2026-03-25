package mk.ukim.finki.ecommerce.ecommercelab.model.projection;

import mk.ukim.finki.ecommerce.ecommercelab.model.enums.BookCategory;
import mk.ukim.finki.ecommerce.ecommercelab.model.enums.BookState;

public interface BookExtendedProjection {
    Long getId();
    String getName();
    BookCategory getCategory();
    BookState getState();
    Integer getAvailableCopies();
    AuthorInfo getAuthor();

     interface AuthorInfo {
        String getName();
        String getSurname();
        CountryInfo getCountry();
     }
    interface CountryInfo {
        String getName();
    }
}
