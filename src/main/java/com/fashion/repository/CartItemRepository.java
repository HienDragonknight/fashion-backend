package com.fashion.repository;

import com.fashion.entity.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {

    List<CartItem> findByUserId(Long userId);

    Optional<CartItem> findByUserIdAndVariantId(Long userId, Long variantId);

    @Modifying
    @Query("DELETE FROM CartItem c WHERE c.user.id = :userId")
    void deleteByUserId(@Param("userId") Long userId);

    @Modifying
    @Query("DELETE FROM CartItem c WHERE c.user.id = :userId AND c.variant.id IN :variantIds")
    void deleteByUserIdAndVariantIds(@Param("userId") Long userId,
                                     @Param("variantIds") List<Long> variantIds);
}
