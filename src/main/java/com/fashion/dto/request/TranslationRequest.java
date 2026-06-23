package com.fashion.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

/**
 * Request body for the admin AI translation endpoint.
 */
@Data
public class TranslationRequest {

    /** Source text to translate (Vietnamese). */
    @NotBlank(message = "text must not be blank")
    private String text;

    /** Target language code — currently only "en" is supported. */
    @NotBlank
    @Pattern(regexp = "en|vi", message = "targetLang must be 'en' or 'vi'")
    private String targetLang;

    /**
     * Optional context hint for the AI model — e.g. "product name", "category name",
     * "product description". Helps the model produce domain-appropriate output.
     */
    private String context;
}
