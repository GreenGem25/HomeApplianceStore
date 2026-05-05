package ohio.rizz.homeappliancestore.services;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import ohio.rizz.homeappliancestore.dto.ChartDataPoint;
import ohio.rizz.homeappliancestore.dto.DashboardDto;
import ohio.rizz.homeappliancestore.dto.TopProductDto;
import ohio.rizz.homeappliancestore.entities.DailyAnalytics;
import ohio.rizz.homeappliancestore.entities.Expense;
import ohio.rizz.homeappliancestore.entities.Order;
import ohio.rizz.homeappliancestore.entities.Product;
import ohio.rizz.homeappliancestore.enums.AuditAction;
import ohio.rizz.homeappliancestore.enums.AuditEntityType;
import ohio.rizz.homeappliancestore.enums.OrderStatus;
import ohio.rizz.homeappliancestore.repositories.*;
import org.springframework.cglib.core.Local;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AnalyticsService {

    private final OrderRepository orderRepository;
    private final CustomerRepository customerRepository;
    private final ProductRepository productRepository;
    private final ExpenseRepository expenseRepository;
    private final DailyAnalyticsRepository dailyAnalyticsRepository;
    private final AuditService auditService;

    @Scheduled(cron = "0 0 1 * * ?")
    @Transactional
    public void updateDailyAnalytics() {
        LocalDate date = LocalDate.now().minusDays(1);
        updateAnalytics(date);
    }

    @Transactional
    public void updateAnalytics(LocalDate date)
    {
        LocalDateTime start = date.atStartOfDay();
        LocalDateTime end = date.plusDays(1).atStartOfDay();

        List<Order> completedOrders = orderRepository
                .findByStatusAndOrderDateBetween(OrderStatus.COMPLETED, start, end);

        BigDecimal revenue = completedOrders.stream()
                .map(Order::getTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal cost = completedOrders.stream()
                .flatMap(o -> o.getOrderItems().stream())
                .map(oi -> oi.getCostPrice()
                        .multiply(BigDecimal.valueOf(oi.getOrderQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        int orderCount = completedOrders.size();
        int newCustomers = (int) customerRepository.countByCreatedAtBetween(start, end);

        DailyAnalytics da = dailyAnalyticsRepository.findById(date)
                .orElse(new DailyAnalytics());
        da.setDate(date);
        da.setTotalRevenue(revenue);
        da.setTotalCost(cost);
        da.setOrderCount(orderCount);
        da.setNewCustomers(newCustomers);
        dailyAnalyticsRepository.save(da);
    }

    @Transactional
    public void refreshAnalytics(LocalDate from, LocalDate to) {
        auditService.log(AuditAction.MANUAL_REFRESH, AuditEntityType.ANALYTICS, from.toString(),
                String.format("Initiated manual refresh from %s to %s", from, to));
        LocalDate date = from;
        while (!date.isAfter(to)) {
            updateAnalytics(date);
            date = date.plusDays(1);
        }
    }

    @Transactional
    public void refreshAnalytics(LocalDate date) {
        auditService.log(AuditAction.MANUAL_REFRESH, AuditEntityType.ANALYTICS, date.toString(),
                String.format("Initiated manual refresh at %s", date));
        updateAnalytics(date);
    }

    @Transactional(readOnly = true)
    public DashboardDto getDashboard(LocalDate from, LocalDate to) {
        LocalDate today = LocalDate.now();
        LocalDateTime dayStart = today.atStartOfDay();
        LocalDateTime dayEnd = today.plusDays(1).atStartOfDay();

        DailyAnalytics todayDA = dailyAnalyticsRepository.findById(today).orElse(null);
        BigDecimal todayRevenue;
        BigDecimal todayCost;
        int completedToday;

        if (todayDA != null) {
            todayRevenue = todayDA.getTotalRevenue();
            todayCost = todayDA.getTotalCost();
            completedToday = todayDA.getOrderCount();
        } else {
            List<Order> todayOrders = orderRepository
                    .findByStatusAndOrderDateBetween(OrderStatus.COMPLETED, dayStart, dayEnd);
            todayRevenue = todayOrders.stream()
                    .map(Order::getTotalPrice)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            todayCost = todayOrders.stream()
                    .flatMap(o -> o.getOrderItems().stream())
                    .map(oi -> oi.getProduct().getCostPrice()
                            .multiply(BigDecimal.valueOf(oi.getOrderQuantity())))
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            completedToday = todayOrders.size();
        }

        List<Expense> todayExpenses = expenseRepository.findByExpenseDateBetween(today, today);
        BigDecimal expensesToday = todayExpenses.stream()
                .map(Expense::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal todayProfit = todayRevenue.subtract(todayCost).subtract(expensesToday);
        BigDecimal avgCheck = completedToday > 0
                ? todayRevenue.divide(BigDecimal.valueOf(completedToday), 2, RoundingMode.HALF_UP)
                : BigDecimal.ZERO;

        long inProgress = orderRepository.countByStatus(OrderStatus.IN_PROGRESS);

        // Склад
        List<Product> allProducts = productRepository.findAll();
        int totalStockQty = allProducts.stream().mapToInt(Product::getStockQuantity).sum();
        BigDecimal stockValue = allProducts.stream()
                .map(p -> p.getPrice().multiply(BigDecimal.valueOf(p.getStockQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // --- График выручки по дням (из предрассчитанной таблицы) ---
        List<DailyAnalytics> dailyData = dailyAnalyticsRepository
                .findByDateBetweenOrderByDate(from, to);
        List<ChartDataPoint> revenueByDay = dailyData.stream()
                .map(d -> ChartDataPoint.builder()
                        .label(d.getDate().toString())
                        .value(d.getTotalRevenue())
                        .build())
                .collect(Collectors.toList());

        // --- Выручка по категориям (считаем на лету за весь период) ---
        LocalDateTime periodStart = from.atStartOfDay();
        LocalDateTime periodEnd = to.plusDays(1).atStartOfDay();
        List<Order> periodOrders = orderRepository
                .findByStatusAndOrderDateBetween(OrderStatus.COMPLETED, periodStart, periodEnd);

        Map<String, BigDecimal> categoryRevenue = periodOrders.stream()
                .flatMap(o -> o.getOrderItems().stream())
                .collect(Collectors.groupingBy(
                        oi -> oi.getProduct().getCategory() != null
                                ? oi.getProduct().getCategory().getName()
                                : "Без категории",
                        Collectors.reducing(BigDecimal.ZERO,
                                oi -> oi.getOrderPrice().multiply(BigDecimal.valueOf(oi.getOrderQuantity())),
                                BigDecimal::add)
                ));
        List<ChartDataPoint> revenueByCategory = categoryRevenue.entrySet().stream()
                .map(e -> ChartDataPoint.builder().label(e.getKey()).value(e.getValue()).build())
                .collect(Collectors.toList());

        // --- Топ-5 товаров по количеству продаж за период ---
        Map<String, TopProductTemp> productStats = periodOrders.stream()
                .flatMap(o -> o.getOrderItems().stream())
                .collect(Collectors.groupingBy(
                        oi -> oi.getProduct().getName(),
                        Collectors.reducing(
                                new TopProductTemp(BigDecimal.ZERO, 0),
                                oi -> new TopProductTemp(
                                        oi.getOrderPrice().multiply(BigDecimal.valueOf(oi.getOrderQuantity())),
                                        oi.getOrderQuantity()
                                ),
                                (t1, t2) -> new TopProductTemp(
                                        t1.revenue.add(t2.revenue),
                                        t1.quantity + t2.quantity
                                )
                        )
                ));

        List<TopProductDto> topProducts = productStats.entrySet().stream()
                .map(e -> TopProductDto.builder()
                        .productName(e.getKey())
                        .totalQuantitySold(e.getValue().quantity)
                        .totalRevenue(e.getValue().revenue)
                        .build())
                .sorted(Comparator.comparingInt(TopProductDto::getTotalQuantitySold).reversed())
                .limit(5)
                .collect(Collectors.toList());

        // Собираем итоговый DTO
        return DashboardDto.builder()
                .todayRevenue(todayRevenue)
                .todayProfit(todayProfit)
                .averageCheck(avgCheck)
                .ordersInProgress((int) inProgress)
                .ordersCompletedToday(completedToday)
                .productsInStock(totalStockQty)
                .stockValue(stockValue)
                .revenueByDay(revenueByDay)
                .revenueByCategory(revenueByCategory)
                .topProducts(topProducts)
                .build();
    }

    // Вспомогательный внутренний класс для агрегации
    @AllArgsConstructor
    private static class TopProductTemp {
        BigDecimal revenue;
        int quantity;
    }
}