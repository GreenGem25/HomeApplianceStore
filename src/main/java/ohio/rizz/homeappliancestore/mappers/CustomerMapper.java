package ohio.rizz.homeappliancestore.mappers;

import ohio.rizz.homeappliancestore.dto.CustomerCreateDto;
import ohio.rizz.homeappliancestore.dto.CustomerDto;
import ohio.rizz.homeappliancestore.entities.Customer;
import org.mapstruct.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Mapper(componentModel = "spring",
        uses = {OrderMapper.class},
        imports = {BigDecimal.class, RoundingMode.class},
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CustomerMapper {

    @Mapping(target = "fullName", expression = "java(customer.getFirstName() + \" \" + customer.getLastName())")
    @Mapping(target = "ordersCount", expression = "java(customer.getOrders() != null ? customer.getOrders().size() : 0)")
    @Mapping(target = "averageOrderValue", source = "customer", qualifiedByName = "calculateAverageOrderValue")
    CustomerDto toDto(Customer customer);

    List<CustomerDto> toDto(List<Customer> customers);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "discount", constant = "0")
    @Mapping(target = "moneySpent", constant = "0")
    @Mapping(target = "imagePath", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "orders", ignore = true)
    Customer toEntity(CustomerCreateDto createDto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "discount", ignore = true)
    @Mapping(target = "moneySpent", ignore = true)
    @Mapping(target = "imagePath", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "orders", ignore = true)
    void updateEntity(@MappingTarget Customer customer, CustomerCreateDto createDto);

    @Named("calculateAverageOrderValue")
    default BigDecimal calculateAverageOrderValue(Customer customer) {
        if (customer.getOrders() == null || customer.getOrders().isEmpty()) {
            return BigDecimal.ZERO;
        }
        return customer.getMoneySpent()
                .divide(BigDecimal.valueOf(customer.getOrders().size()),
                        2, RoundingMode.HALF_UP);
    }
}