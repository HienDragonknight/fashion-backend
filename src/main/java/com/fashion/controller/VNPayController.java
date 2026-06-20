package com.fashion.controller;

import com.fashion.dto.response.ApiResponse;
import com.fashion.entity.Order;
import com.fashion.exception.ResourceNotFoundException;
import com.fashion.repository.OrderRepository;
import com.fashion.service.VNPayService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/payment/vnpay")
@RequiredArgsConstructor
public class VNPayController {

    private final VNPayService vnPayService;
    private final OrderRepository orderRepository;

    @PostMapping("/create")
    public ResponseEntity<ApiResponse<Map<String, String>>> createPayment(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody Map<String, Object> body,
            HttpServletRequest request) {
        Long orderId = Long.parseLong(body.get("orderId").toString());
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Đơn hàng", orderId));
        String paymentUrl = vnPayService.createPaymentUrl(
                orderId, order.getTotal().longValue(),
                "Thanh toan don hang #" + orderId, request.getRemoteAddr());
        return ResponseEntity.ok(ApiResponse.success(Map.of("paymentUrl", paymentUrl)));
    }

    @GetMapping("/callback")
    public ResponseEntity<ApiResponse<Void>> callback(@RequestParam Map<String, String> params) {
        boolean valid = vnPayService.verifyCallback(new HashMap<>(params));
        if (valid) {
            Long orderId = Long.parseLong(params.get("vnp_TxnRef").split("_")[0]);
            orderRepository.findById(orderId).ifPresent(order -> {
                order.setPaymentStatus("PAID");
                order.setStatus("CONFIRMED");
                orderRepository.save(order);
            });
            return ResponseEntity.ok(ApiResponse.success("Thanh toán thành công", null));
        }
        return ResponseEntity.badRequest().body(ApiResponse.error("Thanh toán thất bại"));
    }
}
