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
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Component
public class ReplicateTryOnClient {

    private static final Logger log = LoggerFactory.getLogger(ReplicateTryOnClient.class);
    private static final String PREDICTIONS_URL = "https://api.replicate.com/v1/predictions";
    private static final String MODEL_VERSION = "0513734a452173b8173e907e3a59d19a36266e55b48528559432bd21c7d7e985";

    private final Cloudinary cloudinary;
    private final RestTemplate restTemplate;

    @Value("${REPLICATE_API_TOKEN:}")
    private String replicateApiToken;

    public ReplicateTryOnClient(Cloudinary cloudinary) {
        this.cloudinary = cloudinary;
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(10_000);
        factory.setReadTimeout(120_000);
        this.restTemplate = new RestTemplate(factory);
    }

    public boolean isConfigured() {
        return StringUtils.hasText(replicateApiToken);
    }

    public String generateTryOnImage(MultipartFile personImage, String garmentImageUrl,
                                     String productName, String categoryName) {
        if (!isConfigured()) {
            throw new IllegalStateException("Replicate API token is not configured.");
        }

        String personImageUrl = uploadPersonImage(personImage);
        String category = resolveCategory(categoryName);
        log.info("Resolved category: '{}' → '{}'", categoryName, category);

        if ("dresses".equals(category)) {
            log.info("=== TWO-PASS try-on pipeline for full outfit ===");
            String upperDesc = buildEnrichedGarmentDescription(productName, "upper_body");
            String pass1ResultUrl = runSinglePrediction(personImageUrl, garmentImageUrl, upperDesc, "upper_body");

            String lowerDesc = buildEnrichedGarmentDescription(productName, "lower_body");
            String finalResultUrl = runSinglePrediction(pass1ResultUrl, garmentImageUrl, lowerDesc, "lower_body");
            return finalResultUrl;
        }

        String desc = buildEnrichedGarmentDescription(productName, category);
        return runSinglePrediction(personImageUrl, garmentImageUrl, desc, category);
    }

    private String uploadPersonImage(MultipartFile personImage) {
        try {
            log.info("Uploading person image to Cloudinary...");
            Map<?, ?> result = cloudinary.uploader().upload(personImage.getBytes(), ObjectUtils.asMap(
                    "folder", "fashion/tryon_temp",
                    "resource_type", "image"
            ));
            String url = (String) result.get("secure_url");
            return url;
        } catch (Exception e) {
            log.warn("Cloudinary upload failed ({}), using Base64 fallback.", e.getMessage());
            return convertToDataUri(personImage);
        }
    }

    private String resolveCategory(String categoryName) {
        if (!StringUtils.hasText(categoryName)) return "upper_body";
        String lower = categoryName.toLowerCase();
        if (lower.contains("dress") || lower.contains("váy") || lower.contains("đầm")
                || lower.contains("áo dài") || lower.contains("ao-dai")
                || lower.contains("traditional") || lower.contains("cổ phục")
                || lower.equals("dresses")) {
            return "dresses";
        }
        if (lower.contains("lower") || lower.contains("quần")
                || lower.contains("pant") || lower.contains("skirt")) {
            return "lower_body";
        }
        return "upper_body";
    }

    private String runSinglePrediction(String humanImgUrl, String garmentImgUrl,
                                       String description, String category) {
        Map<String, Object> input = new HashMap<>();
        input.put("human_img", humanImgUrl);
        input.put("garm_img", garmentImgUrl);
        input.put("garment_des", description);
        input.put("category", category);
        input.put("crop", true);
        input.put("steps", 40);
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
            log.info("Sending prediction request to Replicate [category={}]...", category);
            response = restTemplate.postForEntity(PREDICTIONS_URL, requestEntity, JsonNode.class);
        } catch (org.springframework.web.client.HttpStatusCodeException e) {
            log.error("Replicate HTTP error: {} {}", e.getStatusCode(), e.getResponseBodyAsString());
            if (e.getStatusCode().value() == 402) {
                throw new RuntimeException("Tài khoản Replicate hết số dư (402). Vui lòng nạp tại https://replicate.com/account/billing");
            }
            if (e.getStatusCode().value() == 401) {
                throw new RuntimeException("Token Replicate không hợp lệ (401). Vui lòng kiểm tra lại.");
            }
            throw new RuntimeException("AI xử lý thất bại: " + e.getStatusText());
        } catch (Exception e) {
            log.error("Cannot connect to Replicate", e);
            throw new RuntimeException("Không thể kết nối đến máy chủ AI (Replicate).", e);
        }

        JsonNode body = response.getBody();
        if (body == null) throw new RuntimeException("Phản hồi từ Replicate trống.");

        String predictionId = body.path("id").asText();
        String pollUrl      = PREDICTIONS_URL + "/" + predictionId;
        String status       = body.path("status").asText();

        int maxAttempts = 60;
        int attempt = 0;
        JsonNode resultNode = body;

        try {
            while (attempt < maxAttempts && ("starting".equals(status) || "processing".equals(status))) {
                Thread.sleep(2000);
                attempt++;

                HttpHeaders getHeaders = new HttpHeaders();
                getHeaders.set("Authorization", "Token " + replicateApiToken);
                ResponseEntity<JsonNode> getResponse = restTemplate.exchange(
                        pollUrl, HttpMethod.GET, new HttpEntity<>(getHeaders), JsonNode.class);
                resultNode = getResponse.getBody();
                if (resultNode != null) {
                    status = resultNode.path("status").asText();
                }
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Quá trình thử đồ bị gián đoạn.", e);
        }

        if ("succeeded".equals(status) && resultNode != null) {
            JsonNode outputNode = resultNode.get("output");
            if (outputNode != null) {
                String replicateUrl = null;
                if (outputNode.isArray() && outputNode.size() > 0) replicateUrl = outputNode.get(0).asText();
                else if (outputNode.isTextual()) replicateUrl = outputNode.asText();

                if (replicateUrl != null) {
                    return uploadResultToCloudinary(replicateUrl);
                }
            }
            throw new RuntimeException("Replicate không trả về kết quả hình ảnh.");
        }

        String errorMsg = resultNode != null ? resultNode.path("error").asText() : "";
        throw new RuntimeException("AI xử lý thất bại: " + (StringUtils.hasText(errorMsg) ? errorMsg : status));
    }

    private String uploadResultToCloudinary(String replicateUrl) {
        try {
            log.info("Uploading try-on result to Cloudinary: {}", replicateUrl);
            byte[] imageBytes = restTemplate.getForObject(replicateUrl, byte[].class);
            if (imageBytes == null || imageBytes.length == 0) {
                return replicateUrl;
            }
            Map<?, ?> result = cloudinary.uploader().upload(imageBytes, ObjectUtils.asMap(
                    "folder", "fashion/tryon_results",
                    "resource_type", "image",
                    "format", "jpg",
                    "quality", "auto:good"
            ));
            return (String) result.get("secure_url");
        } catch (Exception e) {
            log.warn("Cloudinary upload failed ({}), falling back to Replicate URL.", e.getMessage());
            return replicateUrl;
        }
    }

    private String buildEnrichedGarmentDescription(String productName, String category) {
        String baseName = StringUtils.hasText(productName) ? productName.trim() : "garment";
        String lower    = baseName.toLowerCase();

        String fabricHint;
        if (lower.contains("cotton") || lower.contains("bông")) {
            fabricHint = "100% cotton fabric with natural soft texture and subtle wrinkles";
        } else if (lower.contains("linen") || lower.contains("đũi") || lower.contains("lanh")) {
            fabricHint = "linen fabric with characteristic natural creases and relaxed drape";
        } else if (lower.contains("silk") || lower.contains("lụa") || lower.contains("satin")) {
            fabricHint = "smooth satin silk fabric with gentle sheen and flowing drape";
        } else if (lower.contains("chiffon") || lower.contains("voan")) {
            fabricHint = "lightweight chiffon with delicate transparent layers and airy drape";
        } else if (lower.contains("denim") || lower.contains("jean") || lower.contains("bò")) {
            fabricHint = "denim fabric with stiff structured texture and visible weave pattern";
        } else if (lower.contains("áo dài") || lower.contains("ao dai")
                   || lower.contains("truyền thống") || lower.contains("cổ phục")) {
            fabricHint = "traditional Vietnamese silk fabric with rich texture, natural sheen and elegant drape";
        } else {
            fabricHint = "woven fabric with natural texture, realistic wrinkle folds and authentic drape";
        }

        String prefix = switch (category) {
            case "lower_body" -> "Pair of pants or skirt,";
            case "dresses"    -> "Full-length dress,";
            default           -> "Top garment,";
        };

        return prefix + " " + baseName + ". " + fabricHint
               + ". High resolution, realistic fabric simulation with natural shadow and body-conforming fit.";
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
