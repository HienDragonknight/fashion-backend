package com.fashion.repository;

import com.fashion.entity.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ReviewRepository extends JpaRepository<Review, Long> {

    Page<Review> findByProductIdAndIsApprovedTrue(Long productId, Pageable pageable);

    Page<Review> findByIsApprovedFalse(Pageable pageable);

    Optional<Review> findByUserIdAndProductIdAndOrderId(Long userId, Long productId, Long orderId);

    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.product.id = :productId AND r.isApproved = true")
    Double avgRatingByProductId(@Param("productId") Long productId);

    @Query("SELECT COUNT(r) FROM Review r WHERE r.product.id = :productId AND r.isApproved = true")
    Long countByProductId(@Param("productId") Long productId);

    /** Batch: lấy avgRating cho nhiều product cùng lúc — tránh N+1 */
    @Query("SELECT r.product.id, AVG(r.rating) FROM Review r WHERE r.product.id IN :productIds AND r.isApproved = true GROUP BY r.product.id")
    List<Object[]> avgRatingForProducts(@Param("productIds") List<Long> productIds);

    /** Batch: lấy reviewCount cho nhiều product cùng lúc — tránh N+1 */
    @Query("SELECT r.product.id, COUNT(r) FROM Review r WHERE r.product.id IN :productIds AND r.isApproved = true GROUP BY r.product.id")
    List<Object[]> countByProductIds(@Param("productIds") List<Long> productIds);
}
