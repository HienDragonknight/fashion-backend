package com.fashion.dto.response;

import lombok.Builder;
import lombok.Data;

/**
 * Response body for the admin AI translation endpoint.
 */
@Data
@Builder
public class TranslationResponse {

    /** AI-suggested translation. Admin must review before saving. */
    private String translation;

    /** Source text that was translated (echoed back for client-side validation). */
    private String sourceText;

    /** Target language code. */
    private String targetLang;

    /**
     * Confidence indicator from the AI model.
     * One of: "high", "medium", "low".
     * Clients may show a warning for "low" confidence.
     */
    private String confidence;
}
