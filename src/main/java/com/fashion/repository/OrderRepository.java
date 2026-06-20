package com.fashion.repository;

import com.fashion.entity.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long>,
        JpaSpecificationExecutor<Order> {

    Page<Order> findByUserId(Long userId, Pageable pageable);

    Page<Order> findByUserIdOrderByCreatedAtDesc(Long userId, Pageable pageable);

    long countByStatus(String status);

    /** Tổng doanh thu từ các đơn DELIVERED */
    @Query("SELECT COALESCE(SUM(o.total), 0) FROM Order o WHERE o.status = 'DELIVERED'")
    BigDecimal sumTotalRevenue();

    /** Doanh thu theo tháng trong 1 năm (chỉ đơn DELIVERED) */
    @Query("""
        SELECT MONTH(o.createdAt) as month,
               COALESCE(SUM(o.total), 0) as revenue,
               COUNT(o) as orderCount
        FROM Order o
        WHERE o.status = 'DELIVERED'
          AND YEAR(o.createdAt) = :year
        GROUP BY MONTH(o.createdAt)
        ORDER BY MONTH(o.createdAt)
        """)
    List<Object[]> revenueByMonth(@Param("year") int year);

    /** Top customers by total spending */
    @Query("""
        SELECT o.user.id, o.user.fullName, o.user.email,
               COUNT(o) as orderCount,
               SUM(o.total) as totalSpent
        FROM Order o
        WHERE o.status != 'CANCELLED'
        GROUP BY o.user.id, o.user.fullName, o.user.email
        ORDER BY SUM(o.total) DESC
        """)
    List<Object[]> topCustomersBySpending(Pageable pageable);

    /** Orders by status count grouped */
    @Query("SELECT o.status, COUNT(o) FROM Order o GROUP BY o.status")
    List<Object[]> countByStatusGroup();

    /** Count orders for a specific user */
    long countByUserId(Long userId);

    /** Total spent by a user */
    @Query("SELECT COALESCE(SUM(o.total), 0) FROM Order o WHERE o.user.id = :userId AND o.status != 'CANCELLED'")
    BigDecimal sumTotalByUserId(@Param("userId") Long userId);
}
