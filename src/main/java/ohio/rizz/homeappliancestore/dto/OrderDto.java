package ohio.rizz.homeappliancestore.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ohio.rizz.homeappliancestore.enums.OrderStatus;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderDto {
    private UUID id;
    private String orderNumber;
    private BigDecimal totalPrice;
    private BigDecimal finalPrice;
    private OrderStatus status;
    private String statusDisplayName;
    private String shippingAddress;
    private LocalDateTime orderDate;
    private UUID customerId;
    private List<OrderItemDto> items;
    private int itemsCount;
    // Из CustomerDto, для страницы order-details.html
    private String customerName;
    private String customerEmail;
    private Integer customerDiscount;
}