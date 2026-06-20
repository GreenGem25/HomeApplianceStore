package ohio.rizz.homeappliancestore.services;

import lombok.RequiredArgsConstructor;
import ohio.rizz.homeappliancestore.dto.OrderCreateDto;
import ohio.rizz.homeappliancestore.dto.OrderDto;
import ohio.rizz.homeappliancestore.dto.OrderItemCreateDto;
import ohio.rizz.homeappliancestore.entities.*;
import ohio.rizz.homeappliancestore.enums.AuditAction;
import ohio.rizz.homeappliancestore.enums.AuditEntityType;
import ohio.rizz.homeappliancestore.enums.OrderStatus;
import ohio.rizz.homeappliancestore.exceptions.*;
import ohio.rizz.homeappliancestore.mappers.OrderMapper;
import ohio.rizz.homeappliancestore.repositories.OrderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;
    private final CustomerService customerService;
    private final ProductService productService;
    private final OrderMapper orderMapper;
    private final AuditService auditService;

    public List<OrderDto> getAllOrders() {
        return orderMapper.toDto(orderRepository.findAll());
    }

    public List<OrderDto> getOrdersByCustomer(UUID customerId) {
        return orderMapper.toDto(orderRepository.findByCustomerId(customerId));
    }

    public OrderDto getOrderById(UUID id) {
        Order order = getOrderEntityOrThrow(id);
        return orderMapper.toDto(order);
    }

    public Order getOrderEntityOrThrow(UUID id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new OrderNotFoundException("Заказ не найден"));
    }

    public Optional<Order> findOrderById(UUID id) {
        return orderRepository.findById(id);
    }

    @Transactional
    public OrderDto createOrder(OrderCreateDto orderDto) {
        // Получаем клиента
        Customer customer = customerService.getCustomerEntityOrThrow(orderDto.getCustomerId());

        // Создаем новый заказ
        Order order = orderMapper.toEntity(orderDto);
        order.setCustomer(customer);
        order.setStatus(OrderStatus.IN_PROGRESS);
        order.setOrderDate(LocalDateTime.now());

        // Добавляем товары в заказ
        for (OrderItemCreateDto itemDto : orderDto.getItems()) {
            addOrderItem(order, itemDto.getProductId(), itemDto.getQuantity());
        }

        // Сохраняем заказ
        Order savedOrder = orderRepository.save(order);

        auditService.log(AuditAction.CREATE, AuditEntityType.ORDER, order.getId().toString(),
                String.format("Created order with number '%s'", savedOrder.getOrderNumber()));

        return orderMapper.toDto(savedOrder);
    }

    @Transactional
    public OrderDto updateOrder(UUID orderId, OrderCreateDto orderDto) {
        // Получаем существующий заказ
        Order order = getOrderEntityOrThrow(orderId);

        // Проверяем, что заказ можно редактировать
        if (order.getStatus() != OrderStatus.IN_PROGRESS) {
            throw new IllegalStateException("Невозможно редактировать завершенный заказ");
        }

        // Обновляем клиента, если изменился
        if (!order.getCustomer().getId().equals(orderDto.getCustomerId())) {
            Customer customer = customerService.getCustomerEntityOrThrow(orderDto.getCustomerId());
            order.setCustomer(customer);
        }

        // Обновляем адрес доставки
        order.setShippingAddress(orderDto.getShippingAddress());

        // Возвращаем товары на склад
        returnItemsToStock(order);

        // Очищаем текущие товары
        order.getOrderItems().clear();

        // Добавляем новые товары
        for (OrderItemCreateDto itemDto : orderDto.getItems()) {
            addOrderItem(order, itemDto.getProductId(), itemDto.getQuantity());
        }

        // Пересчитываем общую стоимость
        order.recalculateTotalPrice();

        Order updatedOrder = orderRepository.save(order);

        auditService.log(AuditAction.UPDATE, AuditEntityType.ORDER, updatedOrder.getId().toString(),
                String.format("Updated order with number '%s'", updatedOrder.getOrderNumber()));

        return orderMapper.toDto(updatedOrder);
    }

    @Transactional
    public OrderDto completeOrder(UUID orderId) {
        Order order = getOrderEntityOrThrow(orderId);

        if (order.getStatus() == OrderStatus.COMPLETED) {
            throw new IllegalStateException("Заказ уже завершен");
        }

        // Меняем статус заказа
        order.setStatus(OrderStatus.COMPLETED);
        order.setTotalPrice(order.calculateFinalPrice());

        // Обновляем информацию о клиенте
        Customer customer = order.getCustomer();
        if (customer.getMoneySpent() == null) {
            customer.setMoneySpent(BigDecimal.ZERO);
        }
        customer.setMoneySpent(customer.getMoneySpent().add(order.calculateFinalPrice()));
        customer.setDiscount(customer.calculateDiscount());
        customerService.save(customer);

        Order completedOrder = orderRepository.save(order);

        auditService.log(AuditAction.COMPLETE, AuditEntityType.ORDER, completedOrder.getId().toString(),
                String.format("Order with number '%s' marked as completed", completedOrder.getOrderNumber()));

        return orderMapper.toDto(completedOrder);
    }

    @Transactional
    public void deleteOrder(UUID orderId) {
        Order order = getOrderEntityOrThrow(orderId);

        // Если заказ в процессе сборки - возвращаем товары на склад
        if (order.getStatus() == OrderStatus.IN_PROGRESS) {
            returnItemsToStock(order);
        }

        String orderNumber = order.getOrderNumber();

        orderRepository.delete(order);

        auditService.log(AuditAction.DELETE, AuditEntityType.ORDER, orderId.toString(),
                String.format("Order with number '%s' was deleted", orderNumber));
    }

    public List<OrderDto> searchOrders(String searchTerm) {
        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            return getAllOrders();
        }
        return orderMapper.toDto(orderRepository.searchByOrderNumber(searchTerm.trim()));
    }

    public List<OrderDto> getOrdersByStatus(OrderStatus status) {
        if (status == null) {
            return getAllOrders();
        }
        return orderMapper.toDto(orderRepository.findByStatus(status));
    }

    public List<OrderDto> getFilteredOrders(String searchTerm, OrderStatus status, UUID customerId) {
        // Начинаем со всех заказов
        List<Order> orders;

        if (customerId != null) {
            orders = orderRepository.findByCustomerId(customerId);
        } else {
            orders = orderRepository.findAll();
        }

        // Применяем фильтры в памяти (для простоты, но для больших данных лучше делать в БД)
        return orders.stream()
                .filter(order -> searchTerm == null || searchTerm.trim().isEmpty() ||
                        order.getOrderNumber().toLowerCase().contains(searchTerm.toLowerCase().trim()))
                .filter(order -> status == null || order.getStatus() == status)
                .map(orderMapper::toDto)
                .toList();
    }

    private void addOrderItem(Order order, UUID productId, int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Количество товара должно быть положительным");
        }

        Product product = productService.getProductEntityOrThrow(productId);

        if (product.getStockQuantity() < quantity) {
            throw new OutOfStockException("Недостаточно товара на складе: " + product.getName());
        }

        OrderItem orderItem = new OrderItem();
        orderItem.setProduct(product);
        orderItem.setOrderQuantity(quantity);
        orderItem.setOrderPrice(product.getPrice());
        orderItem.setCostPrice(product.getCostPrice());

        // НДС
        Integer vatRate = product.getVatRate();
        if (vatRate != null && vatRate > 0) {
            orderItem.setVatRate(vatRate);
            // НДС = цена * ставка / (100 + ставка) — если цена включает НДС (обычно да)
            BigDecimal vatAmount = orderItem.getOrderPrice()
                    .multiply(BigDecimal.valueOf(vatRate))
                    .divide(BigDecimal.valueOf(100 + vatRate), 2, RoundingMode.HALF_UP);
            orderItem.setVatAmount(vatAmount);
        } else {
            orderItem.setVatRate(null);
            orderItem.setVatAmount(BigDecimal.ZERO);
        }

        product.decreaseStock(quantity);
        productService.save(product);

        order.addOrderItem(orderItem);
    }

    private void returnItemsToStock(Order order) {
        order.getOrderItems().forEach(item -> {
            Product product = item.getProduct();
            if (product != null) {
                product.increaseStock(item.getOrderQuantity());
                productService.save(product);
            }
        });
    }
}