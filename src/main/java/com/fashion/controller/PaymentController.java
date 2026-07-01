package com.fashion.controller;

import com.fashion.dto.response.ApiResponse;
import com.fashion.service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    // ── PayOS ─────────────────────────────────────────────────────────────────

    /**
     * Create a PayOS payment link for an existing order.
     * POST /api/payments/payos/create
     * Body: { "orderId": 123 }
     */
    @PostMapping("/payos/create")
    public ResponseEntity<ApiResponse<Map<String, String>>> createPayOSLink(
            @RequestBody Map<String, Long> body) {
        try {
            Long orderId = body.get("orderId");
            String checkoutUrl = paymentService.createPayOSLink(orderId);
            return ResponseEntity.ok(ApiResponse.success(Map.of("checkoutUrl", checkoutUrl)));
        } catch (Exception e) {
            log.error("PayOS create error: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Tạo link PayOS thất bại: " + e.getMessage()));
        }
    }

    /**
     * Receive PayOS webhook (no auth required — verified by signature inside service).
     * POST /api/payments/payos/webhook
     */
    @PostMapping("/payos/webhook")
    public ResponseEntity<Map<String, String>> payosWebhook(
            @RequestBody Map<String, Object> body) {
        try {
            paymentService.handlePayOSWebhook(body);
            return ResponseEntity.ok(Map.of("code", "00", "desc", "success"));
        } catch (Exception e) {
            log.error("PayOS webhook handling error: {}", e.getMessage());
            return ResponseEntity.ok(Map.of("code", "99", "desc", e.getMessage()));
        }
    }

    // ── Stripe ────────────────────────────────────────────────────────────────

    /**
     * Create a Stripe PaymentIntent for an existing order.
     * POST /api/payments/stripe/create-intent
     * Body: { "orderId": 123 }
     */
    @PostMapping("/stripe/create-intent")
    public ResponseEntity<ApiResponse<Map<String, String>>> createStripeIntent(
            @RequestBody Map<String, Long> body) {
        try {
            Long orderId = body.get("orderId");
            Map<String, String> result = paymentService.createStripeIntent(orderId);
            return ResponseEntity.ok(ApiResponse.success(result));
        } catch (Exception e) {
            log.error("Stripe intent error: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Tạo Stripe PaymentIntent thất bại: " + e.getMessage()));
        }
    }

    /**
     * Receive Stripe webhook (raw body needed for signature verification).
     * POST /api/payments/stripe/webhook
     */
    @PostMapping("/stripe/webhook")
    public ResponseEntity<Map<String, String>> stripeWebhook(
            @RequestBody String payload,
            @RequestHeader("Stripe-Signature") String sigHeader) {
        try {
            paymentService.handleStripeWebhook(payload, sigHeader);
            return ResponseEntity.ok(Map.of("received", "true"));
        } catch (Exception e) {
            log.error("Stripe webhook error: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}
