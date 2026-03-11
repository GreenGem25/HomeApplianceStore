package ohio.rizz.homeappliancestore.mappers;

import ohio.rizz.homeappliancestore.dto.CategoryCreateDto;
import ohio.rizz.homeappliancestore.dto.CategoryDto;
import ohio.rizz.homeappliancestore.entities.Category;
import org.mapstruct.*;
import java.util.List;

@Mapper(componentModel = "spring",
        uses = {ProductMapper.class},
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CategoryMapper {

    @Mapping(target = "parentCategoryId", source = "parentCategory.id")
    @Mapping(target = "parentCategoryName", source = "parentCategory.name")
    @Mapping(target = "productCount", expression = "java(category.getProducts() != null ? category.getProducts().size() : 0)")
    CategoryDto toDto(Category category);

    List<CategoryDto> toDto(List<Category> categories);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "parentCategory", ignore = true)
    @Mapping(target = "childCategories", ignore = true)
    @Mapping(target = "products", ignore = true)
    Category toEntity(CategoryCreateDto createDto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "parentCategory", ignore = true)
    @Mapping(target = "childCategories", ignore = true)
    @Mapping(target = "products", ignore = true)
    void updateEntity(@MappingTarget Category category, CategoryCreateDto createDto);

    @AfterMapping
    default void setParentCategory(@MappingTarget Category category, CategoryCreateDto createDto) {
        if (createDto.getParentCategoryId() != null) {
            Category parent = new Category();
            parent.setId(createDto.getParentCategoryId());
            category.setParentCategory(parent);
        }
    }
}