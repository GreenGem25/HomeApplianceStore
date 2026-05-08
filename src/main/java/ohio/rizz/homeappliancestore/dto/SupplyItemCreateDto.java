package ohio.rizz.homeappliancestore.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SupplyItemCreateDto {
    @NotNull(message = "Товар обязателен")
    private UUID productId;

    @Min(value = 1, message = "Количество должно быть не менее 1")
    private int quantity;

    @NotNull(message = "Цена за единицу обязательна")
    @Min(value = 0, message = "Цена не может быть отрицательной")
    private BigDecimal pricePerUnit;

    // Поля для отображения в форме (read-only)
    private String productName;
    private int availableStock;
}