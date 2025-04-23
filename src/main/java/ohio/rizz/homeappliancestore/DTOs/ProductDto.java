package ohio.rizz.homeappliancestore.DTOs;

import lombok.Getter;
import lombok.Setter;
import ohio.rizz.homeappliancestore.entities.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class ProductDto {
    private Long id;
    private String name;
    private String description;
    private BigDecimal price;
    private int stockQuantity;
    private String manufacturer;
    private int warrantyPeriod;
    private String imagePath;
    private LocalDateTime createdAt;
    private Category category;
    private Store store;
    private Supplier supplier;
}
