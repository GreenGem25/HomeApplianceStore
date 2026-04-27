package ohio.rizz.homeappliancestore.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "analytics_daily")
@Getter
@Setter
public class DailyAnalytics {
    @Id
    @Column(name = "date", columnDefinition = "DATE")
    private LocalDate date;

    @Column(name = "total_revenue")
    private BigDecimal totalRevenue;

    @Column(name = "total_cost")
    private BigDecimal totalCost;

    @Column(name = "order_count")
    private int orderCount;

    @Column(name = "new_customers")
    private int newCustomers;
}