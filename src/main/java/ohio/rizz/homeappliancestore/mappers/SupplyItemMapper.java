package ohio.rizz.homeappliancestore.mappers;

import ohio.rizz.homeappliancestore.dto.SupplyItemCreateDto;
import ohio.rizz.homeappliancestore.dto.SupplyItemDto;
import ohio.rizz.homeappliancestore.entities.Product;
import ohio.rizz.homeappliancestore.entities.SupplyItem;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring",
        imports = {java.math.BigDecimal.class},
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface SupplyItemMapper {

    @Mapping(target = "productId", source = "product.id")
    @Mapping(target = "productName", source = "product.name")
    @Mapping(target = "productManufacturer", source = "product.manufacturer")
    @Mapping(target = "productImagePath", source = "product.imagePath")
    @Mapping(target = "totalPrice", expression = "java(item.getPricePerUnit().multiply(BigDecimal.valueOf(item.getQuantity())))")
    SupplyItemDto toDto(SupplyItem item);

    List<SupplyItemDto> toDto(List<SupplyItem> items);

    @Mapping(target = "product", ignore = true)
    @Mapping(target = "supply", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    SupplyItem toEntity(SupplyItemCreateDto createDto);

    @AfterMapping
    default void setProduct(@MappingTarget SupplyItem item, SupplyItemCreateDto createDto) {
        if (createDto.getProductId() != null) {
            Product product = new Product();
            product.setId(createDto.getProductId());
            item.setProduct(product);
        }
    }
}