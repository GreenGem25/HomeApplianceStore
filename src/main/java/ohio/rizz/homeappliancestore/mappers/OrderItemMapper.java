package ohio.rizz.homeappliancestore.mappers;

import ohio.rizz.homeappliancestore.dto.OrderItemCreateDto;
import ohio.rizz.homeappliancestore.dto.OrderItemDto;
import ohio.rizz.homeappliancestore.entities.OrderItem;
import ohio.rizz.homeappliancestore.entities.Product;
import org.mapstruct.*;
import java.math.BigDecimal;
import java.util.List;

@Mapper(componentModel = "spring",
        imports = BigDecimal.class,
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface OrderItemMapper {

    @Mapping(target = "totalPrice", expression = "java(item.getOrderPrice().multiply(BigDecimal.valueOf(item.getOrderQuantity())))")
    @Mapping(target = "productId", source = "product.id")
    @Mapping(target = "productName", source = "product.name")
    @Mapping(target = "productImage", source = "product.imagePath")
    @Mapping(target = "price", source = "product.price")
    @Mapping(target = "quantity", source = "orderQuantity")
    OrderItemDto toDto(OrderItem item);

    List<OrderItemDto> toDto(List<OrderItem> items);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "order", ignore = true)
    @Mapping(target = "orderPrice", ignore = true)
    @Mapping(target = "product", ignore = true)
    OrderItem toEntity(OrderItemCreateDto createDto);

    @AfterMapping
    default void setProduct(@MappingTarget OrderItem item, OrderItemCreateDto createDto) {
        if (createDto.getProductId() != null) {
            Product product = new Product();
            product.setId(createDto.getProductId());
            item.setProduct(product);
        }
    }
}