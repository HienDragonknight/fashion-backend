package com.fashion.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.fasterxml.jackson.databind.JsonNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Component
public class OotdiffusionClient {

    private static final Logger log = LoggerFactory.getLogger(OotdiffusionClient.class);
    private static final String PREDICTIONS_URL = "https://api.replicate.com/v1/predictions";
    private static final String MODEL_VERSION = "9f8fa4956970dde99689af7488157a30aa152e23953526a605df1d77598343d7";

    private final Cloudinary cloudinary;
    private final RestTemplate restTemplate;

    @Value("${REPLICATE_API_TOKEN:}")
    private String replicateApiToken;

    public OotdiffusionClient(Cloudinary cloudinary) {
        this.cloudinary = cloudinary;
        this.restTemplate = new RestTemplate();
    }

    public boolean isConfigured() {
        return StringUtils.hasText(replicateApiToken);
    }

    public String generateTryOnImage(MultipartFile personImage, String garmentImageUrl) {
        if (!isConfigured()) {
            throw new IllegalStateException("Replicate API token is not configured.");
        }

        String personImageUrl;
        try {
            log.info("[OOTDiffusion] Uploading person image to Cloudinary...");
            Map<?, ?> uploadResult = cloudinary.uploader().upload(personImage.getBytes(), ObjectUtils.asMap(
                    "folder", "fashion/tryon_temp",
                    "resource_type", "image"
            ));
            personImageUrl = (String) uploadResult.get("secure_url");
        } catch (Exception e) {
            log.warn("[OOTDiffusion] Cloudinary upload failed, using Base64...");
            personImageUrl = convertToDataUri(personImage);
        }

        Map<String, Object> input = new HashMap<>();
        input.put("model_image", personImageUrl);
        input.put("garment_image", garmentImageUrl);
        input.put("steps", 40);
        input.put("guidance_scale", 2.0);
        input.put("seed", 42);

        Map<String, Object> payload = new HashMap<>();
        payload.put("version", MODEL_VERSION);
        payload.put("input", input);

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Token " + replicateApiToken);
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(payload, headers);
        ResponseEntity<JsonNode> response;
        try {
            response = restTemplate.postForEntity(PREDICTIONS_URL, requestEntity, JsonNode.class);
        } catch (org.springframework.web.client.HttpStatusCodeException e) {
            log.error("[OOTDiffusion] API error: {}", e.getResponseBodyAsString());
            if (e.getStatusCode().value() == 402) {
                throw new RuntimeException("Tài khoản Replicate hết số dư (402). Vui lòng nạp tiền tại https://replicate.com/account/billing");
            }
            if (e.getStatusCode().value() == 401) {
                throw new RuntimeException("Token Replicate không hợp lệ (401 Unauthorized).");
            }
            throw new RuntimeException("OOTDiffusion xử lý thất bại: " + e.getStatusText());
        }

        JsonNode body = response.getBody();
        if (body == null) throw new RuntimeException("Phản hồi từ OOTDiffusion trống.");

        String predictionId = body.path("id").asText();
        String getUrl = PREDICTIONS_URL + "/" + predictionId;
        String status = body.path("status").asText();

        int maxAttempts = 60;
        int attempt = 0;
        JsonNode predictionNode = body;

        try {
            while (attempt < maxAttempts && ("starting".equals(status) || "processing".equals(status))) {
                Thread.sleep(2000);
                attempt++;

                HttpHeaders getHeaders = new HttpHeaders();
                getHeaders.set("Authorization", "Token " + replicateApiToken);
                HttpEntity<Void> getEntity = new HttpEntity<>(getHeaders);
                ResponseEntity<JsonNode> getResponse = restTemplate.exchange(getUrl, HttpMethod.GET, getEntity, JsonNode.class);
                predictionNode = getResponse.getBody();
                if (predictionNode != null) {
                    status = predictionNode.path("status").asText();
                }
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Quá trình thử đồ OOTDiffusion bị gián đoạn.", e);
        }

        if ("succeeded".equals(status) && predictionNode != null) {
            JsonNode outputNode = predictionNode.get("output");
            if (outputNode != null) {
                if (outputNode.isArray() && outputNode.size() > 0) {
                    return outputNode.get(0).asText();
                } else if (outputNode.isTextual()) {
                    return outputNode.asText();
                }
            }
            throw new RuntimeException("OOTDiffusion không trả về hình ảnh kết quả.");
        } else {
            String errorMsg = predictionNode != null ? predictionNode.path("error").asText() : "";
            throw new RuntimeException("OOTDiffusion thất bại: " + (StringUtils.hasText(errorMsg) ? errorMsg : status));
        }
    }

    private String convertToDataUri(MultipartFile file) {
        try {
            String mimeType = file.getContentType();
            if (!StringUtils.hasText(mimeType)) mimeType = "image/jpeg";
            String base64 = java.util.Base64.getEncoder().encodeToString(file.getBytes());
            return "data:" + mimeType + ";base64," + base64;
        } catch (IOException e) {
            throw new RuntimeException("Không thể xử lý file ảnh tải lên.", e);
        }
    }
}
