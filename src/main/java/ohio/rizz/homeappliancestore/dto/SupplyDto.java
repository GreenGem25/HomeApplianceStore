package ohio.rizz.homeappliancestore.dto;

import lombok.*;
import ohio.rizz.homeappliancestore.entities.Supply;
import ohio.rizz.homeappliancestore.enums.SupplyStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SupplyDto {
    private UUID id;
    private String supplyNumber;
    private UUID supplierId;
    private String supplierName;
    private String supplierContact;
    private LocalDateTime supplyDate;
    private SupplyStatus status;
    private String statusDisplayName;
    private String notes;
    private BigDecimal logisticCost;
    private LocalDateTime createdAt;
    private List<SupplyItemDto> items;
    private int totalItems;
    private int totalQuantity;
    private double totalValue;
}