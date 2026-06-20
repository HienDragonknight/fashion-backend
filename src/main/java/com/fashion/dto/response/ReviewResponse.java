package com.fashion.dto.response;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

@Data @Builder
public class ReviewResponse {
    private Long id;
    private Long userId;
    private String userFullName;
    private String userAvatarUrl;
    private Long productId;
    private Integer rating;
    private String comment;
    private String[] images;
    private Boolean isApproved;
    private LocalDateTime createdAt;
}
