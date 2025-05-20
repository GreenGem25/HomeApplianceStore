package ohio.rizz.homeappliancestore.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CategoryDto {
    private Long id;

    @NotBlank(message = "Название категории обязательно")
    @Size(min = 2, max = 50, message = "Название должно быть от 2 до 50 символов")
    private String name;

    @Size(max = 500, message = "Описание не должно превышать 500 символов")
    private String description;

    private Long parentCategoryId;
}
