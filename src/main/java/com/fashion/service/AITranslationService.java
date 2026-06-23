package com.fashion.service;

import com.fashion.dto.request.TranslationRequest;
import com.fashion.dto.response.TranslationResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

/**
 * Admin-only AI translation assistant.
 *
 * <p>This service calls an LLM API (OpenAI-compatible) to generate English translations
 * of Vietnamese product/category/brand content. The result is a SUGGESTION only — it
 * must be reviewed and saved by an admin before it is published.
 *
 * <p>If no API key is configured, the service returns a graceful "not configured" message
 * rather than throwing, so the rest of the admin UI remains functional.
 */
@Service
@Slf4j
public class AITranslationService {

    @Value("${ai.translation.api-key:}")
    private String apiKey;

    @Value("${ai.translation.api-url:https://api.openai.com/v1/chat/completions}")
    private String apiUrl;

    @Value("${ai.translation.model:gpt-4o-mini}")
    private String model;

    private final RestTemplate restTemplate = new RestTemplate();

    /**
     * Translates Vietnamese text to English using an LLM.
     *
     * @param request contains source text, target language, and optional context hint
     * @return suggested translation with confidence indicator
     */
    public TranslationResponse translate(TranslationRequest request) {
        if (apiKey == null || apiKey.isBlank()) {
            log.warn("AI translation API key not configured. Returning placeholder.");
            return TranslationResponse.builder()
                    .translation("[AI translation not configured — please set ai.translation.api-key]")
                    .sourceText(request.getText())
                    .targetLang(request.getTargetLang())
                    .confidence("low")
                    .build();
        }

        try {
            String systemPrompt = "You are a professional e-commerce localization specialist.\n\n" +
                    "Translate the following Vietnamese product information into natural commercial English.\n\n" +
                    "Requirements:\n" +
                    "- Do not translate word-by-word.\n" +
                    "- Use terminology commonly found on international e-commerce websites.\n" +
                    "- Preserve product meaning and category.\n" +
                    "- Make the content attractive and professional for customers.\n" +
                    "- Keep product specifications unchanged.\n" +
                    "- Return only the translated text.";

            String context = request.getContext() != null ? request.getContext().toLowerCase() : "";
            String userPrompt;
            if (context.contains("name")) {
                userPrompt = "Product Name:\n" + request.getText();
            } else if (context.contains("description") || context.contains("desc")) {
                userPrompt = "Description:\n" + request.getText();
            } else {
                userPrompt = request.getText();
            }

            Map<String, Object> body = new HashMap<>();
            body.put("model", model);
            body.put("temperature", 0.3);
            body.put("messages", List.of(
                    Map.of("role", "system", "content", systemPrompt),
                    Map.of("role", "user", "content", userPrompt)
            ));

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(apiKey);

            @SuppressWarnings("unchecked")
            Map<String, Object> response = restTemplate.exchange(
                    apiUrl,
                    HttpMethod.POST,
                    new HttpEntity<>(body, headers),
                    Map.class
            ).getBody();

            String translation = extractContent(response);

            return TranslationResponse.builder()
                    .translation(translation)
                    .sourceText(request.getText())
                    .targetLang(request.getTargetLang())
                    .confidence("high")
                    .build();

        } catch (Exception e) {
            log.error("AI translation failed: {}", e.getMessage());
            return TranslationResponse.builder()
                    .translation("[Translation failed: " + e.getMessage() + "]")
                    .sourceText(request.getText())
                    .targetLang(request.getTargetLang())
                    .confidence("low")
                    .build();
        }
    }

    @SuppressWarnings("unchecked")
    private String extractContent(Map<String, Object> response) {
        if (response == null) return "";
        List<Map<String, Object>> choices = (List<Map<String, Object>>) response.get("choices");
        if (choices == null || choices.isEmpty()) return "";
        Map<String, Object> message = (Map<String, Object>) choices.get(0).get("message");
        if (message == null) return "";
        return (String) message.get("content");
    }
}
