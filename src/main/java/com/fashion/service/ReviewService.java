package com.fashion.service;

import com.fashion.dto.response.ReviewResponse;
import com.fashion.dto.request.ReviewRequest;
import com.fashion.entity.*;
import com.fashion.exception.BusinessException;
import com.fashion.exception.ResourceNotFoundException;
import com.fashion.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;

    public Page<ReviewResponse> getProductReviews(Long productId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return reviewRepository.findByProductIdAndIsApprovedTrue(productId, pageable)
                .map(this::toResponse);
    }

    @Transactional
    public ReviewResponse createReview(Long userId, ReviewRequest request) {
        if (reviewRepository.findByUserIdAndProductIdAndOrderId(
                userId, request.getProductId(), request.getOrderId()).isPresent()) {
            throw new BusinessException("Bạn đã đánh giá sản phẩm này rồi");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Người dùng", userId));
        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Sản phẩm", request.getProductId()));
        Order order = orderRepository.findById(request.getOrderId())
                .orElseThrow(() -> new ResourceNotFoundException("Đơn hàng", request.getOrderId()));

        if (!order.getUser().getId().equals(userId)) {
            throw new BusinessException("Bạn chưa mua sản phẩm này");
        }

        Review review = Review.builder()
                .user(user)
                .product(product)
                .order(order)
                .rating(request.getRating())
                .comment(request.getComment())
                .isApproved(false)
                .build();

        return toResponse(reviewRepository.save(review));
    }

    // Admin
    public Page<ReviewResponse> getPendingReviews(int page, int size) {
        return reviewRepository.findByIsApprovedFalse(PageRequest.of(page, size))
                .map(this::toResponse);
    }

    @Transactional
    public ReviewResponse approveReview(Long id) {
        Review review = reviewRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Review", id));
        review.setIsApproved(true);
        return toResponse(reviewRepository.save(review));
    }

    @Transactional
    public void deleteReview(Long id) {
        reviewRepository.deleteById(id);
    }

    private ReviewResponse toResponse(Review review) {
        return ReviewResponse.builder()
                .id(review.getId())
                .userId(review.getUser().getId())
                .userFullName(review.getUser().getFullName())
                .userAvatarUrl(review.getUser().getAvatarUrl())
                .productId(review.getProduct().getId())
                .rating(review.getRating())
                .comment(review.getComment())
                .images(review.getImages())
                .isApproved(review.getIsApproved())
                .createdAt(review.getCreatedAt())
                .build();
    }
}
