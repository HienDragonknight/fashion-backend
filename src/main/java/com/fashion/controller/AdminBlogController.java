package com.fashion.controller;

import com.fashion.dto.request.BlogPostRequest;
import com.fashion.dto.response.ApiResponse;
import com.fashion.dto.response.BlogPostResponse;
import com.fashion.service.BlogService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/blog")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class AdminBlogController {

    private final BlogService blogService;

    /** GET /api/admin/blog — Admin paginated list of all posts with search */
    @GetMapping
    public ResponseEntity<ApiResponse<Page<BlogPostResponse>>> getPosts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "15") int size,
            @RequestParam(required = false) String search) {
        return ResponseEntity.ok(ApiResponse.success(blogService.getAdminPosts(page, size, search)));
    }

    /** GET /api/admin/blog/{id} — Get blog post by ID */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<BlogPostResponse>> getPostById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(blogService.getPostById(id)));
    }

    /** POST /api/admin/blog — Create new blog post */
    @PostMapping
    public ResponseEntity<ApiResponse<BlogPostResponse>> createPost(@Valid @RequestBody BlogPostRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Tạo bài viết thành công", blogService.createPost(request)));
    }

    /** PUT /api/admin/blog/{id} — Update blog post */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<BlogPostResponse>> updatePost(
            @PathVariable Long id,
            @Valid @RequestBody BlogPostRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Cập nhật bài viết thành công", blogService.updatePost(id, request)));
    }

    /** DELETE /api/admin/blog/{id} — Delete blog post */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deletePost(@PathVariable Long id) {
        blogService.deletePost(id);
        return ResponseEntity.ok(ApiResponse.success("Đã xóa bài viết thành công", null));
    }

    /** PATCH /api/admin/blog/{id}/toggle — Toggle active status */
    @PatchMapping("/{id}/toggle")
    public ResponseEntity<ApiResponse<BlogPostResponse>> toggleActive(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success("Cập nhật trạng thái hiển thị thành công", blogService.toggleActive(id)));
    }
}
