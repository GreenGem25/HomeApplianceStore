package ohio.rizz.homeappliancestore.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
public class CustomerDto {
    private Long id;

    @NotBlank(message = "Имя обязательно")
    @Size(min = 2, max = 50, message = "Имя должно быть от 2 до 50 символов")
    private String firstName;

    @NotBlank(message = "Фамилия обязательна")
    @Size(min = 2, max = 50, message = "Фамилия должна быть от 2 до 50 символов")
    private String lastName;

    @NotBlank(message = "Email обязателен")
    @Email(message = "Некорректный формат email")
    private String email;

    @Size(max = 20, message = "Телефон не должен превышать 20 символов")
    private String phone;

    @Size(max = 200, message = "Адрес не должен превышать 200 символов")
    private String address;

    private String imagePath;
    private MultipartFile imageFile;
    private boolean removeImage;
}
