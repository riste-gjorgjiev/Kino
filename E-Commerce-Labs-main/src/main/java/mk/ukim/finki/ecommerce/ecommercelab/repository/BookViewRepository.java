package mk.ukim.finki.ecommerce.ecommercelab.repository;

import mk.ukim.finki.ecommerce.ecommercelab.model.views.BookView;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BookViewRepository extends JpaRepository<BookView, Long> {}
