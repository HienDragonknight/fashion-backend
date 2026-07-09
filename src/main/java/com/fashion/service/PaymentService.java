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
import vn.payos.core.ClientOptions;
import vn.payos.model.v2.paymentRequests.CreatePaymentLinkRequest;
import vn.payos.model.v2.paymentRequests.CreatePaymentLinkResponse;
import vn.payos.model.v2.paymentRequests.PaymentLinkItem;
import vn.payos.model.webhooks.WebhookData;

import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentService {

    private final OrderRepository orderRepository;
    private final ObjectMapper objectMapper;

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

    @Value("${stripe.secret-key}")
    private String stripeSecretKey;

    @Value("${stripe.webhook-secret}")
    private String stripeWebhookSecret;

    private PayOS payOSClient() {
        return new PayOS(
                ClientOptions.builder()
                        .clientId(payosClientId)
                        .apiKey(payosApiKey)
                        .checksumKey(payosChecksumKey)
                        .build()
        );
    }

    @Transactional
    public String createPayOSLink(Long orderId) throws Exception {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found: " + orderId));

        PayOS payOS = payOSClient();

        long amount = order.getTotal().longValue();
        long orderCode = order.getId();

        List<PaymentLinkItem> items = order.getItems().stream()
                .map(item -> PaymentLinkItem.builder()
                        .name(item.getProductName().substring(0, Math.min(item.getProductName().length(), 50)))
                        .quantity(item.getQuantity())
                        .price(item.getPrice().longValue())
                        .build())
                .toList();

        CreatePaymentLinkRequest paymentData = CreatePaymentLinkRequest.builder()
                .orderCode(orderCode)
                .amount(amount)
                .description("DH" + orderId)
                .items(items)
                .returnUrl(payosReturnUrl + "?orderId=" + orderId + "&method=PAYOS")
                .cancelUrl(payosCancelUrl + "?orderId=" + orderId)
                .build();

        CreatePaymentLinkResponse response = payOS.paymentRequests().create(paymentData);

        order.setPaymentRef(String.valueOf(orderCode));
        orderRepository.save(order);

        return response.getCheckoutUrl();
    }

    @Transactional
    public void handlePayOSWebhook(Map<String, Object> body) {
        try {
            PayOS payOS = payOSClient();
            String webhookJson = objectMapper.writeValueAsString(body);
            WebhookData webhookData = payOS.webhooks().verify(webhookJson);

            if (webhookData != null && webhookData.getOrderCode() != null) {
                long orderCode = webhookData.getOrderCode();
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

    @Transactional
    public Map<String, String> createStripeIntent(Long orderId) throws Exception {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found: " + orderId));

        Stripe.apiKey = stripeSecretKey;

        long amountVnd = order.getTotal().longValue();
        long amountUsd = Math.max(50, amountVnd / 25000);

        PaymentIntentCreateParams params = PaymentIntentCreateParams.builder()
                .setAmount(amountUsd * 100L)
                .setCurrency("usd")
                .addPaymentMethodType("card")
                .putMetadata("orderId", orderId.toString())
                .putMetadata("amountVnd", String.valueOf(amountVnd))
                .setDescription("YODY Order #" + orderId)
                .build();

        PaymentIntent intent = PaymentIntent.create(params);

        order.setPaymentRef(intent.getId());
        orderRepository.save(order);

        return Map.of(
                "clientSecret", intent.getClientSecret(),
                "paymentIntentId", intent.getId(),
                "amountUsd", String.valueOf(amountUsd),
                "amountVnd", String.valueOf(amountVnd)
        );
    }

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
