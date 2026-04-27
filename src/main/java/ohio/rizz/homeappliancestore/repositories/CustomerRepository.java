package ohio.rizz.homeappliancestore.repositories;

import ohio.rizz.homeappliancestore.entities.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface CustomerRepository extends JpaRepository<Customer, UUID> {
    List<Customer> findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCaseOrEmailContainingIgnoreCase(
            String firstName, String lastName, String email);

    @Query("SELECT c FROM Customer c WHERE c.email = :email")
    Customer findByEmail(@Param("email") String email);

    @Query("SELECT COUNT(c) FROM Customer c WHERE CAST(c.createdAt AS LocalDate) = :date")
    int countNewCustomersOnDate(@Param("date") LocalDate date);

    long countByCreatedAtBetween(LocalDateTime start, LocalDateTime end);
}
