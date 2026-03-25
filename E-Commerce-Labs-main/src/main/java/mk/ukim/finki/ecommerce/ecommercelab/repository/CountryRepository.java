package mk.ukim.finki.ecommerce.ecommercelab.repository;

import mk.ukim.finki.ecommerce.ecommercelab.model.domain.Country;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CountryRepository extends JpaRepository<Country, Long> {

}
