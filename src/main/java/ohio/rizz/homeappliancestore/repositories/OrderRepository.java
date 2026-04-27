package ohio.rizz.homeappliancestore.repositories;

import ohio.rizz.homeappliancestore.entities.Order;
import ohio.rizz.homeappliancestore.enums.OrderStatus;
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
    List<Order> findByStatus(OrderStatus status);

    @Query("SELECT o FROM Order o WHERE LOWER(o.orderNumber) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    List<Order> searchByOrderNumber(@Param("searchTerm") String searchTerm);

    @Query("SELECT o FROM Order o JOIN FETCH o.customer WHERE o.id = :id")
    Order findByIdWithCustomer(@Param("id") UUID id);

    @Query("SELECT MAX(o.orderNumber) FROM Order o WHERE o.orderNumber LIKE :prefix%")
    String findMaxOrderNumberByPrefix(@Param("prefix") String prefix);
}