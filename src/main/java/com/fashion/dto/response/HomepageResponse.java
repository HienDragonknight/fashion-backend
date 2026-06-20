package com.fashion.dto.response;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * Aggregated response for the homepage — all sections in one call or individual endpoints.
 */
public class HomepageResponse {

    @Data @Builder
    public static class BannerSlide {
        private Long id;
        private String badge;
        private String badgeColor;
        private String titleText;
        private String subtitle;
        private String ctaText;
        private String textColor;
        private String overlayGradient;
        private String imageUrl;
        private String linkUrl;
        private Integer sortOrder;
    }

    @Data @Builder
    public static class CollectionItem {
        private Long id;
        private String name;
        private String slug;
        private String imageUrl;
    }

    @Data @Builder
    public static class ProductCard {
        private Long id;
        private String name;
        private String slug;
        private BigDecimal price;
        private BigDecimal originalPrice;
        private String image;
    }

    @Data @Builder
    public static class ProductSection {
        private String id;
        private String title;
        private String viewMoreLink;
        private List<ProductCard> products;
    }

    @Data @Builder
    public static class BlogPostItem {
        private Long id;
        private String title;
        private String slug;
        private LocalDate date;
        private String image;
    }
}
