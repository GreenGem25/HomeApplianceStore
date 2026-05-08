package ohio.rizz.homeappliancestore.dto;

import lombok.*;

import java.math.BigDecimal;

@Data
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ChartDataPoint {
    private String label;
    private BigDecimal value;
}