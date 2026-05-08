package ohio.rizz.homeappliancestore.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import ohio.rizz.homeappliancestore.enums.Role;

@Data
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserCreateDto {
    @NotBlank
    private String username;

    @NotBlank @Size(min = 4)
    private String password;

    private String fullName;

    @NotNull
    private Role role;
}
