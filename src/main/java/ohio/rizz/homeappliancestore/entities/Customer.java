package ohio.rizz.homeappliancestore.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "customers")
@Getter
@Setter
public class Customer {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "customer_id", columnDefinition = "UUID")
    private UUID id;

    @Column(name = "first_name", nullable = false)
    private String firstName = "No first name";

    @Column(name = "last_name", nullable = false)
    private String lastName = "No last name";

    @Column(name = "email", nullable = false, unique = true)
    private String email = "No email";

    @Column(name = "phone")
    private String phone = "No phone";

    @Column(name = "address")
    private String address = "No address";

    @Column(name = "image_path")
    private String imagePath;

    @Column(name = "discount")
    private Integer discount = 0;

    @Column(name = "money_spent")
    private BigDecimal moneySpent = BigDecimal.ZERO;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Order> orders = new ArrayList<>();

    public int calculateDiscount() {
        // Максимальная скидка 50%
        if (discount >= 50) {
            return 50;
        }

        // Нужно потратить для получения +5% скидки
        if (moneySpent.compareTo(BigDecimal.valueOf(5000 * Math.pow(2, (orders.size() - 1)))) >= 0) {
            return discount + 5;
        }

        return discount;
    }
}