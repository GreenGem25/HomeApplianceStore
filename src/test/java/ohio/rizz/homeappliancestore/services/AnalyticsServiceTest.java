package ohio.rizz.homeappliancestore.services;

import ohio.rizz.homeappliancestore.dto.DashboardDto;
import ohio.rizz.homeappliancestore.entities.DailyAnalytics;
import ohio.rizz.homeappliancestore.entities.Expense;
import ohio.rizz.homeappliancestore.entities.Order;
import ohio.rizz.homeappliancestore.entities.OrderItem;
import ohio.rizz.homeappliancestore.entities.Product;
import ohio.rizz.homeappliancestore.enums.AuditAction;
import ohio.rizz.homeappliancestore.enums.AuditEntityType;
import ohio.rizz.homeappliancestore.enums.OrderStatus;
import ohio.rizz.homeappliancestore.repositories.CustomerRepository;
import ohio.rizz.homeappliancestore.repositories.DailyAnalyticsRepository;
import ohio.rizz.homeappliancestore.repositories.ExpenseRepository;
import ohio.rizz.homeappliancestore.repositories.OrderRepository;
import ohio.rizz.homeappliancestore.repositories.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AnalyticsServiceTest {

    @Mock
    private OrderRepository orderRepository;
    @Mock
    private CustomerRepository customerRepository;
    @Mock
    private ProductRepository productRepository;
    @Mock
    private ExpenseRepository expenseRepository;
    @Mock
    private DailyAnalyticsRepository dailyAnalyticsRepository;
    @Mock
    private AuditService auditService;

    @InjectMocks
    private AnalyticsService analyticsService;

    private LocalDate testDate;
    private LocalDateTime dayStart;
    private LocalDateTime dayEnd;

    @BeforeEach
    void setUp() {
        testDate = LocalDate.of(2026, 3, 10);
        dayStart = testDate.atStartOfDay();
        dayEnd = testDate.plusDays(1).atStartOfDay();
    }

    // Вспомогательный метод для создания заказа с позициями
    private Order createOrderWithItems(BigDecimal unitPrice,
                                       BigDecimal unitCostPrice,
                                       int quantity,
                                       LocalDateTime orderDate) {
        Product product = new Product();
        product.setCostPrice(unitCostPrice);
        product.setPrice(unitPrice);
        product.setStockQuantity(10);
        product.setName("Test Product");

        OrderItem item = new OrderItem();
        item.setProduct(product);
        item.setOrderQuantity(quantity);
        item.setOrderPrice(unitPrice);
        item.setCostPrice(unitCostPrice);
        item.setVatRate(20);
        item.setVatAmount(
                unitPrice.multiply(BigDecimal.valueOf(20))
                        .divide(BigDecimal.valueOf(120), 2, RoundingMode.HALF_UP)
        );

        Order order = new Order();
        order.setStatus(OrderStatus.COMPLETED);
        order.setOrderDate(orderDate);
        order.addOrderItem(item);   // пересчитывает totalPrice = unitPrice * quantity

        return order;
    }

    @Test
    void updateAnalytics_ShouldSaveCorrectDailyAggregates() {
        // Given
        Order order1 = createOrderWithItems(
                new BigDecimal("20000.00"), new BigDecimal("15000.00"), 2, dayStart);
        Order order2 = createOrderWithItems(
                new BigDecimal("20000.00"), new BigDecimal("10000.00"), 1, dayStart);
        List<Order> completedOrders = List.of(order1, order2);

        when(orderRepository.findByStatusAndOrderDateBetween(OrderStatus.COMPLETED, dayStart, dayEnd))
                .thenReturn(completedOrders);
        when(customerRepository.countByCreatedAtBetween(dayStart, dayEnd)).thenReturn(5L);
        when(dailyAnalyticsRepository.findById(testDate)).thenReturn(Optional.empty());

        // When
        analyticsService.updateAnalytics(testDate);

        // Then
        ArgumentCaptor<DailyAnalytics> captor = ArgumentCaptor.forClass(DailyAnalytics.class);
        verify(dailyAnalyticsRepository).save(captor.capture());

        DailyAnalytics saved = captor.getValue();
        assertEquals(testDate, saved.getDate());
        // Выручка = 20000*2 + 20000*1 = 60000
        assertEquals(new BigDecimal("60000.00"), saved.getTotalRevenue());
        // Себестоимость = 15000*2 + 10000*1 = 40000
        assertEquals(new BigDecimal("40000.00"), saved.getTotalCost());
        assertEquals(2, saved.getOrderCount());
        assertEquals(5, saved.getNewCustomers());
    }

    @Test
    void updateAnalytics_ShouldUpdateExistingRecord() {
        // Given
        Order order = createOrderWithItems(
                new BigDecimal("10000.00"), new BigDecimal("5000.00"), 1, dayStart);
        List<Order> completedOrders = List.of(order);
        DailyAnalytics existing = new DailyAnalytics();
        existing.setDate(testDate);
        existing.setTotalRevenue(BigDecimal.ZERO);
        existing.setTotalCost(BigDecimal.ZERO);
        existing.setOrderCount(0);
        existing.setNewCustomers(0);

        when(orderRepository.findByStatusAndOrderDateBetween(OrderStatus.COMPLETED, dayStart, dayEnd))
                .thenReturn(completedOrders);
        when(customerRepository.countByCreatedAtBetween(dayStart, dayEnd)).thenReturn(3L);
        when(dailyAnalyticsRepository.findById(testDate)).thenReturn(Optional.of(existing));

        // When
        analyticsService.updateAnalytics(testDate);

        // Then
        verify(dailyAnalyticsRepository).save(existing);
        assertEquals(new BigDecimal("10000.00"), existing.getTotalRevenue());
        assertEquals(new BigDecimal("5000.00"), existing.getTotalCost());
        assertEquals(1, existing.getOrderCount());
        assertEquals(3, existing.getNewCustomers());
    }

    @Test
    void refreshAnalyticsSingleDate_ShouldUpdateAndLog() {
        // Given
        doNothing().when(auditService).log(any(), any(), any(), anyString());

        // Моки для updateAnalytics (метод вызывается внутри refreshAnalytics)
        when(orderRepository.findByStatusAndOrderDateBetween(eq(OrderStatus.COMPLETED), any(), any()))
                .thenReturn(Collections.emptyList());
        when(customerRepository.countByCreatedAtBetween(any(), any())).thenReturn(0L);
        when(dailyAnalyticsRepository.findById(testDate)).thenReturn(Optional.empty());

        // When
        analyticsService.refreshAnalytics(testDate);

        // Then
        verify(dailyAnalyticsRepository).save(any(DailyAnalytics.class));
        verify(auditService).log(eq(AuditAction.MANUAL_REFRESH), eq(AuditEntityType.ANALYTICS),
                eq(testDate.toString()), anyString());
    }

    @Test
    void refreshAnalyticsDateRange_ShouldIterateAndLog() {
        // Given
        LocalDate from = LocalDate.of(2026, 3, 8);
        LocalDate to = LocalDate.of(2026, 3, 10);
        doNothing().when(auditService).log(any(), any(), any(), anyString());

        when(orderRepository.findByStatusAndOrderDateBetween(eq(OrderStatus.COMPLETED), any(), any()))
                .thenReturn(Collections.emptyList());
        when(customerRepository.countByCreatedAtBetween(any(), any())).thenReturn(0L);
        when(dailyAnalyticsRepository.findById(any())).thenReturn(Optional.empty());

        // When
        analyticsService.refreshAnalytics(from, to);

        // Then
        // Должен сохранить 3 дня
        verify(dailyAnalyticsRepository, times(3)).save(any(DailyAnalytics.class));
        verify(auditService).log(eq(AuditAction.MANUAL_REFRESH), eq(AuditEntityType.ANALYTICS),
                anyString(), anyString());
    }

    @Test
    void getDashboard_WithTodayAnalytics_ShouldReturnPrecomputedValues() {
        // Given
        LocalDate today = LocalDate.now();
        DailyAnalytics todayDA = new DailyAnalytics();
        todayDA.setDate(today);
        todayDA.setTotalRevenue(new BigDecimal("150000.00"));
        todayDA.setTotalCost(new BigDecimal("100000.00"));
        todayDA.setOrderCount(3);
        todayDA.setNewCustomers(2);

        when(dailyAnalyticsRepository.findById(today)).thenReturn(Optional.of(todayDA));

        // Расходы за сегодня
        Expense expense1 = new Expense();
        expense1.setAmount(new BigDecimal("5000.00"));
        Expense expense2 = new Expense();
        expense2.setAmount(new BigDecimal("3000.00"));
        when(expenseRepository.findByExpenseDateBetween(today, today))
                .thenReturn(List.of(expense1, expense2));

        // Моки для склада
        Product p1 = new Product();
        p1.setPrice(new BigDecimal("1000.00"));
        p1.setStockQuantity(10);
        Product p2 = new Product();
        p2.setPrice(new BigDecimal("500.00"));
        p2.setStockQuantity(4);
        when(productRepository.findAll()).thenReturn(List.of(p1, p2));

        // Для графиков (период 30 дней) – пустой список
        LocalDate from = today.minusDays(30);
        LocalDate to = today;
        when(dailyAnalyticsRepository.findByDateBetweenOrderByDate(from, to))
                .thenReturn(Collections.emptyList());
        when(orderRepository.findByStatusAndOrderDateBetween(eq(OrderStatus.COMPLETED), any(), any()))
                .thenReturn(Collections.emptyList());
        when(orderRepository.countByStatus(OrderStatus.IN_PROGRESS)).thenReturn(4L);

        // When
        DashboardDto dashboard = analyticsService.getDashboard(from, to);

        // Then
        assertEquals(new BigDecimal("150000.00"), dashboard.getTodayRevenue());
        // Прибыль = выручка - себестоимость - расходы = 150000 - 100000 - 8000 = 42000
        assertEquals(new BigDecimal("42000.00"), dashboard.getTodayProfit());
        assertEquals(new BigDecimal("50000.00"), dashboard.getAverageCheck()); // 150000/3
        assertEquals(3, dashboard.getOrdersCompletedToday());
        assertEquals(4, dashboard.getOrdersInProgress());
        assertEquals(14, dashboard.getProductsInStock());
        assertEquals(new BigDecimal("12000.00"), dashboard.getStockValue()); // 10*1000 + 4*500 = 10000+2000
    }

    @Test
    void getDashboard_WithoutTodayAnalytics_ShouldCalculateFromOrders() {
        // Given
        LocalDate today = LocalDate.now();
        LocalDateTime todayStart = today.atStartOfDay();
        LocalDateTime todayEnd = today.plusDays(1).atStartOfDay();

        when(dailyAnalyticsRepository.findById(today)).thenReturn(Optional.empty());

        // Заказ с сегодняшней датой
        Order order = createOrderWithItems(
                new BigDecimal("30000.00"), new BigDecimal("20000.00"), 1, todayStart);
        List<Order> todayOrders = List.of(order);

        // Графики за период (задаем временные рамки)
        LocalDate from = today.minusDays(30);
        LocalDate to = today;
        LocalDateTime periodStart = from.atStartOfDay();
        LocalDateTime periodEnd = to.plusDays(1).atStartOfDay();

        // Настраиваем моки для orderRepository раздельно, чтобы они не перезаписывали друг друга
        // 1. Вызов для сегодняшней статистики
        when(orderRepository.findByStatusAndOrderDateBetween(OrderStatus.COMPLETED, todayStart, todayEnd))
                .thenReturn(todayOrders);

        // 2. Вызов для аналитики по категориям и топ-товарам за период
        when(orderRepository.findByStatusAndOrderDateBetween(OrderStatus.COMPLETED, periodStart, periodEnd))
                .thenReturn(Collections.emptyList());

        // Расходы отсутствуют
        when(expenseRepository.findByExpenseDateBetween(today, today))
                .thenReturn(Collections.emptyList());

        // Склад пуст
        when(productRepository.findAll()).thenReturn(Collections.emptyList());

        // Мокаем получение данных для графика выручки по дням
        when(dailyAnalyticsRepository.findByDateBetweenOrderByDate(from, to))
                .thenReturn(Collections.emptyList());

        // Мокаем количество заказов в процессе
        when(orderRepository.countByStatus(OrderStatus.IN_PROGRESS)).thenReturn(0L);

        // When
        DashboardDto dashboard = analyticsService.getDashboard(from, to);

        // Then
        assertEquals(new BigDecimal("30000.00"), dashboard.getTodayRevenue());
        assertEquals(new BigDecimal("10000.00"), dashboard.getTodayProfit());
        assertEquals(new BigDecimal("30000.00"), dashboard.getAverageCheck());
        assertEquals(1, dashboard.getOrdersCompletedToday());
    }
}