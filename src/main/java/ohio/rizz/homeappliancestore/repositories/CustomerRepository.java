package ohio.rizz.homeappliancestore.repositories;

import ohio.rizz.homeappliancestore.entities.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CustomerRepository extends JpaRepository<Customer, Long> {
    List<Customer> findByFirstNameContainingOrLastNameContainingOrEmailContaining(
            String firstName, String lastName, String email);
}
