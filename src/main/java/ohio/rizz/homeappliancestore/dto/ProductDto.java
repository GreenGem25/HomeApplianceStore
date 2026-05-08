package ohio.rizz.homeappliancestore.dto;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductDto {
    private UUID id;
    private String name;
    private String description;
    private BigDecimal price;
    private BigDecimal costPrice;
    private int stockQuantity;
    private String manufacturer;
    private int warrantyPeriod;
    private Integer vatRate;
    private String imagePath;
    private LocalDateTime createdAt;
    private UUID categoryId;
    private String categoryName;
    private UUID supplierId;
    private String supplierName;
    private int totalSold;
    private boolean inStock;
}