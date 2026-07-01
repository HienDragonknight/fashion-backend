package com.fashion.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class TryOnRequest {

    private Long productId;

    private String productName;

    @NotBlank(message = "originalImageUrl is required")
    private String originalImageUrl;

    private String productImageUrl;
}
