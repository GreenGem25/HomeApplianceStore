package ohio.rizz.homeappliancestore.dto;

import lombok.*;

import java.util.UUID;

@Data
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SettingsDto {
    private UUID id;
    private String shopName;
    private String address;
    private String phone;
    private String email;
}
