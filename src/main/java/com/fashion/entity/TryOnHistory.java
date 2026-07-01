package com.fashion.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "try_on_history")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class TryOnHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "product_id")
    private Long productId;

    @Column(name = "product_name", length = 255)
    private String productName;

    @Column(name = "original_image_url", columnDefinition = "TEXT", nullable = false)
    private String originalImageUrl;

    @Column(name = "generated_image_url", columnDefinition = "TEXT")
    private String generatedImageUrl;

    @Column(name = "status", length = 20, nullable = false)
    @Builder.Default
    private String status = "PENDING";  // PENDING | COMPLETED | FAILED

    @Column(name = "error_message", columnDefinition = "TEXT")
    private String errorMessage;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
}
