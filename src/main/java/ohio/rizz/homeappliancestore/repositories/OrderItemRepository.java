package ohio.rizz.homeappliancestore.repositories;

import ohio.rizz.homeappliancestore.entities.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface OrderItemRepository extends JpaRepository<OrderItem, UUID> {

    // Все позиции конкретного заказа
    List<OrderItem> findByOrder_Id(UUID orderId);

    // Все позиции по конкретному товару
    List<OrderItem> findByProduct_Id(UUID productId);

}