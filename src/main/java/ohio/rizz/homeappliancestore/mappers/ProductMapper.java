package ohio.rizz.homeappliancestore.mappers;

import ohio.rizz.homeappliancestore.dto.ProductCreateDto;
import ohio.rizz.homeappliancestore.dto.ProductDto;
import ohio.rizz.homeappliancestore.entities.Category;
import ohio.rizz.homeappliancestore.entities.OrderItem;
import ohio.rizz.homeappliancestore.entities.Product;
import ohio.rizz.homeappliancestore.entities.Supplier;
import org.mapstruct.*;
import java.util.List;

@Mapper(componentModel = "spring",
        uses = {CategoryMapper.class, SupplierMapper.class},
        imports = {OrderItem.class, java.util.stream.Collectors.class},
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ProductMapper {

    @Mapping(target = "categoryId", source = "category.id")
    @Mapping(target = "categoryName", source = "category.name")
    @Mapping(target = "supplierId", source = "supplier.id")
    @Mapping(target = "supplierName", source = "supplier.name")
    @Mapping(target = "totalSold", expression = "java(product.getOrderItems() != null ? product.getOrderItems().stream().mapToInt(OrderItem::getOrderQuantity).sum() : 0)")
    @Mapping(target = "inStock", expression = "java(product.getStockQuantity() > 0)")
    ProductDto toDto(Product product);

    List<ProductDto> toDto(List<Product> products);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "imagePath", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "orderItems", ignore = true)
    @Mapping(target = "category", ignore = true)
    @Mapping(target = "supplier", ignore = true)
    Product toEntity(ProductCreateDto createDto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "imagePath", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "orderItems", ignore = true)
    @Mapping(target = "category", ignore = true)
    @Mapping(target = "supplier", ignore = true)
    void updateEntity(@MappingTarget Product product, ProductCreateDto createDto);

    @AfterMapping
    default void setCategory(@MappingTarget Product product, ProductCreateDto createDto) {
        if (createDto.getCategoryId() != null) {
            Category category = new Category();
            category.setId(createDto.getCategoryId());
            product.setCategory(category);
        }
    }

    @AfterMapping
    default void setSupplier(@MappingTarget Product product, ProductCreateDto createDto) {
        if (createDto.getSupplierId() != null) {
            Supplier supplier = new Supplier();
            supplier.setId(createDto.getSupplierId());
            product.setSupplier(supplier);
        }
    }
}