package ohio.rizz.homeappliancestore.mappers;

import ohio.rizz.homeappliancestore.dto.ExpenseCreateDto;
import ohio.rizz.homeappliancestore.dto.ExpenseDto;
import ohio.rizz.homeappliancestore.entities.Expense;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ExpenseMapper {
    ExpenseDto toDto(Expense expense);

    List<ExpenseDto> toDto(List<Expense> expenses);

    @Mapping(target = "id", ignore = true)
    Expense toEntity(ExpenseCreateDto expenseDto);
}
