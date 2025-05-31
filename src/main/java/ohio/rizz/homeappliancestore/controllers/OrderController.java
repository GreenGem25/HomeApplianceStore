package ohio.rizz.homeappliancestore.controllers;

import ohio.rizz.homeappliancestore.dto.*;
import ohio.rizz.homeappliancestore.entities.*;
import ohio.rizz.homeappliancestore.exceptions.CustomerNotFoundException;
import ohio.rizz.homeappliancestore.services.CustomerService;
import ohio.rizz.homeappliancestore.services.OrderService;
import ohio.rizz.homeappliancestore.services.ProductService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/orders")
public class OrderController {
    private final OrderService orderService;
    private final CustomerService customerService;
    private final ProductService productService;

    public OrderController(OrderService orderService, CustomerService customerService, ProductService productService) {
        this.orderService = orderService;
        this.customerService = customerService;
        this.productService = productService;
    }

    @GetMapping
    public String listOrders(
            @RequestParam(required = false) Long customerId,
            Model model) {
        List<Order> orders;

        if (customerId != null) {
            System.out.println("Showing orders for customer " + customerId);
            orders = orderService.getOrdersByCustomer(customerId);
            Customer customer = customerService.getCustomerById(customerId)
                    .orElseThrow(() -> new CustomerNotFoundException("Клиент не найден"));
            model.addAttribute("customer", customer);
        } else {
            System.out.println("Showing all orders");
            orders = orderService.getAllOrders();
        }

        model.addAttribute("orders", orders);
        return "orders";
    }

    @GetMapping("/add")
    public String showAddOrderForm(Model model) {
        model.addAttribute("order", new CreateOrderDto());
        model.addAttribute("customers", customerService.getAllCustomers());
        model.addAttribute("products", productService.getAllAvailableProducts());
        return "add-order";
    }

    @PostMapping("/add")
    public String addOrder(
            @ModelAttribute CreateOrderDto orderDto,
            RedirectAttributes redirectAttributes) {
        try {
            Order order = orderService.createOrder(orderDto);
            redirectAttributes.addFlashAttribute("successMessage", "Заказ успешно создан");
            return "redirect:/orders/" + order.getId();
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Ошибка при создании заказа: " + e.getMessage());
            return "redirect:/orders/add";
        }
    }

    @GetMapping("/{id}")
    public String getOrderDetails(@PathVariable Long id, Model model) {
        Order order = orderService.getOrderById(id);

        OrderDto orderDto = convertToDto(order);
        model.addAttribute("order", orderDto);
        model.addAttribute("canEdit", order.getStatus() == Order.OrderStatus.IN_PROGRESS);
        return "order-details";
    }

    @GetMapping("/{id}/edit")
    public String showEditOrderForm(@PathVariable Long id, Model model) {
        Order order = orderService.getOrderById(id);

        if (order.getStatus() != Order.OrderStatus.IN_PROGRESS) {
            throw new IllegalStateException("Невозможно редактировать завершенный заказ");
        }

        // Преобразуем заказ в DTO для формы редактирования
        OrderDto orderDto = convertToDto(order);

        model.addAttribute("order", orderDto);
        model.addAttribute("products", productService.getAllAvailableProducts());
        return "edit-order";
    }

    @PutMapping("/{id}")
    public String updateOrder(
            @PathVariable Long id,
            @ModelAttribute CreateOrderDto orderDto,
            RedirectAttributes redirectAttributes) {
        try {
            Order order = orderService.updateOrder(id, orderDto);
            redirectAttributes.addFlashAttribute("successMessage", "Заказ успешно обновлен");
            return "redirect:/orders/" + order.getId();
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Ошибка при обновлении заказа: " + e.getMessage());
            return "redirect:/orders/" + id + "/edit";
        }
    }

    @PostMapping("/{id}/complete")
    public String completeOrder(
            @PathVariable Long id,
            RedirectAttributes redirectAttributes) {
        try {
            Order order = orderService.completeOrder(id);
            redirectAttributes.addFlashAttribute("successMessage", "Заказ успешно завершен");
            return "redirect:/orders/" + order.getId();
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Ошибка при завершении заказа: " + e.getMessage());
            return "redirect:/orders/" + id;
        }
    }

    @DeleteMapping("/{id}")
    public String deleteOrder(
            @PathVariable Long id,
            RedirectAttributes redirectAttributes) {
        try {
            orderService.deleteOrder(id);
            redirectAttributes.addFlashAttribute("successMessage", "Заказ успешно удалён");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Ошибка при удалении заказа: " + e.getMessage());
        }
        return "redirect:/orders";
    }

    private OrderDto convertToDto(Order order) {
        OrderDto dto = new OrderDto();
        dto.setId(order.getId());
        dto.setTotalPrice(order.getTotalPrice());
        dto.setStatus(order.getStatus());
        dto.setShippingAddress(order.getShippingAddress());
        dto.setOrderDate(order.getOrderDate());
        dto.setCustomerId(order.getCustomer().getId());
        dto.setCustomerName(order.getCustomer().getFirstName() + " " + order.getCustomer().getLastName());
        dto.setCustomerDiscount(order.getCustomer().getDiscount());

        dto.setOrderItems(order.getOrderItems().stream()
                .map(this::convertItemToDto)
                .collect(Collectors.toList()));

        return dto;
    }

    private OrderItemDto convertItemToDto(OrderItem item) {
        OrderItemDto dto = new OrderItemDto();
        dto.setId(item.getId());
        dto.setQuantity(item.getOrderQuantity());
        dto.setPrice(item.getOrderPrice());
        dto.setProductId(item.getProduct().getId());
        dto.setProductName(item.getProduct().getName());
        dto.setProductManufacturer(item.getProduct().getManufacturer());
        dto.setProductImagePath(item.getProduct().getImagePath());
        dto.setProductStockQuantity(item.getProduct().getStockQuantity());
        return dto;
    }
}