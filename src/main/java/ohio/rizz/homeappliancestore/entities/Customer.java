package ohio.rizz.homeappliancestore.entities;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import ohio.rizz.homeappliancestore.exceptions.CustomerNotFoundException;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "customers")
@Getter
@Setter
public class Customer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "customer_id")
    private Long id;

    @Column(name = "first_name", nullable = false)
    private String firstName;
    @Column(name = "last_name", nullable = false)
    private String lastName;
    @Column(name = "email", nullable = false, unique = true)
    private String email;
    @Column(name = "phone")
    private String phone;
    @Column(name = "address")
    private String address;
    @Column(name = "image_path")
    private String imagePath;

    @Column(name = "discount")
    private Integer discount;

    @Column(name = "money_spent")
    private Double moneySpent;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Order> orders = new ArrayList<>();

    public int calculateDiscount() {

        // Максимальная скидка 80%
        if (discount >= 80) {
            return 80;
        }

        // Нужно потратить для получения +5% скидки:
        // 5000 10000 20000 40000 80000
        // за один заказ нельзя получить больше 5% скидки.
        if (moneySpent >= 5000 * Math.pow(2, (orders.size() - 1))) {
            return discount + 5;
        }

        return discount;
    }

}
