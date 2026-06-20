package com.fashion.dto.request;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class ReviewRequest {

    @NotNull
    private Long productId;

    @NotNull
    private Long orderId;

    @Min(1) @Max(5)
    private Integer rating;

    @Size(max = 2000)
    private String comment;
}
