package com.fashion.dto.response;

import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data @Builder
public class ProductResponse {
    private Long id;
    private String name;
    private String slug;
    private String description;
    private BrandInfo brand;
    private CategoryInfo category;
    private BigDecimal basePrice;
    private BigDecimal salePrice;
    private BigDecimal effectivePrice;
    private String thumbnailUrl;
    private Boolean isActive;
    private Boolean isFeatured;
    private Integer weightGrams;
    private Integer soldCount;
    private Integer viewCount;
    private Double avgRating;
    private Long reviewCount;
    private List<String> imageUrls;
    private List<VariantResponse> variants;
    private LocalDateTime createdAt;

    @Data @Builder
    public static class BrandInfo {
        private Long id;
        private String name;
        private String slug;
        private String logoUrl;
    }

    @Data @Builder
    public static class CategoryInfo {
        private Long id;
        private String name;
        private String slug;
    }

    @Data @Builder
    public static class VariantResponse {
        private Long id;
        private String size;
        private String color;
        private String colorHex;
        private String sku;
        private Integer stockQty;
        private BigDecimal priceAdjustment;
        private String label;
    }
}
