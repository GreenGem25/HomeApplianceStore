package ohio.rizz.homeappliancestore.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.UUID;

@Data
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrderItemCreateDto {
    @NotNull(message = "ID товара обязателен")
    private UUID productId;

    @Min(value = 1, message = "Количество должно быть не менее 1")
    private int quantity;

}