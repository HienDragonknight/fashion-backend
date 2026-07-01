package com.fashion.controller;

import com.fashion.dto.request.TryOnRequest;
import com.fashion.dto.request.TryOnResultRequest;
import com.fashion.dto.response.ApiResponse;
import com.fashion.dto.response.TryOnResponse;
import com.fashion.repository.UserRepository;
import com.fashion.service.TryOnService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/try-on")
@RequiredArgsConstructor
public class TryOnController {

    private final TryOnService tryOnService;
    private final UserRepository userRepository;

    /**
     * POST /try-on/history
     * Creates a PENDING history record when user initiates try-on.
     */
    @PostMapping("/history")
    public ResponseEntity<ApiResponse<TryOnResponse>> createHistory(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody TryOnRequest request) {

        Long userId = resolveUserId(userDetails);
        TryOnResponse response = tryOnService.createRequest(userId, request);
        return ResponseEntity.ok(ApiResponse.success("Đã tạo yêu cầu thử đồ", response));
    }

    /**
     * PATCH /try-on/history/{id}
     * Updates a history record with the generated image URL (COMPLETED | FAILED).
     */
    @PatchMapping("/history/{id}")
    public ResponseEntity<ApiResponse<TryOnResponse>> updateResult(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long id,
            @Valid @RequestBody TryOnResultRequest request) {

        Long userId = resolveUserId(userDetails);
        TryOnResponse response = tryOnService.updateResult(userId, id, request);
        return ResponseEntity.ok(ApiResponse.success("Đã cập nhật kết quả thử đồ", response));
    }

    /**
     * GET /try-on/history
     * Returns paginated try-on history for the authenticated user.
     */
    @GetMapping("/history")
    public ResponseEntity<ApiResponse<Page<TryOnResponse>>> getHistory(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Long userId = resolveUserId(userDetails);
        Page<TryOnResponse> history = tryOnService.getHistory(userId, page, size);
        return ResponseEntity.ok(ApiResponse.success(history));
    }

    /**
     * DELETE /try-on/history/{id}
     * Deletes a try-on history record — users can only delete their own.
     */
    @DeleteMapping("/history/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteHistory(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long id) {

        Long userId = resolveUserId(userDetails);
        tryOnService.deleteHistory(userId, id);
        return ResponseEntity.ok(ApiResponse.success("Đã xóa lịch sử thử đồ", null));
    }

    // ── Helper ───────────────────────────────────────────────────────────────

    private Long resolveUserId(UserDetails userDetails) {
        String identifier = userDetails.getUsername(); // email or phone stored in JWT subject
        return userRepository.findByEmailOrPhone(identifier)
                .orElseThrow(() -> new com.fashion.exception.ResourceNotFoundException("Người dùng không tồn tại"))
                .getId();
    }
}
