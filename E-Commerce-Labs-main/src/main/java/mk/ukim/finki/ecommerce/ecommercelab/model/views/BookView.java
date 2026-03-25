package mk.ukim.finki.ecommerce.ecommercelab.model.views;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import org.hibernate.annotations.Immutable;

@Getter @Entity @Immutable
@Table(name = "v_book_details")
public class BookView {
    @Id
    private Long id;
    @Column(name = "book_name")
    private String bookName;
    private String category;
    private String state;
    @Column(name = "available_copies")    private Integer availableCopies;
    @Column(name = "author_full_name")    private String authorFullName;
    @Column(name = "country_name")    private String countryName;
}
