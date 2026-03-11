package ohio.rizz.homeappliancestore.mappers;

import ohio.rizz.homeappliancestore.dto.SupplierCreateDto;
import ohio.rizz.homeappliancestore.dto.SupplierDto;
import ohio.rizz.homeappliancestore.entities.Supplier;
import org.mapstruct.*;
import java.util.List;

@Mapper(componentModel = "spring",
        uses = {ProductMapper.class},
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface SupplierMapper {

    @Mapping(target = "productsCount", expression = "java(supplier.getProducts() != null ? supplier.getProducts().size() : 0)")
    SupplierDto toDto(Supplier supplier);

    List<SupplierDto> toDto(List<Supplier> suppliers);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "products", ignore = true)
    Supplier toEntity(SupplierCreateDto createDto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "products", ignore = true)
    void updateEntity(@MappingTarget Supplier supplier, SupplierCreateDto createDto);
}