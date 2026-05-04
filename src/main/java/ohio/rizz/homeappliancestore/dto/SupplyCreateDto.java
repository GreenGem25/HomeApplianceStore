package ohio.rizz.homeappliancestore.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SupplyCreateDto {
    @NotNull(message = "Поставщик обязателен")
    private UUID supplierId;

    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    @PastOrPresent(message = "Дата поставки не может быть в будущем")
    private LocalDateTime supplyDate;

    private String supplyNumber;

    private String notes;

    @NotEmpty(message = "Добавьте хотя бы один товар")
    @Valid
    @Builder.Default
    private List<SupplyItemCreateDto> items = new ArrayList<>();
}