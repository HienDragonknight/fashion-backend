package com.fashion.service;

import com.fashion.dto.request.TryOnRequest;
import com.fashion.dto.request.TryOnResultRequest;
import com.fashion.dto.response.TryOnResponse;
import com.fashion.entity.TryOnHistory;
import com.fashion.entity.User;
import com.fashion.exception.ResourceNotFoundException;
import com.fashion.repository.TryOnHistoryRepository;
import com.fashion.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class TryOnService {

    private final TryOnHistoryRepository tryOnHistoryRepository;
    private final UserRepository userRepository;

    /**
     * Saves a PENDING try-on request record when user initiates the try-on.
     */
    @Transactional
    public TryOnResponse createRequest(Long userId, TryOnRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Người dùng", userId));

        TryOnHistory history = TryOnHistory.builder()
                .user(user)
                .productId(request.getProductId())
                .productName(request.getProductName())
                .originalImageUrl(request.getOriginalImageUrl())
                .status("PENDING")
                .build();

        TryOnHistory saved = tryOnHistoryRepository.save(history);
        log.info("TryOn PENDING created: id={} user={} product={}", saved.getId(), userId, request.getProductId());
        return toResponse(saved);
    }

    /**
     * Updates a history record with the generated image URL (COMPLETED or FAILED).
     * Only the owning user can update their own record.
     */
    @Transactional
    public TryOnResponse updateResult(Long userId, Long historyId, TryOnResultRequest request) {
        TryOnHistory history = tryOnHistoryRepository.findByIdAndUserId(historyId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Try-on history", historyId));

        String status = request.getStatus() != null ? request.getStatus() : "COMPLETED";
        history.setStatus(status);
        history.setGeneratedImageUrl(request.getGeneratedImageUrl());
        history.setErrorMessage(request.getErrorMessage());

        TryOnHistory saved = tryOnHistoryRepository.save(history);
        log.info("TryOn {} updated: id={} user={}", status, historyId, userId);
        return toResponse(saved);
    }

    /**
     * Returns paginated try-on history for the authenticated user.
     */
    @Transactional(readOnly = true)
    public Page<TryOnResponse> getHistory(Long userId, int page, int size) {
        return tryOnHistoryRepository
                .findByUserIdOrderByCreatedAtDesc(userId, PageRequest.of(page, size))
                .map(this::toResponse);
    }

    /**
     * Deletes a history record — user can only delete their own records.
     */
    @Transactional
    public void deleteHistory(Long userId, Long historyId) {
        TryOnHistory history = tryOnHistoryRepository.findByIdAndUserId(historyId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Try-on history", historyId));
        tryOnHistoryRepository.delete(history);
        log.info("TryOn history deleted: id={} user={}", historyId, userId);
    }

    // ── Mapping ──────────────────────────────────────────────────────────────

    private TryOnResponse toResponse(TryOnHistory h) {
        return TryOnResponse.builder()
                .id(h.getId())
                .userId(h.getUser().getId())
                .productId(h.getProductId())
                .productName(h.getProductName())
                .originalImageUrl(h.getOriginalImageUrl())
                .generatedImageUrl(h.getGeneratedImageUrl())
                .status(h.getStatus())
                .errorMessage(h.getErrorMessage())
                .createdAt(h.getCreatedAt())
                .build();
    }
}
