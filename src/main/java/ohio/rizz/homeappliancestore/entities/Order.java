package ohio.rizz.homeappliancestore.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders")
@Getter
@Setter
public class Order {

    public enum OrderStatus {
        IN_PROGRESS("В сборке"),
        COMPLETED("Выполнен");

        private final String displayName;

        OrderStatus(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_id")
    private Long id;

    @Column(name = "total_price", nullable = false)
    private BigDecimal totalPrice;

    @Enumerated(EnumType.STRING)
    @Column(name = "status") // add default value
    private OrderStatus status = OrderStatus.IN_PROGRESS;

    @Column(name = "shipping_address")
    private String shippingAddress;

    @CreationTimestamp
    @Column(name = "order_date", nullable = false, updatable = false)
    private LocalDateTime orderDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> orderItems = new ArrayList<>();

    public BigDecimal calculateFinalPrice() {
        BigDecimal discount = customer.getDiscount() != null ?
                BigDecimal.valueOf(customer.getDiscount()).divide(BigDecimal.valueOf(100)) :
                BigDecimal.ZERO;

        return totalPrice.multiply(BigDecimal.ONE.subtract(discount));
    }

    public void addOrderItem(OrderItem item) {
        orderItems.add(item);
        item.setOrder(this);
        recalculateTotalPrice();
    }

    public void removeOrderItem(OrderItem item) {
        orderItems.remove(item);
        item.setOrder(null);
        recalculateTotalPrice();
    }

    public void recalculateTotalPrice() {
        this.totalPrice = orderItems.stream()
                .map(item -> item.getOrderPrice().multiply(BigDecimal.valueOf(item.getOrderQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

}
