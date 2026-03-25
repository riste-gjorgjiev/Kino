package mk.ukim.finki.ecommerce.ecommercelab.repository;

import mk.ukim.finki.ecommerce.ecommercelab.model.views.BookCategoryStats;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BookCategoryStatsRepository extends JpaRepository<BookCategoryStats, Long> {}
