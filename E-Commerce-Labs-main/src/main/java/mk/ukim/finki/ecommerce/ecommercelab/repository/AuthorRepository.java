package mk.ukim.finki.ecommerce.ecommercelab.repository;

import mk.ukim.finki.ecommerce.ecommercelab.model.domain.Author;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AuthorRepository extends JpaRepository<Author, Long> {
}
