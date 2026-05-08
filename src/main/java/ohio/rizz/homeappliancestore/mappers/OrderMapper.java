package ohio.rizz.homeappliancestore.mappers;

import ohio.rizz.homeappliancestore.dto.OrderCreateDto;
import ohio.rizz.homeappliancestore.dto.OrderDto;
import ohio.rizz.homeappliancestore.entities.Customer;
import ohio.rizz.homeappliancestore.entities.Order;
import ohio.rizz.homeappliancestore.entities.OrderItem;
import org.mapstruct.*;

import java.math.BigDecimal;
import java.util.List;

@Mapper(componentModel = "spring",
        uses = {OrderItemMapper.class, CustomerMapper.class},
        imports = {OrderItem.class, BigDecimal.class},
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface OrderMapper {

    @Mapping(target = "finalPrice", expression = "java(order.calculateFinalPrice())")
    @Mapping(target = "statusDisplayName", expression = "java(order.getStatus().getDisplayName())")
    @Mapping(target = "customerId", source = "customer.id")
    @Mapping(target = "customerName", expression = "java(order.getCustomer().getFirstName() + \" \" + order.getCustomer().getLastName())")
    @Mapping(target = "customerEmail", source = "customer.email")
    @Mapping(target = "customerDiscount", source = "customer.discount")
    @Mapping(target = "items", source = "orderItems")
    @Mapping(target = "itemsCount", expression = "java(order.getOrderItems() != null ? order.getOrderItems().size() : 0)")
    @Mapping(target = "totalVatAmount", expression = "java(order.getOrderItems().stream().map(OrderItem::getVatAmount).reduce(BigDecimal.ZERO, BigDecimal::add))")
    OrderDto toDto(Order order);

    List<OrderDto> toDto(List<Order> orders);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "orderNumber", ignore = true)
    @Mapping(target = "totalPrice", ignore = true)
    @Mapping(target = "status", constant = "IN_PROGRESS")
    @Mapping(target = "orderDate", ignore = true)
    @Mapping(target = "orderItems", ignore = true)
    @Mapping(target = "customer", ignore = true)
    Order toEntity(OrderCreateDto createDto);

    @AfterMapping
    default void setCustomer(@MappingTarget Order order, OrderCreateDto createDto) {
        if (createDto.getCustomerId() != null) {
            Customer customer = new Customer();
            customer.setId(createDto.getCustomerId());
            order.setCustomer(customer);
        }
    }
}