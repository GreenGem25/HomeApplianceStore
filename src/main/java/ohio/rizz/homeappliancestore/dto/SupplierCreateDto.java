package ohio.rizz.homeappliancestore.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SupplierCreateDto {
    @NotBlank(message = "Название компании обязательно")
    private String name;

    private String contactName;

    @Pattern(regexp = "^\\+?[0-9\\-\\s]{10,15}$", message = "Некорректный формат телефона")
    private String phone;

    @Email(message = "Некорректный формат email")
    private String email;

    private String address;
}