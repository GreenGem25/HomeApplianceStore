package ohio.rizz.homeappliancestore.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.math.BigDecimal;

@Getter @Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TopProductDto {
    private String productName;
    private int totalQuantitySold;
    private BigDecimal totalRevenue;
}