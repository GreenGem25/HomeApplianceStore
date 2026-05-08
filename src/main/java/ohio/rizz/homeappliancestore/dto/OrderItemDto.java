package ohio.rizz.homeappliancestore.dto;

import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrderItemDto {
    private UUID id;
    private int quantity;
    private BigDecimal price;
    private BigDecimal totalPrice;
    private UUID productId;
    private String productName;
    private String productImage;
}