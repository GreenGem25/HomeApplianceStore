package ohio.rizz.homeappliancestore.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.*;

import java.util.List;

@Data
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SupplyUpdateDto {
    private String notes;
    private String status;

    @NotEmpty(message = "Добавьте хотя бы один товар")
    @Valid
    private List<SupplyItemCreateDto> items;
}