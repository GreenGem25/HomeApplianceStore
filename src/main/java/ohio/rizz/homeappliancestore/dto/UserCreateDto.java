package ohio.rizz.homeappliancestore.dto;

import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import ohio.rizz.homeappliancestore.enums.Role;

@Data
@Getter
@Setter
@Builder
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
