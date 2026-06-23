package com.fashion.dto.response;

import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Public-facing product response.
 *
 * <p>The {@code name} and {@code description} fields carry the locale-resolved value
 * (Vietnamese or English depending on the request's Accept-Language header).
 *
 * <p>The {@code nameEn} / {@code descriptionEn} raw fields are exposed so that the
 * admin panel can always display and edit both languages regardless of the request locale.
 * Public frontend clients should use only {@code name} and {@code description}.
 */
@Data @Builder
public class ProductResponse {
    private Long id;

    /** Locale-resolved name (vi or en based on Accept-Language). */
    private String name;

    /** Raw Vietnamese name — for admin bilingual editor. */
    private String nameVi;

    /** Raw English name — for admin bilingual editor. */
    private String nameEn;

    private String slug;

    /** Locale-resolved description. */
    private String description;

    /** Raw Vietnamese description — for admin. */
    private String descriptionVi;

    /** Raw English description — for admin. */
    private String descriptionEn;

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
        /** Locale-resolved brand name. */
        private String name;
        private String nameEn;
        private String slug;
        private String logoUrl;
    }

    @Data @Builder
    public static class CategoryInfo {
        private Long id;
        /** Locale-resolved category name. */
        private String name;
        private String nameEn;
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
        private List<String> imageUrls;
    }
}
