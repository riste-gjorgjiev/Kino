package mk.ukim.finki.ecommerce.ecommercelab.repository;

import mk.ukim.finki.ecommerce.ecommercelab.model.domain.ActivityLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ActivityLogRepository extends JpaRepository<ActivityLog, Long> {
    Page<ActivityLog> findAll(Pageable pageable);

    @Query("SELECT a.bookName, COUNT(a) as rentCount FROM ActivityLog a " +
            "WHERE a.eventType = 'BOOK_RENTED' " +
            "GROUP BY a.bookName ORDER BY rentCount DESC")
    List<Object[]> findMostPopularBooks(Pageable pageable);

    @Query(value = "SELECT CONCAT(au.name, ' ', au.surname) as authorName, COUNT(al.id) as rentCount " +
            "FROM activity_logs al " +
            "JOIN books b ON b.name = al.book_name " +
            "JOIN authors au ON au.id = b.author_id " +
            "WHERE al.event_type = 'BOOK_RENTED' " +
            "GROUP BY au.name, au.surname " +
            "ORDER BY rentCount DESC",
            nativeQuery = true)
    List<Object[]> findMostPopularAuthors(Pageable pageable);
}
