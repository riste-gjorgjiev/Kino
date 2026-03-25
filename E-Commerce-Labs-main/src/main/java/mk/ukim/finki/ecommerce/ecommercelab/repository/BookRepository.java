package mk.ukim.finki.ecommerce.ecommercelab.repository;

import jakarta.persistence.Entity;
import mk.ukim.finki.ecommerce.ecommercelab.model.domain.Book;
import mk.ukim.finki.ecommerce.ecommercelab.model.enums.BookCategory;
import mk.ukim.finki.ecommerce.ecommercelab.model.enums.BookState;
import mk.ukim.finki.ecommerce.ecommercelab.model.projection.BookExtendedProjection;
import mk.ukim.finki.ecommerce.ecommercelab.model.projection.BookShortProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {
    List<Book> findByCategory(BookCategory category);
    List<Book> findByState(BookState state);
    List<Book> findByAuthorId (Long authorId);

    List<BookShortProjection> findAllProjectedBy();
    List<BookExtendedProjection> findAllExtendedProjectedBy();

    @EntityGraph(attributePaths = {"author", "author.country"})
    Optional<Book> findWithAuthorAndCountryById(Long id);

    @EntityGraph(attributePaths = {"author", "author.country"})
    List<Book> findWithAuthorAndCountryBy();

    @Query("""
        SELECT b FROM Book b
        WHERE (:category IS NULL OR b.category = :category)
          AND (:state IS NULL OR b.state = :state)
          AND (:authorId IS NULL OR b.author.id = :authorId)
          AND (:availableOnly = false OR b.availableCopies > 0)
        """)
    Page<BookShortProjection> findAllFiltered(@Param("category") BookCategory category,
                               @Param("state") BookState state,
                               @Param("authorId") Long authorId,
                               @Param("availableOnly") boolean availableOnly,
                               Pageable pageable);
}
