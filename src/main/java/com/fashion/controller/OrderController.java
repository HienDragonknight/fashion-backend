package com.fashion.controller;

import com.fashion.dto.request.CreateOrderRequest;
import com.fashion.dto.response.ApiResponse;
import com.fashion.dto.response.OrderResponse;
import com.fashion.repository.UserRepository;
import com.fashion.service.OrderService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Map;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;
    private final UserRepository userRepository;

    private Long getUserId(UserDetails userDetails) {
        return userRepository.findByEmailOrPhone(userDetails.getUsername())
                .orElseThrow().getId();
    }

    @PostMapping
    public ResponseEntity<ApiResponse<OrderResponse>> createOrder(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody CreateOrderRequest request,
            HttpServletRequest httpRequest) {

        BigDecimal shippingFee = new BigDecimal(
                httpRequest.getHeader("X-Shipping-Fee") != null
                        ? httpRequest.getHeader("X-Shipping-Fee") : "0");

        return ResponseEntity.ok(ApiResponse.success("Đặt hàng thành công",
                orderService.createOrder(getUserId(userDetails), request, shippingFee)));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Page<OrderResponse>>> getUserOrders(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(ApiResponse.success(
                orderService.getUserOrders(getUserId(userDetails), page, size)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<OrderResponse>> getOrderDetail(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(
                orderService.getOrderDetail(getUserId(userDetails), id)));
    }

    @PutMapping("/{id}/cancel")
    public ResponseEntity<ApiResponse<OrderResponse>> cancelOrder(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success("Đã hủy đơn hàng",
                orderService.cancelOrder(getUserId(userDetails), id)));
    }
}
