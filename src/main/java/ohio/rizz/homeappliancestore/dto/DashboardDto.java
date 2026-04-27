package ohio.rizz.homeappliancestore.dto;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Builder
public class DashboardDto {
    BigDecimal todayRevenue;
    BigDecimal todayProfit;
    BigDecimal averageCheck;
    int ordersInProgress;
    int ordersCompletedToday;
    int productsInStock;
    BigDecimal stockValue;

    List<ChartDataPoint> revenueByDay;      // для графика
    List<ChartDataPoint> revenueByCategory; // для круговой диаграммы
    List<TopProductDto> topProducts;
}