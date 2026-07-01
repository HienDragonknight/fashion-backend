package com.fashion.controller;

import com.fashion.dto.request.ProductRequest;
import com.fashion.dto.response.ApiResponse;
import com.fashion.dto.response.ProductResponse;
import com.fashion.service.ProductService;
import com.fashion.util.LocaleUtils;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @GetMapping
    public ResponseEntity<ApiResponse<Page<ProductResponse>>> getProducts(
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) Long brandId,
            @RequestParam(required = false) java.math.BigDecimal minPrice,
            @RequestParam(required = false) java.math.BigDecimal maxPrice,
            @RequestParam(required = false) String gender,
            @RequestParam(required = false) Integer minDiscountPercent,
            @RequestParam(defaultValue = "newest") String sort,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestHeader(value = "Accept-Language", defaultValue = "vi") String acceptLanguage) {
        String lang = LocaleUtils.fromHeader(acceptLanguage);
        return ResponseEntity.ok(ApiResponse.success(
                productService.getProducts(categoryId, brandId, minPrice, maxPrice, gender, minDiscountPercent, sort, page, size, lang)));
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<Page<ProductResponse>>> search(
            @RequestParam String q,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestHeader(value = "Accept-Language", defaultValue = "vi") String acceptLanguage) {
        String lang = LocaleUtils.fromHeader(acceptLanguage);
        return ResponseEntity.ok(ApiResponse.success(productService.search(q, page, size, lang)));
    }

    @GetMapping("/{slug}")
    public ResponseEntity<ApiResponse<ProductResponse>> getBySlug(
            @PathVariable String slug,
            @RequestHeader(value = "Accept-Language", defaultValue = "vi") String acceptLanguage) {
        String lang = LocaleUtils.fromHeader(acceptLanguage);
        return ResponseEntity.ok(ApiResponse.success(productService.getBySlug(slug, lang)));
    }
}
