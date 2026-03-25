package mk.ukim.finki.ecommerce.ecommercelab.model.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import mk.ukim.finki.ecommerce.ecommercelab.model.enums.BookCategory;
import mk.ukim.finki.ecommerce.ecommercelab.model.enums.BookState;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "books")
public class Book extends BaseAuditableEntity{
    @Column(nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BookCategory category;

    @ManyToOne
    @JoinColumn(name = "author_id", nullable = false)
    private Author author;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BookState state;

    @Column(nullable = false)
    private Integer availableCopies;

    public Book(String name, BookCategory category, Author author, BookState state, Integer availableCopies) {
        this.name = name;
        this.category = category;
        this.author = author;
        this.state = state;
        this.availableCopies = availableCopies;
    }
}
