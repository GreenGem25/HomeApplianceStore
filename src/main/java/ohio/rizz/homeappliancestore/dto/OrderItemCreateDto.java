package ohio.rizz.homeappliancestore.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderItemCreateDto {
    @NotNull(message = "ID товара обязателен")
    private UUID productId;

    @Min(value = 1, message = "Количество должно быть не менее 1")
    private int quantity;

}