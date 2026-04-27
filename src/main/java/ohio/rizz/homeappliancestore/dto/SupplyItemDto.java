package ohio.rizz.homeappliancestore.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SupplyItemDto {
    private UUID id;
    private UUID productId;
    private String productName;
    private String productManufacturer;
    private String productImagePath;
    private int quantity;
    private BigDecimal pricePerUnit;
    private BigDecimal totalPrice;
    private int availableStock;
}