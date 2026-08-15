package com.fashion.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BlogPostRequest {

    @NotBlank(message = "Tiêu đề bài viết không được để trống")
    @Size(max = 500, message = "Tiêu đề không quá 500 ký tự")
    private String title;

    @Size(max = 500, message = "Tiêu đề tiếng Anh không quá 500 ký tự")
    private String titleEn;

    private String slug;

    private String excerpt;

    private String excerptEn;

    @NotBlank(message = "Nội dung bài viết không được để trống")
    private String content;

    private String contentEn;

    @NotBlank(message = "Ảnh đại diện bài viết không được để trống")
    private String imageUrl;

    private LocalDate date;

    @Builder.Default
    private Boolean isActive = true;

    @Builder.Default
    private Integer sortOrder = 0;
}
