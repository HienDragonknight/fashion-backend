package com.fashion.controller;

import com.fashion.dto.response.ApiResponse;
import com.fashion.dto.response.BlogPostResponse;
import com.fashion.service.BlogService;
import com.fashion.util.LocaleUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/blog")
@RequiredArgsConstructor
public class BlogController {

    private final BlogService blogService;

    /** GET /api/blog — List paginated active blog posts */
    @GetMapping
    public ResponseEntity<ApiResponse<Page<BlogPostResponse>>> getPosts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "9") int size,
            @RequestParam(required = false) String search,
            @RequestHeader(value = "Accept-Language", defaultValue = "vi") String acceptLanguage) {
        String lang = LocaleUtils.fromHeader(acceptLanguage);
        return ResponseEntity.ok(ApiResponse.success(blogService.getPublicPosts(page, size, search, lang)));
    }

    /** GET /api/blog/{slug} — Get single blog post by slug */
    @GetMapping("/{slug}")
    public ResponseEntity<ApiResponse<BlogPostResponse>> getPostBySlug(
            @PathVariable String slug,
            @RequestHeader(value = "Accept-Language", defaultValue = "vi") String acceptLanguage) {
        String lang = LocaleUtils.fromHeader(acceptLanguage);
        return ResponseEntity.ok(ApiResponse.success(blogService.getPostBySlug(slug, lang)));
    }

    /** GET /api/blog/recent — Top 5 recent posts for widgets */
    @GetMapping("/recent")
    public ResponseEntity<ApiResponse<List<BlogPostResponse>>> getRecentPosts(
            @RequestHeader(value = "Accept-Language", defaultValue = "vi") String acceptLanguage) {
        String lang = LocaleUtils.fromHeader(acceptLanguage);
        return ResponseEntity.ok(ApiResponse.success(blogService.getRecentPosts(lang)));
    }
}
