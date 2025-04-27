package ohio.rizz.homeappliancestore.DTOs;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import ohio.rizz.homeappliancestore.entities.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class ProductDto {
    private Long id;

    @NotBlank(message = "Название обязательно")
    private String name;

    private String description;

    @NotNull(message = "Цена обязательна")
    @DecimalMin(value = "0.01", message = "Цена должна быть больше 0")
    private BigDecimal price;

    @NotNull(message = "Количество обязательно")
    @Min(value = 0, message = "Количество не может быть отрицательным")
    private int stockQuantity;

    private String manufacturer;

    private int warrantyPeriod;

    private String imagePath;

    private LocalDateTime createdAt;

    private Category category;

    private Store store;

    private Supplier supplier;

}
