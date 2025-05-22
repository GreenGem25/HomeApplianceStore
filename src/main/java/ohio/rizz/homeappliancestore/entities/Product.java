package ohio.rizz.homeappliancestore.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "products")
@Getter
@Setter
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_id")
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;
    @Column(name = "description")
    private String description;
    @Column(name = "price", nullable = false)
    private BigDecimal price;
    @Column(name = "stock_quantity", nullable = false)
    private int stockQuantity;
    @Column(name = "manufacturer")
    private String manufacturer;
    @Column(name = "warranty_period")
    private int warrantyPeriod;
    @Column(name = "image_path")
    private String imagePath;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @ManyToOne
    @OnDelete(action = OnDeleteAction.SET_NULL)
    @JoinColumn(name = "category_id", nullable = true)
    private Category category;

    @ManyToOne
    @OnDelete(action = OnDeleteAction.SET_NULL)
    @JoinColumn(name = "store_id", nullable = true)
    private Store store;

    @ManyToOne
    @OnDelete(action = OnDeleteAction.SET_NULL)
    @JoinColumn(name = "supplier_id", nullable = true)
    private Supplier supplier;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL)
    private List<OrderItem> orderItems;

}
