package ohio.rizz.homeappliancestore.repositories;

import ohio.rizz.homeappliancestore.entities.Customer;
import ohio.rizz.homeappliancestore.entities.Supplier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface SupplierRepository extends JpaRepository<Supplier, Long> {
    @Query("SELECT c FROM Supplier c WHERE c.email = :email")
    Supplier findByEmail(@Param("email") String email);
}
