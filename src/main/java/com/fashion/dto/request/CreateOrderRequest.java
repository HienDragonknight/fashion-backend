package com.fashion.dto.request;

import jakarta.validation.constraints.*;
import lombok.Data;
import java.util.List;

@Data
public class CreateOrderRequest {

    @NotNull
    private Long addressId;

    @NotBlank
    private String paymentMethod; // COD | VNPAY

    @NotEmpty
    private List<OrderItemRequest> items;

    private String note;

    @Data
    public static class OrderItemRequest {
        @NotNull
        private Long variantId;

        @Min(1)
        private Integer quantity;
    }
}
