package ohio.rizz.homeappliancestore.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import ohio.rizz.homeappliancestore.enums.Role;

import java.util.UUID;

@Data
public class UserEditDto {
    private UUID id;
    @NotBlank
    private String fullName;
    @NotBlank
    private Role role;
    private String password;
}