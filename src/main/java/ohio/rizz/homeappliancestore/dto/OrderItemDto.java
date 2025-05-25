package ohio.rizz.homeappliancestore.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class OrderItemDto {
    private Long id;
    private int quantity;
    private BigDecimal price;
    private Long productId;
    private String productName;
    private String productManufacturer;
    private String productImagePath;
}