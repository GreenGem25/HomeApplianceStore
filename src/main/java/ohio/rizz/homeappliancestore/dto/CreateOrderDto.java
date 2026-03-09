package ohio.rizz.homeappliancestore.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class CreateOrderDto {
    private UUID customerId;
    private String shippingAddress;
    private List<OrderItemRequest> items;
}