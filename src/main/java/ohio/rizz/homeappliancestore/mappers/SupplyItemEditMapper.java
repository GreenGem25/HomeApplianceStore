package ohio.rizz.homeappliancestore.mappers;

import ohio.rizz.homeappliancestore.dto.SupplyItemCreateDto;
import ohio.rizz.homeappliancestore.dto.SupplyItemDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface SupplyItemEditMapper {

    @Mapping(target = "productId", source = "productId")
    @Mapping(target = "quantity", source = "quantity")
    @Mapping(target = "pricePerUnit", source = "pricePerUnit")
    @Mapping(target = "productName", source = "productName")
    @Mapping(target = "availableStock", source = "availableStock")
    SupplyItemCreateDto toCreateDto(SupplyItemDto itemDto);
}