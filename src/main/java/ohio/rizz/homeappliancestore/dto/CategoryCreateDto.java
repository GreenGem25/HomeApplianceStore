package ohio.rizz.homeappliancestore.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.UUID;

@Data
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CategoryCreateDto {
    @NotBlank(message = "Название категории обязательно")
    @Size(min = 2, max = 100, message = "Название должно содержать от 2 до 100 символов")
    private String name;

    @Size(max = 500, message = "Описание не должно превышать 500 символов")
    private String description;

    private UUID parentCategoryId;
}