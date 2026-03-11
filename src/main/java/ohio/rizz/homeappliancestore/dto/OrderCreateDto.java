package ohio.rizz.homeappliancestore.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderCreateDto {
    @NotNull(message = "ID клиента обязателен")
    private UUID customerId;

    private String shippingAddress;

    @NotEmpty(message = "Заказ должен содержать хотя бы одну позицию")
    private List<OrderItemCreateDto> items;
}