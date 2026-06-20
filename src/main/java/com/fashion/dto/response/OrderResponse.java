package com.fashion.dto.response;

import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data @Builder
public class OrderResponse {
    private Long id;
    private Long userId;
    private String status;
    private String paymentMethod;
    private String paymentStatus;
    private BigDecimal shippingFee;
    private BigDecimal subtotal;
    private BigDecimal discount;
    private BigDecimal total;
    private String ghnOrderCode;
    private String note;
    private String snapshotAddress;
    private List<OrderItemResponse> items;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @Data @Builder
    public static class OrderItemResponse {
        private Long id;
        private Long productId;
        private String productName;
        private String variantLabel;
        private String thumbnailUrl;
        private BigDecimal price;
        private Integer quantity;
        private BigDecimal subtotal;
    }
}
