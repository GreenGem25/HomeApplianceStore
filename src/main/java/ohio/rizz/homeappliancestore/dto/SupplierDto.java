package ohio.rizz.homeappliancestore.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SupplierDto {
    private Long id;

    @NotBlank(message = "Название поставщика обязательно")
    @Size(min = 3, max = 100, message = "Название должно быть от 3 до 100 символов")
    private String name;

    @NotBlank(message = "Контактное имя обязательно")
    @Size(min = 2, max = 50, message = "Имя должно быть от 2 до 50 символов")
    private String contactName;

    @NotBlank(message = "Email обязателен")
    @Email(message = "Некорректный формат email")
    private String email;

    @Size(max = 20, message = "Телефон не должен превышать 20 символов")
    private String phone;

    @Size(max = 200, message = "Адрес не должен превышать 200 символов")
    private String address;
}
