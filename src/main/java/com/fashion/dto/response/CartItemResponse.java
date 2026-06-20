package com.fashion.dto.response;

import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;

@Data @Builder
public class CartItemResponse {
    private Long id;
    private Long variantId;
    private String sku;
    private String size;
    private String color;
    private String colorHex;
    private Long productId;
    private String productName;
    private String productSlug;
    private String thumbnailUrl;
    private BigDecimal price;
    private Integer quantity;
    private BigDecimal subtotal;
    private Integer stockQty;
}
