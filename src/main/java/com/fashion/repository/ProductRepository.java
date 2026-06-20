package com.fashion.repository;

import com.fashion.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long>,
        JpaSpecificationExecutor<Product> {

    Optional<Product> findBySlugAndIsActiveTrue(String slug);

    Page<Product> findByIsActiveTrue(Pageable pageable);

    @Query("""
        SELECT p FROM Product p
        WHERE p.isActive = true
        AND (LOWER(p.name) LIKE LOWER(CONCAT('%', :q, '%'))
             OR LOWER(p.description) LIKE LOWER(CONCAT('%', :q, '%')))
        """)
    Page<Product> search(@Param("q") String query, Pageable pageable);

    @Modifying
    @Query("UPDATE Product p SET p.viewCount = p.viewCount + 1 WHERE p.id = :id")
    void incrementViewCount(@Param("id") Long id);

    /** Top products by soldCount */
    @Query("SELECT p FROM Product p ORDER BY p.soldCount DESC")
    List<Product> findTopBySoldCount(Pageable pageable);

    /** Products sold count and revenue from order_items */
    @Query("""
        SELECT oi.product.id, oi.product.name, oi.product.thumbnailUrl,
               oi.product.category.name,
               SUM(oi.quantity) as totalSold,
               SUM(oi.price * oi.quantity) as totalRevenue
        FROM OrderItem oi
        WHERE oi.order.status != 'CANCELLED'
        GROUP BY oi.product.id, oi.product.name, oi.product.thumbnailUrl, oi.product.category.name
        ORDER BY SUM(oi.quantity) DESC
        """)
    List<Object[]> topProductsBySales(Pageable pageable);

    /** Revenue grouped by category */
    @Query("""
        SELECT oi.product.category.name,
               SUM(oi.price * oi.quantity) as revenue
        FROM OrderItem oi
        WHERE oi.order.status != 'CANCELLED'
          AND oi.product.category IS NOT NULL
        GROUP BY oi.product.category.name
        ORDER BY SUM(oi.price * oi.quantity) DESC
        """)
    List<Object[]> revenueByCategory();
}
