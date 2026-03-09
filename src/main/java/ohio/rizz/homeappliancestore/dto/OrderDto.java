package ohio.rizz.homeappliancestore.dto;

import lombok.Getter;
import lombok.Setter;
import ohio.rizz.homeappliancestore.entities.Order;
import ohio.rizz.homeappliancestore.enums.OrderStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class OrderDto {
    private UUID id;
    private BigDecimal totalPrice;
    private OrderStatus status;
    private String shippingAddress;
    private LocalDateTime orderDate;
    private UUID customerId;
    private String customerName;
    private Integer customerDiscount;
    private String orderNumber;
    private List<OrderItemDto> orderItems;
}