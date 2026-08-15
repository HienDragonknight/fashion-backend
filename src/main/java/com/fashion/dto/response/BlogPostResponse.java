package com.fashion.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BlogPostResponse {

    private Long id;
    private String title;
    private String titleEn;
    private String slug;
    private String excerpt;
    private String excerptEn;
    private String content;
    private String contentEn;
    private String imageUrl;
    private LocalDate date;
    private Boolean isActive;
    private Integer sortOrder;
    private LocalDateTime createdAt;
}
