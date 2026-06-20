package com.fashion.controller;

import com.fashion.dto.response.ApiResponse;
import com.fashion.dto.response.CartItemResponse;
import com.fashion.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import com.fashion.repository.UserRepository;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;
    private final UserRepository userRepository;

    private Long getUserId(UserDetails userDetails) {
        return userRepository.findByEmailOrPhone(userDetails.getUsername())
                .orElseThrow().getId();
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<CartItemResponse>>> getCart(
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(ApiResponse.success(cartService.getCart(getUserId(userDetails))));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<CartItemResponse>> addToCart(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody Map<String, Object> body) {
        Long variantId = Long.parseLong(body.get("variantId").toString());
        int quantity = Integer.parseInt(body.getOrDefault("quantity", 1).toString());
        return ResponseEntity.ok(ApiResponse.success(
                cartService.addToCart(getUserId(userDetails), variantId, quantity)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<CartItemResponse>> updateQuantity(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long id,
            @RequestBody Map<String, Integer> body) {
        return ResponseEntity.ok(ApiResponse.success(
                cartService.updateQuantity(getUserId(userDetails), id, body.get("quantity"))));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> remove(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long id) {
        cartService.removeFromCart(getUserId(userDetails), id);
        return ResponseEntity.ok(ApiResponse.success("Đã xóa khỏi giỏ hàng", null));
    }

    @DeleteMapping
    public ResponseEntity<ApiResponse<Void>> clearCart(
            @AuthenticationPrincipal UserDetails userDetails) {
        cartService.clearCart(getUserId(userDetails));
        return ResponseEntity.ok(ApiResponse.success("Đã xóa giỏ hàng", null));
    }
}
