package com.fashion.dto.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.*;
import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

@Data
public class ProductRequest {

    @NotBlank
    @Size(max = 500)
    private String name;

    @Size(max = 500)
    private String nameEn;

    private String description;

    private String descriptionEn;

    private Long brandId;

    private Long categoryId;

    @NotNull
    @DecimalMin("0.0")
    private BigDecimal basePrice;

    private BigDecimal salePrice;

    private String thumbnailUrl;

    private Boolean isActive = true;

    private Boolean isFeatured = false;

    private Integer weightGrams = 300;

    private List<String> imageUrls;

    private List<VariantRequest> variants;

    /** Ignore unknown fields (e.g. id, label) sent by the frontend from loaded product data. */
    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class VariantRequest {
        private String size;
        private String color;
        private String colorHex;

        @NotBlank
        private String sku;

        @Min(0)
        private Integer stockQty = 0;

        private BigDecimal priceAdjustment = BigDecimal.ZERO;

        private List<String> imageUrls;
    }
}

