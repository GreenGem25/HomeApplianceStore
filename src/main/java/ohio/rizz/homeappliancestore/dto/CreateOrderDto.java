package ohio.rizz.homeappliancestore.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class CreateOrderDto {
    private Long customerId;
    private String shippingAddress;
    private List<OrderItemRequest> items;
}