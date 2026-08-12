package com.fashion.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.fashion.dto.request.TryOnRequest;
import com.fashion.dto.request.TryOnResultRequest;
import com.fashion.dto.response.TryOnGenerateResponse;
import com.fashion.dto.response.TryOnResponse;
import com.fashion.entity.TryOnHistory;
import com.fashion.entity.User;
import com.fashion.exception.BadRequestException;
import com.fashion.exception.ResourceNotFoundException;
import com.fashion.repository.TryOnHistoryRepository;
import com.fashion.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class TryOnService {

    private static final long MAX_PERSON_IMAGE_BYTES = 10L * 1024 * 1024;

    private final TryOnHistoryRepository tryOnHistoryRepository;
    private final UserRepository userRepository;

    private final OpenAiImageEditClient openAiImageEditClient;
    private final ReplicateTryOnClient replicateTryOnClient;
    private final OotdiffusionClient ootdiffusionClient;
    private final Cloudinary cloudinary;

    /**
     * AI generation handler for virtual try-on.
     */
    public TryOnGenerateResponse generate(
            MultipartFile personImage,
            MultipartFile garmentImage,
            String garmentImageUrl,
            String productName,
            String category,
            String modelType
    ) {
        validateImageFile(personImage, "Thiếu ảnh của bạn");

        String effectiveGarmentUrl = garmentImageUrl;
        if (garmentImage != null && !garmentImage.isEmpty()) {
            validateImageFile(garmentImage, "File ảnh trang phục không hợp lệ");
            try {
                log.info("Uploading user garment image to Cloudinary...");
                Map<?, ?> uploadResult = cloudinary.uploader().upload(garmentImage.getBytes(), ObjectUtils.asMap(
                        "folder", "fashion/tryon_garments",
                        "resource_type", "image"
                ));
                effectiveGarmentUrl = (String) uploadResult.get("secure_url");
            } catch (Exception e) {
                log.warn("Failed to upload garment image to Cloudinary ({}), using Base64 Data URI fallback...", e.getMessage());
                effectiveGarmentUrl = convertToDataUri(garmentImage);
            }
        }

        if (!StringUtils.hasText(effectiveGarmentUrl)) {
            throw new BadRequestException("Thiếu ảnh trang phục / sản phẩm. Vui lòng chọn hoặc tải ảnh trang phục lên.");
        }

        String resultUrl;
        String effectiveModel = (modelType != null) ? modelType.toUpperCase() : "IDM_VTON";
        log.info("Using AI model: {}", effectiveModel);

        switch (effectiveModel) {
            case "OOTDIFFUSION" -> {
                if (!ootdiffusionClient.isConfigured()) {
                    throw new RuntimeException("Replicate API token chưa được cấu hình.");
                }
                resultUrl = ootdiffusionClient.generateTryOnImage(personImage, effectiveGarmentUrl);
            }
            case "STABLEVITON" -> {
                if (!replicateTryOnClient.isConfigured()) {
                    throw new RuntimeException("Replicate API token chưa được cấu hình.");
                }
                resultUrl = replicateTryOnClient.generateTryOnImage(personImage, effectiveGarmentUrl, productName, category);
            }
            default -> {
                if (replicateTryOnClient.isConfigured()) {
                    resultUrl = replicateTryOnClient.generateTryOnImage(personImage, effectiveGarmentUrl, productName, category);
                } else {
                    resultUrl = openAiImageEditClient.generateTryOnImage(personImage, effectiveGarmentUrl, productName);
                }
            }
        }

        return TryOnGenerateResponse.builder()
                .resultUrl(resultUrl)
                .build();
    }

    private void validateImageFile(MultipartFile image, String missingMsg) {
        if (image == null || image.isEmpty()) {
            throw new BadRequestException(missingMsg);
        }
        String contentType = image.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new BadRequestException("Vui lòng chọn file ảnh hợp lệ (JPG, PNG, WEBP)");
        }
        if (image.getSize() > MAX_PERSON_IMAGE_BYTES) {
            throw new BadRequestException("Ảnh tối đa 10MB");
        }
    }

    private String convertToDataUri(MultipartFile file) {
        try {
            String mimeType = file.getContentType();
            if (!StringUtils.hasText(mimeType)) {
                mimeType = "image/jpeg";
            }
            String base64 = java.util.Base64.getEncoder().encodeToString(file.getBytes());
            return "data:" + mimeType + ";base64," + base64;
        } catch (java.io.IOException e) {
            throw new BadRequestException("Không thể xử lý file ảnh tải lên.");
        }
    }

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
