package ohio.rizz.homeappliancestore.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
public class OrderEditDto {
    private UUID id;
    private String orderNumber;
    private String statusDisplayName;
    private String customerName;
    private LocalDateTime orderDate;
    private Integer customerDiscount;
    private BigDecimal totalPrice;

    @NotNull(message = "ID клиента обязателен")
    private UUID customerId;

    @NotBlank(message = "Адрес доставки обязателен")
    private String shippingAddress;

    @NotEmpty(message = "Добавьте хотя бы один товар")
    @Valid
    private List<OrderItemCreateDto> items;
}