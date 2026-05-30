package ohio.rizz.homeappliancestore.dto;

import lombok.*;
import ohio.rizz.homeappliancestore.enums.ExpenseType;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Data
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ExpenseDto {
    private UUID id;
    private BigDecimal amount;
    private String description;
    private ExpenseType type;
    private LocalDate expenseDate;
}
