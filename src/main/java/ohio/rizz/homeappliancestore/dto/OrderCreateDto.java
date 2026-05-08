package ohio.rizz.homeappliancestore.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.List;
import java.util.UUID;

@Data
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrderCreateDto {
    @NotNull(message = "ID клиента обязателен")
    private UUID customerId;

    private String shippingAddress;

    @NotEmpty(message = "Заказ должен содержать хотя бы одну позицию")
    private List<OrderItemCreateDto> items;
}