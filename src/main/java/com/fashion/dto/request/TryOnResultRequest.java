package com.fashion.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class TryOnResultRequest {

    @NotBlank(message = "generatedImageUrl is required")
    private String generatedImageUrl;

    private String status;        // COMPLETED | FAILED

    private String errorMessage;
}
