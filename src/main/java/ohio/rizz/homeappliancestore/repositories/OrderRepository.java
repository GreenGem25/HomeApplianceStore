package ohio.rizz.homeappliancestore.repositories;

import ohio.rizz.homeappliancestore.entities.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface OrderRepository extends JpaRepository<Order, UUID> {
    List<Order> findByCustomerId(UUID customerId);
    @Query("SELECT MAX(o.orderNumber) FROM Order o WHERE o.orderNumber LIKE :prefix%")
    String findMaxOrderNumberByPrefix(@Param("prefix") String prefix);

    Optional<Order> findByOrderNumber(String orderNumber);
}