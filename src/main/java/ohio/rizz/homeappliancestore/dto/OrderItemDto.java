package ohio.rizz.homeappliancestore.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
public class OrderItemDto {
    private UUID id;
    private int quantity;
    private BigDecimal price;
    private UUID productId;
    private String productName;
    private String productManufacturer;
    private String productImagePath;
    private int productStockQuantity;
}