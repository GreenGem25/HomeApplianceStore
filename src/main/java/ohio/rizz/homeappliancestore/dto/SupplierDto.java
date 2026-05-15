package ohio.rizz.homeappliancestore.dto;

import lombok.*;

import java.util.List;
import java.util.UUID;

@Data
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SupplierDto {
    private UUID id;
    private String name;
    private String contactName;
    private String phone;
    private String email;
    private String address;
    private String inn;
    private List<ProductDto> products;
    private int productsCount;
}