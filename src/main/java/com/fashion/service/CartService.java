package com.fashion.service;

import com.fashion.dto.response.CartItemResponse;
import com.fashion.entity.*;
import com.fashion.exception.BusinessException;
import com.fashion.exception.ResourceNotFoundException;
import com.fashion.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CartService {

    private final CartItemRepository cartItemRepository;
    private final UserRepository userRepository;
    private final ProductVariantRepository variantRepository;

    @Transactional(readOnly = true)
    public List<CartItemResponse> getCart(Long userId) {
        return cartItemRepository.findByUserId(userId).stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public CartItemResponse addToCart(Long userId, Long variantId, int quantity) {
        ProductVariant variant = variantRepository.findById(variantId)
                .orElseThrow(() -> new ResourceNotFoundException("Biến thể sản phẩm", variantId));

        if (variant.getStockQty() < quantity) {
            throw new BusinessException("Số lượng trong kho không đủ");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Người dùng", userId));

        CartItem existing = cartItemRepository.findByUserIdAndVariantId(userId, variantId)
                .orElse(null);

        if (existing != null) {
            int newQty = existing.getQuantity() + quantity;
            if (newQty > variant.getStockQty()) {
                throw new BusinessException("Vượt quá số lượng trong kho");
            }
            existing.setQuantity(newQty);
            return toResponse(cartItemRepository.save(existing));
        }

        CartItem cartItem = CartItem.builder()
                .user(user)
                .variant(variant)
                .quantity(quantity)
                .build();
        return toResponse(cartItemRepository.save(cartItem));
    }

    @Transactional
    public CartItemResponse updateQuantity(Long userId, Long cartItemId, int quantity) {
        CartItem item = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new ResourceNotFoundException("Giỏ hàng item", cartItemId));

        if (!item.getUser().getId().equals(userId)) {
            throw new BusinessException("Không có quyền thao tác");
        }

        if (quantity <= 0) {
            cartItemRepository.delete(item);
            return null;
        }

        if (quantity > item.getVariant().getStockQty()) {
            throw new BusinessException("Vượt quá số lượng trong kho");
        }

        item.setQuantity(quantity);
        return toResponse(cartItemRepository.save(item));
    }

    @Transactional
    public void removeFromCart(Long userId, Long cartItemId) {
        CartItem item = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new ResourceNotFoundException("Giỏ hàng item", cartItemId));
        if (!item.getUser().getId().equals(userId)) {
            throw new BusinessException("Không có quyền thao tác");
        }
        cartItemRepository.delete(item);
    }

    @Transactional
    public void clearCart(Long userId) {
        cartItemRepository.deleteByUserId(userId);
    }

    private CartItemResponse toResponse(CartItem item) {
        ProductVariant variant = item.getVariant();
        Product product = variant.getProduct();
        BigDecimal price = product.getEffectivePrice().add(variant.getPriceAdjustment());

        return CartItemResponse.builder()
                .id(item.getId())
                .variantId(variant.getId())
                .sku(variant.getSku())
                .size(variant.getSize())
                .color(variant.getColor())
                .colorHex(variant.getColorHex())
                .productId(product.getId())
                .productName(product.getName())
                .productSlug(product.getSlug())
                .thumbnailUrl(product.getThumbnailUrl())
                .price(price)
                .quantity(item.getQuantity())
                .subtotal(price.multiply(BigDecimal.valueOf(item.getQuantity())))
                .stockQty(variant.getStockQty())
                .build();
    }
}
