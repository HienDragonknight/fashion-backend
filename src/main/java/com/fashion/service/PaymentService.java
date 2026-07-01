package com.fashion.service;

import com.fashion.entity.Order;
import com.fashion.repository.OrderRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.stripe.Stripe;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentCreateParams;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.payos.PayOS;
import vn.payos.type.CheckoutResponseData;
import vn.payos.type.ItemData;
import vn.payos.type.PaymentData;
import vn.payos.type.Webhook;

import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentService {

    private final OrderRepository orderRepository;

    // ── PayOS config ──────────────────────────────────────────────────────────
    @Value("${payos.client-id}")
    private String payosClientId;

    @Value("${payos.api-key}")
    private String payosApiKey;

    @Value("${payos.checksum-key}")
    private String payosChecksumKey;

    @Value("${payos.return-url}")
    private String payosReturnUrl;

    @Value("${payos.cancel-url}")
    private String payosCancelUrl;

    // ── Stripe config ─────────────────────────────────────────────────────────
    @Value("${stripe.secret-key}")
    private String stripeSecretKey;

    @Value("${stripe.webhook-secret}")
    private String stripeWebhookSecret;

    // ─────────────────────────────────────────────────────────────────────────
    // PayOS
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Creates a PayOS payment link for the given order.
     * Returns the checkoutUrl to redirect the customer to.
     */
    @Transactional
    public String createPayOSLink(Long orderId) throws Exception {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found: " + orderId));

        PayOS payOS = new PayOS(payosClientId, payosApiKey, payosChecksumKey);

        // PayOS requires amount in VND integer (no decimal)
        long amount = order.getTotal().longValue();

        // Use orderId as orderCode (must be unique positive int)
        long orderCode = order.getId();

        List<ItemData> items = order.getItems().stream()
                .map(item -> ItemData.builder()
                        .name(item.getProductName().substring(0, Math.min(item.getProductName().length(), 50)))
                        .quantity(item.getQuantity())
                        .price((int) item.getPrice().longValue())
                        .build())
                .toList();

        PaymentData paymentData = PaymentData.builder()
                .orderCode(orderCode)
                .amount((int) amount)
                .description("DH" + orderId) // max 25 chars
                .items(items)
                .returnUrl(payosReturnUrl + "?orderId=" + orderId + "&method=PAYOS")
                .cancelUrl(payosCancelUrl + "?orderId=" + orderId)
                .build();

        CheckoutResponseData response = payOS.createPaymentLink(paymentData);

        // Save paymentRef
        order.setPaymentRef(String.valueOf(orderCode));
        orderRepository.save(order);

        return response.getCheckoutUrl();
    }

    /**
     * Handles PayOS webhook. Verifies signature, marks order PAID.
     */
    @Transactional
    public void handlePayOSWebhook(Map<String, Object> body) {
        try {
            PayOS payOS = new PayOS(payosClientId, payosApiKey, payosChecksumKey);
            // Convert raw map to PayOS Webhook type
            ObjectMapper mapper = new ObjectMapper();
            Webhook webhookBody = mapper.convertValue(body, Webhook.class);
            // Verify webhook signature
            payOS.verifyPaymentWebhookData(webhookBody);

            String code = webhookBody.getCode() != null ? webhookBody.getCode() : "99";

            if ("00".equals(code) && webhookBody.getData() != null) {
                long orderCode = webhookBody.getData().getOrderCode();
                orderRepository.findById(orderCode).ifPresent(order -> {
                    order.setPaymentStatus("PAID");
                    order.setStatus("PROCESSING");
                    orderRepository.save(order);
                    log.info("PayOS: Order {} marked as PAID", orderCode);
                });
            }
        } catch (Exception e) {
            log.error("PayOS webhook error: {}", e.getMessage());
            throw new RuntimeException("Invalid PayOS webhook: " + e.getMessage());
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Stripe
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Creates a Stripe PaymentIntent for the given order.
     * Returns clientSecret + paymentIntentId to the frontend.
     */
    @Transactional
    public Map<String, String> createStripeIntent(Long orderId) throws Exception {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found: " + orderId));

        Stripe.apiKey = stripeSecretKey;

        // Stripe amount is in smallest currency unit — VND has no subunit so amount is same
        // But Stripe does NOT support VND directly, so we convert to USD (approximate)
        // 1 USD ≈ 25,000 VND  — for production use a live rate
        long amountVnd = order.getTotal().longValue();
        long amountUsd = Math.max(50, amountVnd / 25000); // min $0.50 per Stripe rules

        PaymentIntentCreateParams params = PaymentIntentCreateParams.builder()
                .setAmount(amountUsd * 100L) // Stripe uses cents
                .setCurrency("usd")
                .addPaymentMethodType("card")
                .putMetadata("orderId", orderId.toString())
                .putMetadata("amountVnd", String.valueOf(amountVnd))
                .setDescription("YODY Order #" + orderId)
                .build();

        PaymentIntent intent = PaymentIntent.create(params);

        // Save paymentRef
        order.setPaymentRef(intent.getId());
        orderRepository.save(order);

        return Map.of(
                "clientSecret", intent.getClientSecret(),
                "paymentIntentId", intent.getId(),
                "amountUsd", String.valueOf(amountUsd),
                "amountVnd", String.valueOf(amountVnd)
        );
    }

    /**
     * Handles Stripe webhook. Verifies signature, marks order PAID.
     */
    @Transactional
    public void handleStripeWebhook(String payload, String sigHeader) {
        try {
            Stripe.apiKey = stripeSecretKey;
            com.stripe.model.Event event = com.stripe.net.Webhook.constructEvent(
                    payload, sigHeader, stripeWebhookSecret);

            if ("payment_intent.succeeded".equals(event.getType())) {
                com.stripe.model.StripeObject stripeObject = event.getDataObjectDeserializer()
                        .getObject().orElse(null);
                if (stripeObject instanceof PaymentIntent pi) {
                    String orderId = pi.getMetadata().get("orderId");
                    if (orderId != null) {
                        orderRepository.findById(Long.parseLong(orderId)).ifPresent(order -> {
                            order.setPaymentStatus("PAID");
                            order.setStatus("PROCESSING");
                            orderRepository.save(order);
                            log.info("Stripe: Order {} marked as PAID", orderId);
                        });
                    }
                }
            }
        } catch (Exception e) {
            log.error("Stripe webhook error: {}", e.getMessage());
            throw new RuntimeException("Invalid Stripe webhook: " + e.getMessage());
        }
    }
}
