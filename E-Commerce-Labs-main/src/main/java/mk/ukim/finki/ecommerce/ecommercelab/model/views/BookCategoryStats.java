package mk.ukim.finki.ecommerce.ecommercelab.model.views;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import org.hibernate.annotations.Immutable;

@Getter @Entity @Immutable
@Table(name = "mv_book_category_stats")
public class BookCategoryStats {
    @Id @Column(name = "category") private String category;
    @Column(name = "total_books") private Long totalBooks;
    @Column(name = "total_available_copies") private Long totalAvailableCopies;
    @Column(name = "books_in_bad_condition") private Long booksInBadCondition;
}
