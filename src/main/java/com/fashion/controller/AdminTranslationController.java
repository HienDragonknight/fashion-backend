package com.fashion.controller;

import com.fashion.dto.request.TranslationRequest;
import com.fashion.dto.response.ApiResponse;
import com.fashion.dto.response.TranslationResponse;
import com.fashion.service.AITranslationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * Admin-only controller for AI-assisted translation.
 *
 * <p>All endpoints are protected by ROLE_ADMIN.
 * The returned translation is a suggestion — the admin must save it explicitly.
 */
@RestController
@RequestMapping("/admin/ai")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class AdminTranslationController {

    private final AITranslationService aiTranslationService;

    /**
     * Translates Vietnamese content to English using an LLM.
     *
     * <p>Request body:
     * <pre>
     * {
     *   "text": "Áo Polo Nam Cổ Bẻ Vải COTTON Thoáng Khí",
     *   "targetLang": "en",
     *   "context": "product name"
     * }
     * </pre>
     *
     * <p>Response:
     * <pre>
     * {
     *   "translation": "Men's Cotton Polo Shirt with Breathable Fabric",
     *   "sourceText": "Áo Polo Nam Cổ Bẻ Vải COTTON Thoáng Khí",
     *   "targetLang": "en",
     *   "confidence": "high"
     * }
     * </pre>
     */
    @PostMapping("/translate")
    public ResponseEntity<ApiResponse<TranslationResponse>> translate(
            @Valid @RequestBody TranslationRequest request) {
        TranslationResponse result = aiTranslationService.translate(request);
        return ResponseEntity.ok(ApiResponse.success("Dịch thành công", result));
    }
}
