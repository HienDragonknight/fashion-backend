package com.fashion.service;

import com.fashion.dto.request.CreateOrderRequest;
import com.fashion.dto.response.OrderResponse;
import com.fashion.entity.*;
import com.fashion.exception.BusinessException;
import com.fashion.exception.ResourceNotFoundException;
import com.fashion.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final AddressRepository addressRepository;
    private final ProductVariantRepository variantRepository;
    private final CartItemRepository cartItemRepository;

    @Transactional
    public OrderResponse createOrder(Long userId, CreateOrderRequest request, BigDecimal shippingFee) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Người dùng", userId));

        Address address = addressRepository.findByIdAndUserId(request.getAddressId(), userId)
                .orElseThrow(() -> new ResourceNotFoundException("Địa chỉ không tìm thấy"));

        BigDecimal subtotal = BigDecimal.ZERO;
        List<OrderItem> items = new java.util.ArrayList<>();

        for (CreateOrderRequest.OrderItemRequest itemReq : request.getItems()) {
            ProductVariant variant = variantRepository.findById(itemReq.getVariantId())
                    .orElseThrow(() -> new ResourceNotFoundException("Biến thể", itemReq.getVariantId()));

            if (variant.getStockQty() < itemReq.getQuantity()) {
                throw new BusinessException("Sản phẩm " + variant.getProduct().getName() + " không đủ hàng");
            }

            Product product = variant.getProduct();
            BigDecimal price = product.getEffectivePrice().add(variant.getPriceAdjustment());
            subtotal = subtotal.add(price.multiply(BigDecimal.valueOf(itemReq.getQuantity())));

            items.add(OrderItem.builder()
                    .variant(variant)
                    .product(product)
                    .productName(product.getName())
                    .variantLabel(variant.getLabel())
                    .thumbnailUrl(product.getThumbnailUrl())
                    .price(price)
                    .quantity(itemReq.getQuantity())
                    .build());

            variant.setStockQty(variant.getStockQty() - itemReq.getQuantity());
            variantRepository.save(variant);
        }

        BigDecimal total = subtotal.add(shippingFee);
        String snapshotAddress = String.format("%s, %s, %s, %s, %s — %s",
                address.getDetail(), address.getWard(), address.getDistrict(),
                address.getProvince(), address.getFullName(), address.getPhone());

        Order order = Order.builder()
                .user(user)
                .address(address)
                .status("PENDING")
                .paymentMethod(request.getPaymentMethod())
                .paymentStatus("UNPAID")
                .shippingFee(shippingFee)
                .subtotal(subtotal)
                .total(total)
                .note(request.getNote())
                .snapshotAddress(snapshotAddress)
                .build();

        items.forEach(item -> item.setOrder(order));
        order.getItems().addAll(items);

        Order saved = orderRepository.save(order);

        // Remove from cart
        List<Long> variantIds = request.getItems().stream()
                .map(CreateOrderRequest.OrderItemRequest::getVariantId).toList();
        cartItemRepository.deleteByUserIdAndVariantIds(userId, variantIds);

        return toResponse(saved);
    }

    @Transactional(readOnly = true)
    public Page<OrderResponse> getUserOrders(Long userId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return orderRepository.findByUserIdOrderByCreatedAtDesc(userId, pageable).map(this::toResponse);
    }

    @Transactional(readOnly = true)
    public OrderResponse getOrderDetail(Long userId, Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Đơn hàng", orderId));
        if (!order.getUser().getId().equals(userId)) {
            throw new BusinessException("Không có quyền xem đơn hàng này");
        }
        return toResponse(order);
    }

    @Transactional
    public OrderResponse cancelOrder(Long userId, Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Đơn hàng", orderId));
        if (!order.getUser().getId().equals(userId)) {
            throw new BusinessException("Không có quyền hủy đơn hàng này");
        }
        if (!order.getStatus().equals("PENDING")) {
            throw new BusinessException("Chỉ có thể hủy đơn hàng đang chờ xử lý");
        }

        // Restore stock
        order.getItems().forEach(item -> {
            if (item.getVariant() != null) {
                item.getVariant().setStockQty(item.getVariant().getStockQty() + item.getQuantity());
                variantRepository.save(item.getVariant());
            }
        });

        order.setStatus("CANCELLED");
        return toResponse(orderRepository.save(order));
    }

    // Admin methods
    @Transactional(readOnly = true)
    public Page<OrderResponse> getAllOrders(String status, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        if (status != null && !status.isBlank()) {
            return orderRepository.findAll(
                    (root, q, cb) -> cb.equal(root.get("status"), status), pageable)
                    .map(this::toResponse);
        }
        return orderRepository.findAll(pageable).map(this::toResponse);
    }

    @Transactional(readOnly = true)
    public OrderResponse getOrderById(Long orderId) {
        return toResponse(orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Đơn hàng", orderId)));
    }

    @Transactional
    public OrderResponse updateStatus(Long orderId, String status) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Đơn hàng", orderId));
        order.setStatus(status);
        return toResponse(orderRepository.save(order));
    }

    @Transactional
    public OrderResponse updateStatusAndPayment(Long orderId, String status, String paymentStatus, String ghnCode) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Đơn hàng", orderId));
        if (status != null) order.setStatus(status);
        if (paymentStatus != null) order.setPaymentStatus(paymentStatus);
        if (ghnCode != null) order.setGhnOrderCode(ghnCode);
        return toResponse(orderRepository.save(order));
    }

    private OrderResponse toResponse(Order order) {
        return OrderResponse.builder()
                .id(order.getId())
                .userId(order.getUser() != null ? order.getUser().getId() : null)
                .status(order.getStatus())
                .paymentMethod(order.getPaymentMethod())
                .paymentStatus(order.getPaymentStatus())
                .shippingFee(order.getShippingFee())
                .subtotal(order.getSubtotal())
                .discount(order.getDiscount())
                .total(order.getTotal())
                .ghnOrderCode(order.getGhnOrderCode())
                .note(order.getNote())
                .snapshotAddress(order.getSnapshotAddress())
                .items(order.getItems().stream().map(i -> OrderResponse.OrderItemResponse.builder()
                        .id(i.getId())
                        .productId(i.getProduct() != null ? i.getProduct().getId() : null)
                        .productName(i.getProductName())
                        .variantLabel(i.getVariantLabel())
                        .thumbnailUrl(i.getThumbnailUrl())
                        .price(i.getPrice())
                        .quantity(i.getQuantity())
                        .subtotal(i.getPrice().multiply(BigDecimal.valueOf(i.getQuantity())))
                        .build()).toList())
                .createdAt(order.getCreatedAt())
                .updatedAt(order.getUpdatedAt())
                .build();
    }
}
