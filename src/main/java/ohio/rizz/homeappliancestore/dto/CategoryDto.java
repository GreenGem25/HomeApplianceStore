package ohio.rizz.homeappliancestore.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CategoryDto {
    private Long id;
    private String name;
    private String description;
    private Long parentCategoryId;
    private String parentCategoryName;
}
