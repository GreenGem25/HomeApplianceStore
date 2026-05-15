package ohio.rizz.homeappliancestore.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import ohio.rizz.homeappliancestore.enums.SupplyStatus;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "supplies")
@Getter
@Setter
public class Supply {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "supply_id", columnDefinition = "UUID")
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "supplier_id", nullable = false)
    private Supplier supplier;

    @Column(name = "supply_number", unique = true, nullable = false, length = 50)
    private String supplyNumber;

    @Column(name = "supply_date", nullable = false)
    private LocalDateTime supplyDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private SupplyStatus status = SupplyStatus.PENDING;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    @Column(name = "logistic_cost", precision = 10, scale = 2)
    private BigDecimal logisticCost = BigDecimal.ZERO;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "supply", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SupplyItem> supplyItems = new ArrayList<>();

    @PrePersist
    public void generateSupplyNumber() {
        if (supplyNumber == null) {
            // Формат: SUP-ГГГГММДД-XXXXX
            String date = LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd"));
            String randomPart = String.format("%05d", (int)(Math.random() * 100000));
            this.supplyNumber = "SUP-" + date + "-" + randomPart;
        }
    }

    public void addSupplyItem(SupplyItem item) {
        supplyItems.add(item);
        item.setSupply(this);
    }

    public void removeSupplyItem(SupplyItem item) {
        supplyItems.remove(item);
        item.setSupply(null);
    }

    public int getTotalItems() {
        return supplyItems.size();
    }

    public int getTotalQuantity() {
        return supplyItems.stream()
                .mapToInt(SupplyItem::getQuantity)
                .sum();
    }

    public double getTotalValue() {
        return supplyItems.stream()
                .mapToDouble(item -> item.getQuantity() * item.getPricePerUnit().doubleValue())
                .sum();
    }
}