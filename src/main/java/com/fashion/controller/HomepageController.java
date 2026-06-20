package com.fashion.controller;

import com.fashion.dto.response.ApiResponse;
import com.fashion.dto.response.HomepageResponse;
import com.fashion.service.HomepageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/homepage")
@RequiredArgsConstructor
public class HomepageController {

    private final HomepageService homepageService;

    /** GET /api/homepage/banners — Hero slider slides */
    @GetMapping("/banners")
    public ResponseEntity<ApiResponse<List<HomepageResponse.BannerSlide>>> getBanners() {
        return ResponseEntity.ok(ApiResponse.success(homepageService.getBannerSlides()));
    }

    /** GET /api/homepage/collections — CollectionTabs items */
    @GetMapping("/collections")
    public ResponseEntity<ApiResponse<List<HomepageResponse.CollectionItem>>> getCollections() {
        return ResponseEntity.ok(ApiResponse.success(homepageService.getCollections()));
    }

    /** GET /api/homepage/sections — 6 product sections with 8 products each */
    @GetMapping("/sections")
    public ResponseEntity<ApiResponse<List<HomepageResponse.ProductSection>>> getSections() {
        return ResponseEntity.ok(ApiResponse.success(homepageService.getProductSections()));
    }

    /** GET /api/homepage/blog-posts — Blog posts */
    @GetMapping("/blog-posts")
    public ResponseEntity<ApiResponse<List<HomepageResponse.BlogPostItem>>> getBlogPosts() {
        return ResponseEntity.ok(ApiResponse.success(homepageService.getBlogPosts()));
    }
}
