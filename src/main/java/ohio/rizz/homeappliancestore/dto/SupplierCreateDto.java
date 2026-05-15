package ohio.rizz.homeappliancestore.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.*;

@Data
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SupplierCreateDto {
    @NotBlank(message = "Название компании обязательно")
    private String name;

    private String contactName;

    @Pattern(regexp = "^\\+7 \\(\\d{3}\\) \\d{3}-\\d{2}-\\d{2}$", message = "Телефон должен быть в формате +7 (XXX) XXX-XX-XX")
    private String phone;

    @Email(message = "Некорректный формат email")
    private String email;

    private String address;

    @Pattern(
            regexp = "^(\\d{10}|\\d{12})?$",
            message = "ИНН должен состоять из 10 или 12 цифр"
    )
    private String inn;
}