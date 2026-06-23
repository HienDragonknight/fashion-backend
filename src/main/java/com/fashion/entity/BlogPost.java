package com.fashion.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "blog_posts")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class BlogPost {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 500)
    private String title;

    /** English translation of title. */
    @Column(name = "title_en", length = 500)
    private String titleEn;

    /** Short excerpt shown on listing cards (Vietnamese). */
    @Column(columnDefinition = "TEXT")
    private String excerpt;

    /** Short excerpt in English. */
    @Column(name = "excerpt_en", columnDefinition = "TEXT")
    private String excerptEn;

    /** Full article content in Vietnamese (HTML or Markdown). */
    @Column(columnDefinition = "LONGTEXT")
    private String content;

    /** Full article content in English. */
    @Column(name = "content_en", columnDefinition = "LONGTEXT")
    private String contentEn;

    @Column(nullable = false, unique = true, length = 500)
    private String slug;

    @Column(nullable = false)
    private LocalDate date;

    @Column(name = "image_url", nullable = false, columnDefinition = "TEXT")
    private String imageUrl;

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
