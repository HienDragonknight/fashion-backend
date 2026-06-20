package com.fashion.controller;

import com.fashion.dto.response.ApiResponse;
import com.fashion.entity.Brand;
import com.fashion.exception.ResourceNotFoundException;
import com.fashion.repository.BrandRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/brands")
@RequiredArgsConstructor
public class BrandController {

    private final BrandRepository brandRepository;

    @GetMapping
    public ResponseEntity<ApiResponse<List<Brand>>> getAll() {
        return ResponseEntity.ok(ApiResponse.success(brandRepository.findByIsActiveTrueOrderByNameAsc()));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Brand>> create(@RequestBody Map<String, Object> body) {
        Brand brand = Brand.builder()
                .name((String) body.get("name"))
                .slug((String) body.get("slug"))
                .description((String) body.get("description"))
                .logoUrl((String) body.get("logoUrl"))
                .isActive(true)
                .build();
        return ResponseEntity.ok(ApiResponse.success(brandRepository.save(brand)));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        Brand brand = brandRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Thương hiệu", id));
        brand.setIsActive(false);
        brandRepository.save(brand);
        return ResponseEntity.ok(ApiResponse.success("Đã xóa thương hiệu", null));
    }
}
