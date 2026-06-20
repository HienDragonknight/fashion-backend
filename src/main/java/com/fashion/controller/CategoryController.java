package com.fashion.controller;

import com.fashion.dto.response.ApiResponse;
import com.fashion.entity.Category;
import com.fashion.exception.ResourceNotFoundException;
import com.fashion.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryRepository categoryRepository;

    @GetMapping
    public ResponseEntity<ApiResponse<List<Category>>> getAll() {
        return ResponseEntity.ok(ApiResponse.success(
                categoryRepository.findByIsActiveTrueOrderBySortOrderAsc()));
    }

    @GetMapping("/tree")
    public ResponseEntity<ApiResponse<List<Category>>> getTree() {
        return ResponseEntity.ok(ApiResponse.success(
                categoryRepository.findByParentIsNullAndIsActiveTrueOrderBySortOrderAsc()));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Category>> create(@RequestBody Map<String, Object> body) {
        Category cat = Category.builder()
                .name((String) body.get("name"))
                .slug((String) body.get("slug"))
                .isActive(true)
                .sortOrder(body.containsKey("sortOrder") ? (Integer) body.get("sortOrder") : 0)
                .build();
        return ResponseEntity.ok(ApiResponse.success(categoryRepository.save(cat)));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        Category cat = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Danh mục", id));
        cat.setIsActive(false);
        categoryRepository.save(cat);
        return ResponseEntity.ok(ApiResponse.success("Đã xóa danh mục", null));
    }
}
