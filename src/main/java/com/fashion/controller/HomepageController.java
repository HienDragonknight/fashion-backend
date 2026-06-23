package com.fashion.controller;

import com.fashion.dto.response.ApiResponse;
import com.fashion.dto.response.HomepageResponse;
import com.fashion.service.HomepageService;
import com.fashion.util.LocaleUtils;
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
    public ResponseEntity<ApiResponse<List<HomepageResponse.BannerSlide>>> getBanners(
            @RequestHeader(value = "Accept-Language", defaultValue = "vi") String acceptLanguage) {
        String lang = LocaleUtils.fromHeader(acceptLanguage);
        return ResponseEntity.ok(ApiResponse.success(homepageService.getBannerSlides(lang)));
    }

    /** GET /api/homepage/collections — CollectionTabs items */
    @GetMapping("/collections")
    public ResponseEntity<ApiResponse<List<HomepageResponse.CollectionItem>>> getCollections(
            @RequestHeader(value = "Accept-Language", defaultValue = "vi") String acceptLanguage) {
        String lang = LocaleUtils.fromHeader(acceptLanguage);
        return ResponseEntity.ok(ApiResponse.success(homepageService.getCollections(lang)));
    }

    /** GET /api/homepage/sections — 6 product sections with 8 products each */
    @GetMapping("/sections")
    public ResponseEntity<ApiResponse<List<HomepageResponse.ProductSection>>> getSections(
            @RequestHeader(value = "Accept-Language", defaultValue = "vi") String acceptLanguage) {
        String lang = LocaleUtils.fromHeader(acceptLanguage);
        return ResponseEntity.ok(ApiResponse.success(homepageService.getProductSections(lang)));
    }

    /** GET /api/homepage/blog-posts — Blog posts */
    @GetMapping("/blog-posts")
    public ResponseEntity<ApiResponse<List<HomepageResponse.BlogPostItem>>> getBlogPosts(
            @RequestHeader(value = "Accept-Language", defaultValue = "vi") String acceptLanguage) {
        String lang = LocaleUtils.fromHeader(acceptLanguage);
        return ResponseEntity.ok(ApiResponse.success(homepageService.getBlogPosts(lang)));
    }
}
