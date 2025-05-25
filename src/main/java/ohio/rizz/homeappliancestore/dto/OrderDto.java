package ohio.rizz.homeappliancestore.dto;

import lombok.Getter;
import lombok.Setter;
import ohio.rizz.homeappliancestore.entities.Order;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class OrderDto {
    private Long id;
    private BigDecimal totalPrice;
    private Order.OrderStatus status;
    private String shippingAddress;
    private LocalDateTime orderDate;
    private Long customerId;
    private String customerName;
    private Integer customerDiscount;
    private List<OrderItemDto> orderItems;
}