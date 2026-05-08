package ohio.rizz.homeappliancestore.dto;

import lombok.*;
import ohio.rizz.homeappliancestore.enums.Role;

import java.util.UUID;

@Data
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {
    private UUID id;
    private String username;
    private String password;
    private Role role;
    private String fullName;
    @Builder.Default
    private boolean enabled = true;
}
