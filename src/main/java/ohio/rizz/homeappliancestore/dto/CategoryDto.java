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
public class CategoryDto {
    private UUID id;
    private String name;
    private String description;
    private UUID parentCategoryId;
    private String parentCategoryName;
    private List<CategoryDto> childCategories;
    private List<ProductDto> products;
    private int productCount;
}