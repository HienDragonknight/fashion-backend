package com.fashion.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "banners")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Banner {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    // Hero slider specific fields
    @Column(length = 100)
    private String badge;

    @Column(name = "badge_color", length = 20)
    @Builder.Default
    private String badgeColor = "#FCCE00";

    @Column(name = "title_text")
    private String titleText;

    @Column(length = 500)
    private String subtitle;

    @Column(name = "cta_text", length = 100)
    @Builder.Default
    private String ctaText = "Khám phá ngay";

    @Column(name = "text_color", length = 20)
    @Builder.Default
    private String textColor = "#ffffff";

    @Column(name = "overlay_gradient", columnDefinition = "TEXT")
    private String overlayGradient;

    @Column(name = "image_url", nullable = false, columnDefinition = "TEXT")
    private String imageUrl;

    @Column(name = "link_url", columnDefinition = "TEXT")
    private String linkUrl;

    @Column(nullable = false, length = 50)
    @Builder.Default
    private String position = "HOME_HERO";

    @Column(name = "is_active", nullable = false)
    @Builder.Default
    private Boolean isActive = true;

    @Column(name = "sort_order", nullable = false)
    @Builder.Default
    private Integer sortOrder = 0;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
}
