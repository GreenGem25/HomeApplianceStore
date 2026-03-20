package ohio.rizz.homeappliancestore.mappers;

import ohio.rizz.homeappliancestore.dto.*;
import ohio.rizz.homeappliancestore.entities.Supplier;
import ohio.rizz.homeappliancestore.entities.Supply;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring",
        uses = {SupplyItemMapper.class, SupplierMapper.class},
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface SupplyMapper {

    @Mapping(target = "supplierId", source = "supplier.id")
    @Mapping(target = "supplierName", source = "supplier.name")
    @Mapping(target = "supplierContact", source = "supplier.contactName")
    @Mapping(target = "statusDisplayName", expression = "java(supply.getStatus().getDisplayName())")
    @Mapping(target = "totalItems", expression = "java(supply.getTotalItems())")
    @Mapping(target = "totalQuantity", expression = "java(supply.getTotalQuantity())")
    @Mapping(target = "totalValue", expression = "java(supply.getTotalValue())")
    @Mapping(target = "items", source = "supplyItems")
    SupplyDto toDto(Supply supply);

    List<SupplyDto> toDto(List<Supply> supplies);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "supplyNumber", ignore = true)
    @Mapping(target = "status", constant = "PENDING")
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "supplier", ignore = true)
    @Mapping(target = "supplyItems", ignore = true)
    Supply toEntity(SupplyCreateDto createDto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "supplyNumber", ignore = true)
    @Mapping(target = "supplier", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "supplyItems", ignore = true)
    void updateEntity(@MappingTarget Supply supply, SupplyUpdateDto updateDto);

    @AfterMapping
    default void setSupplier(@MappingTarget Supply supply, SupplyCreateDto createDto) {
        if (createDto.getSupplierId() != null) {
            Supplier supplier = new Supplier();
            supplier.setId(createDto.getSupplierId());
            supply.setSupplier(supplier);
        }
    }

    @AfterMapping
    default void setSupplyDate(@MappingTarget Supply supply, SupplyCreateDto createDto) {
        if (createDto.getSupplyDate() == null) {
            supply.setSupplyDate(java.time.LocalDateTime.now());
        } else {
            supply.setSupplyDate(createDto.getSupplyDate());
        }
    }
}