package ohio.rizz.homeappliancestore.mappers;

import ohio.rizz.homeappliancestore.dto.OrderCreateDto;
import ohio.rizz.homeappliancestore.dto.OrderDto;
import ohio.rizz.homeappliancestore.dto.OrderEditDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface OrderEditMapper {

    @Mapping(target = "id", source = "id")
    @Mapping(target = "orderNumber", source = "orderNumber")
    @Mapping(target = "statusDisplayName", source = "statusDisplayName")
    @Mapping(target = "customerName", source = "customerName")
    @Mapping(target = "orderDate", source = "orderDate")
    @Mapping(target = "customerDiscount", source = "customerDiscount")
    @Mapping(target = "totalPrice", source = "totalPrice")
    @Mapping(target = "customerId", source = "customerId")
    @Mapping(target = "shippingAddress", source = "shippingAddress")
    @Mapping(target = "items", source = "items")
    OrderEditDto toEditDto(OrderDto orderDto);

    // Маппим из OrderEditDto в OrderCreateDto (для отправки на сервер)
    @Mapping(target = "customerId", source = "customerId")
    @Mapping(target = "shippingAddress", source = "shippingAddress")
    @Mapping(target = "items", source = "items")
    OrderCreateDto toCreateDto(OrderEditDto editDto);

}