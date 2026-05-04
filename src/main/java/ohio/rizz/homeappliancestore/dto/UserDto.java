package ohio.rizz.homeappliancestore.dto;

import jakarta.persistence.*;
import lombok.*;
import ohio.rizz.homeappliancestore.enums.Role;

import java.util.UUID;

@Data
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {
    private UUID id;
    private String username;
    private String password;
    private Role role;
    private String fullName;
    private boolean enabled = true;
}
