package ohio.rizz.homeappliancestore.dto;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CustomerDto {
    private UUID id;
    private String firstName;
    private String lastName;
    private String fullName;
    private String email;
    private String phone;
    private String address;
    private String imagePath;
    private Integer discount;
    private BigDecimal moneySpent;
    private LocalDateTime createdAt;
    private List<OrderDto> orders;
    private int ordersCount;
    private BigDecimal averageOrderValue;
}