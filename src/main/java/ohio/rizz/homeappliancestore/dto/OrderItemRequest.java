package ohio.rizz.homeappliancestore.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class OrderItemRequest {
    private UUID productId;
    private int quantity;
}