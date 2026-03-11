package ohio.rizz.homeappliancestore.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import ohio.rizz.homeappliancestore.dto.*;
import ohio.rizz.homeappliancestore.enums.OrderStatus;
import ohio.rizz.homeappliancestore.mappers.OrderEditMapper;
import ohio.rizz.homeappliancestore.services.CustomerService;
import ohio.rizz.homeappliancestore.services.OrderService;
import ohio.rizz.homeappliancestore.services.ProductService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;
    private final CustomerService customerService;
    private final ProductService productService;
    private final OrderEditMapper orderEditMapper;

    @GetMapping
    public String listOrders(
            @RequestParam(required = false) UUID customerId,
            Model model) {

        List<OrderDto> orders;

        if (customerId != null) {
            orders = orderService.getOrdersByCustomer(customerId);
            CustomerDto customer = customerService.getCustomerById(customerId);
            model.addAttribute("customer", customer);
        } else {
            orders = orderService.getAllOrders();
        }

        model.addAttribute("orders", orders);
        return "orders";
    }

    @GetMapping("/add")
    public String showAddOrderForm(Model model) {
        model.addAttribute("order", new OrderCreateDto());
        model.addAttribute("customers", customerService.getAllCustomers());
        model.addAttribute("products", productService.getAllAvailableProducts());
        return "add-order";
    }

    @PostMapping("/add")
    public String addOrder(
            @ModelAttribute("order") OrderCreateDto orderDto,
            RedirectAttributes redirectAttributes) {
        try {
            OrderDto order = orderService.createOrder(orderDto);
            redirectAttributes.addFlashAttribute("successMessage", "Заказ успешно создан");
            return "redirect:/orders/" + order.getId();
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Ошибка при создании заказа: " + e.getMessage());
            return "redirect:/orders/add";
        }
    }

    @GetMapping("/{id}")
    public String getOrderDetails(@PathVariable UUID id, Model model) {
        OrderDto order = orderService.getOrderById(id);
        model.addAttribute("order", order);
        model.addAttribute("canEdit", order.getStatus() == OrderStatus.IN_PROGRESS);
        return "order-details";
    }

    @GetMapping("/{id}/edit")
    public String showEditOrderForm(@PathVariable UUID id, Model model) {
        OrderDto order = orderService.getOrderById(id);

        if (order.getStatus() != OrderStatus.IN_PROGRESS) {
            throw new IllegalStateException("Невозможно редактировать завершенный заказ");
        }

        OrderEditDto editDto = orderEditMapper.toEditDto(order);

        model.addAttribute("order", editDto);
        model.addAttribute("products", productService.getAllAvailableProducts());

        return "edit-order";
    }

    @PutMapping("/{id}")
    public String updateOrder(
            @PathVariable UUID id,
            @Valid @ModelAttribute("order") OrderEditDto editDto,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            return "edit-order";
        }

        try {
            OrderCreateDto createDto = orderEditMapper.toCreateDto(editDto);
            OrderDto updated = orderService.updateOrder(id, createDto);
            redirectAttributes.addFlashAttribute("successMessage", "Заказ успешно обновлен");
            return "redirect:/orders/" + updated.getId();
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/orders/" + id + "/edit";
        }
    }

    @PostMapping("/{id}/complete")
    public String completeOrder(
            @PathVariable UUID id,
            RedirectAttributes redirectAttributes) {
        try {
            OrderDto order = orderService.completeOrder(id);
            redirectAttributes.addFlashAttribute("successMessage", "Заказ успешно завершен");
            return "redirect:/orders/" + order.getId();
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Ошибка при завершении заказа: " + e.getMessage());
            return "redirect:/orders/" + id;
        }
    }

    @DeleteMapping("/{id}")
    public String deleteOrder(
            @PathVariable UUID id,
            RedirectAttributes redirectAttributes) {
        try {
            orderService.deleteOrder(id);
            redirectAttributes.addFlashAttribute("successMessage", "Заказ успешно удалён");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Ошибка при удалении заказа: " + e.getMessage());
        }
        return "redirect:/orders";
    }
}