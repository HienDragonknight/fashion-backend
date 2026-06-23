package com.fashion.dto.response;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * Aggregated response for the homepage — all sections in one call or individual endpoints.
 * All text fields carry locale-resolved values (vi or en) based on Accept-Language.
 */
public class HomepageResponse {

    @Data @Builder
    public static class BannerSlide {
        private Long id;
        private String badge;
        /** Raw English badge — for admin. */
        private String badgeEn;
        private String badgeColor;
        private String titleText;
        private String subtitle;
        /** Raw English subtitle — for admin. */
        private String subtitleEn;
        private String ctaText;
        /** Raw English CTA text — for admin. */
        private String ctaTextEn;
        private String textColor;
        private String overlayGradient;
        private String imageUrl;
        private String linkUrl;
        private Integer sortOrder;
    }

    @Data @Builder
    public static class CollectionItem {
        private Long id;
        /** Locale-resolved category name. */
        private String name;
        /** Raw English name — for admin. */
        private String nameEn;
        private String slug;
        private String imageUrl;
    }

    @Data @Builder
    public static class ProductCard {
        private Long id;
        /** Locale-resolved product name. */
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
        /** Locale-resolved title. */
        private String title;
        /** Raw English title — for admin. */
        private String titleEn;
        private String slug;
        private LocalDate date;
        private String image;
        /** Locale-resolved excerpt. */
        private String excerpt;
    }
}
