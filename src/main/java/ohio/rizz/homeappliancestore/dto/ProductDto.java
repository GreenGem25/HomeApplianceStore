package ohio.rizz.homeappliancestore.dto;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;

@Getter
@Setter
public class ProductDto {
    private Long id;

    @NotBlank(message = "Название товара обязательно")
    @Size(min = 3, max = 100, message = "Название должно быть от 3 до 100 символов")
    private String name;

    @Size(max = 1000, message = "Описание не должно превышать 1000 символов")
    private String description;

    @NotNull(message = "Цена обязательна")
    @DecimalMin(value = "0.01", message = "Цена должна быть больше 0")
    private BigDecimal price;

    @NotNull(message = "Количество обязательно")
    @Min(value = 0, message = "Количество не может быть отрицательным")
    private Integer stockQuantity;

    @Size(max = 50, message = "Название производителя не должно превышать 50 символов")
    private String manufacturer;

    @Min(value = 0, message = "Гарантия не может быть отрицательной")
    private Integer warrantyPeriod;

    @NotNull(message = "Поставщик обязателен")
    private Long supplierId;

    @NotNull(message = "Категория обязательна")
    private Long categoryId;

    private String imagePath;
    private MultipartFile imageFile;

}