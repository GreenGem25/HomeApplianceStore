package ohio.rizz.homeappliancestore.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductCreateDto {
    @NotBlank(message = "Название товара обязательно")
    private String name;

    private String description;

    @NotNull(message = "Цена обязательна")
    @DecimalMin(value = "0.01", message = "Цена должна быть больше 0")
    private BigDecimal price;

    @Min(value = 0, message = "Количество не может быть отрицательным")
    private int stockQuantity;

    private String manufacturer;

    @Min(value = 0, message = "Срок гарантии не может быть отрицательным")
    private int warrantyPeriod;

    private UUID categoryId;

    private UUID supplierId;
}