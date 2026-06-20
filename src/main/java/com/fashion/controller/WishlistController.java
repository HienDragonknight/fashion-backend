package com.fashion.controller;

import com.fashion.dto.response.ApiResponse;
import com.fashion.entity.Wishlist;
import com.fashion.exception.BusinessException;
import com.fashion.exception.ResourceNotFoundException;
import com.fashion.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/wishlist")
@RequiredArgsConstructor
public class WishlistController {

    private final WishlistRepository wishlistRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;

    private Long getUserId(UserDetails userDetails) {
        return userRepository.findByEmailOrPhone(userDetails.getUsername()).orElseThrow().getId();
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Object>> getWishlist(@AuthenticationPrincipal UserDetails userDetails) {
        Long userId = getUserId(userDetails);
        return ResponseEntity.ok(ApiResponse.success(wishlistRepository.findByUserId(userId)));
    }

    @PostMapping
    @Transactional
    public ResponseEntity<ApiResponse<Void>> addToWishlist(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody Map<String, Long> body) {
        Long userId = getUserId(userDetails);
        Long productId = body.get("productId");
        if (wishlistRepository.existsByUserIdAndProductId(userId, productId)) {
            throw new BusinessException("Sản phẩm đã có trong danh sách yêu thích");
        }
        var user = userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User", userId));
        var product = productRepository.findById(productId).orElseThrow(() -> new ResourceNotFoundException("Product", productId));
        wishlistRepository.save(Wishlist.builder().user(user).product(product).build());
        return ResponseEntity.ok(ApiResponse.success("Đã thêm vào yêu thích", null));
    }

    @DeleteMapping("/{productId}")
    @Transactional
    public ResponseEntity<ApiResponse<Void>> removeFromWishlist(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long productId) {
        Long userId = getUserId(userDetails);
        wishlistRepository.deleteByUserIdAndProductId(userId, productId);
        return ResponseEntity.ok(ApiResponse.success("Đã xóa khỏi yêu thích", null));
    }

    @GetMapping("/ids")
    public ResponseEntity<ApiResponse<Object>> getWishlistIds(@AuthenticationPrincipal UserDetails userDetails) {
        Long userId = getUserId(userDetails);
        return ResponseEntity.ok(ApiResponse.success(wishlistRepository.findProductIdsByUserId(userId)));
    }
}
