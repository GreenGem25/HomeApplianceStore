package ohio.rizz.homeappliancestore.services;

import ohio.rizz.homeappliancestore.dto.CreateOrderDto;
import ohio.rizz.homeappliancestore.entities.*;
import ohio.rizz.homeappliancestore.exceptions.CustomerNotFoundException;
import ohio.rizz.homeappliancestore.exceptions.OrderNotFoundException;
import ohio.rizz.homeappliancestore.exceptions.OutOfStockException;
import ohio.rizz.homeappliancestore.exceptions.ProductNotFoundException;
import ohio.rizz.homeappliancestore.repositories.OrderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
public class OrderService {
    private final OrderRepository orderRepository;
    private final CustomerService customerService;
    private final ProductService productService;

    public OrderService(OrderRepository orderRepository,
                        CustomerService customerService,
                        ProductService productService) {
        this.orderRepository = orderRepository;
        this.customerService = customerService;
        this.productService = productService;
    }

    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    public List<Order> getOrdersByCustomer(Long customerId) {
        return orderRepository.findByCustomerId(customerId);
    }

    public Order getOrderById(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new OrderNotFoundException("Заказ не найден"));
    }

    public Long getOrderCustomerId(Long orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException("Заказ не найден")).getCustomer().getId();
    }

    @Transactional
    public Order createOrder(CreateOrderDto orderDto) {
        // Получаем клиента
        Customer customer = customerService.getCustomerById(orderDto.getCustomerId())
                .orElseThrow(() -> new CustomerNotFoundException("Клиент не найден"));

        // Создаем новый заказ
        Order order = new Order();
        order.setCustomer(customer);
        order.setShippingAddress(orderDto.getShippingAddress());
        order.setStatus(Order.OrderStatus.IN_PROGRESS);

        // Добавляем товары в заказ
        for (var itemRequest : orderDto.getItems()) {
            addOrderItem(order, itemRequest.getProductId(), itemRequest.getQuantity());
        }

        // Сохраняем заказ
        return orderRepository.save(order);
    }

    @Transactional
    public Order updateOrder(Long orderId, CreateOrderDto orderDto) {
        // Получаем существующий заказ
        Order order = getOrderById(orderId);

        // Проверяем, что заказ можно редактировать
        if (order.getStatus() != Order.OrderStatus.IN_PROGRESS) {
            throw new IllegalStateException("Невозможно редактировать завершенный заказ");
        }

        // Обновляем клиента, если изменился
        if (!order.getCustomer().getId().equals(orderDto.getCustomerId())) {
            Customer customer = customerService.getCustomerById(orderDto.getCustomerId())
                    .orElseThrow(() -> new CustomerNotFoundException("Клиент не найден"));
            order.setCustomer(customer);
        }

        // Обновляем адрес доставки
        order.setShippingAddress(orderDto.getShippingAddress());

        // Удаляем все текущие товары из заказа (возвращаем на склад)
        order.getOrderItems().forEach(item -> {
            Product product = item.getProduct();
            product.increaseStock(item.getOrderQuantity());
            productService.save(product);
        });
        order.getOrderItems().clear();

        // Добавляем новые товары в заказ
        for (var itemRequest : orderDto.getItems()) {
            addOrderItem(order, itemRequest.getProductId(), itemRequest.getQuantity());
        }

        // Пересчитываем общую стоимость
        order.recalculateTotalPrice();

        return orderRepository.save(order);
    }

    @Transactional
    public Order completeOrder(Long orderId) {
        Order order = getOrderById(orderId);

        // Проверяем, что заказ еще не завершен
        if (order.getStatus() == Order.OrderStatus.COMPLETED) {
            throw new IllegalStateException("Заказ уже завершен");
        }

        // Меняем статус заказа
        order.setStatus(Order.OrderStatus.COMPLETED);

        // Обновляем информацию о клиенте (добавляем сумму заказа к потраченным средствам)
        Customer customer = order.getCustomer();
        if (customer.getMoneySpent() == null) {
            customer.setMoneySpent(0.0);
        }
        customer.setMoneySpent(customer.getMoneySpent() + order.calculateFinalPrice().doubleValue());
        customerService.save(customer);

        return orderRepository.save(order);
    }

    @Transactional
    public void deleteOrder(Long orderId) {
        Order order = getOrderById(orderId);

        // Если заказ в процессе сборки - возвращаем товары на склад
        if (order.getStatus() == Order.OrderStatus.IN_PROGRESS) {
            for (OrderItem item : order.getOrderItems()) {
                Product product = item.getProduct();
                product.increaseStock(item.getOrderQuantity());
                productService.save(product);
            }
        }

        orderRepository.delete(order);
    }

    private void addOrderItem(Order order, Long productId, int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Количество товара должно быть положительным");
        }

        Product product = productService.getProductById(productId)
                .orElseThrow(() -> new ProductNotFoundException("Товар не найден"));

        // Проверяем наличие товара на складе
        if (product.getStockQuantity() < quantity) {
            throw new OutOfStockException("Недостаточно товара на складе: " + product.getName());
        }

        // Создаем позицию заказа
        OrderItem orderItem = new OrderItem();
        orderItem.setProduct(product);
        orderItem.setOrderQuantity(quantity);
        orderItem.setOrderPrice(product.getPrice());

        // Уменьшаем количество товара на складе
        product.decreaseStock(quantity);
        productService.save(product);

        // Добавляем позицию в заказ
        order.addOrderItem(orderItem);
    }
}