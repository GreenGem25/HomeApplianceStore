package ohio.rizz.homeappliancestore.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import ohio.rizz.homeappliancestore.exceptions.OutOfStockException;
import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "order_items")
@Getter
@Setter
public class OrderItem {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "order_item_id", columnDefinition = "UUID")
    private UUID id;

    @Column(name = "quantity", nullable = false)
    private int orderQuantity = 0;

    @Column(name = "price", nullable = false)
    private BigDecimal orderPrice = BigDecimal.ZERO;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = true)
    private Product product;

    @PrePersist
    @PreUpdate
    public void validateStock() {
        if (product != null && orderQuantity > product.getStockQuantity()) {
            throw new OutOfStockException("Недостаточно товара на складе");
        }
    }
}