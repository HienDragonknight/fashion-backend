package com.fashion.controller;

import com.fashion.dto.response.ApiResponse;
import com.fashion.dto.response.OrderResponse;
import com.fashion.dto.response.ProductResponse;
import com.fashion.dto.response.ReviewResponse;
import com.fashion.dto.response.UserResponse;
import com.fashion.dto.request.ProductRequest;
import com.fashion.entity.Banner;
import com.fashion.entity.Promotion;
import com.fashion.entity.User;
import com.fashion.repository.*;
import com.fashion.service.OrderService;
import com.fashion.service.ProductService;
import com.fashion.service.ReviewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Year;
import java.util.*;

@RestController
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class AdminController {

    private final ProductService productService;
    private final OrderService orderService;
    private final ReviewService reviewService;
    private final UserRepository userRepository;
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final ProductVariantRepository productVariantRepository;
    private final BannerRepository bannerRepository;
    private final PromotionRepository promotionRepository;

    // ── DASHBOARD ─────────────────────────────────────────────
    @GetMapping("/dashboard/stats")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getDashboardStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalUsers", userRepository.count());
        stats.put("totalOrders", orderRepository.count());
        stats.put("totalProducts", productRepository.count());
        stats.put("pendingOrders", orderRepository.countByStatus("PENDING"));
        stats.put("confirmedOrders", orderRepository.countByStatus("CONFIRMED"));
        stats.put("deliveredOrders", orderRepository.countByStatus("DELIVERED"));
        stats.put("cancelledOrders", orderRepository.countByStatus("CANCELLED"));

        // Revenue
        BigDecimal totalRevenue = orderRepository.sumTotalRevenue();
        stats.put("totalRevenue", totalRevenue != null ? totalRevenue : BigDecimal.ZERO);

        // Today revenue
        stats.put("revenueToday", BigDecimal.ZERO); // Can be extended with date filter
        return ResponseEntity.ok(ApiResponse.success(stats));
    }

    // ── DASHBOARD REVENUE BY MONTH ─────────────────────────────
    @GetMapping("/dashboard/revenue")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> getRevenueByMonth(
            @RequestParam(defaultValue = "0") int year) {
        int targetYear = year > 0 ? year : Year.now().getValue();

        List<Object[]> raw = orderRepository.revenueByMonth(targetYear);
        // Build map month 1..12
        Map<Integer, Object[]> byMonth = new LinkedHashMap<>();
        for (Object[] row : raw) {
            byMonth.put(((Number) row[0]).intValue(), row);
        }

        List<Map<String, Object>> result = new ArrayList<>();
        String[] monthNames = {"T1","T2","T3","T4","T5","T6","T7","T8","T9","T10","T11","T12"};
        for (int m = 1; m <= 12; m++) {
            Map<String, Object> entry = new LinkedHashMap<>();
            entry.put("month", monthNames[m - 1]);
            entry.put("monthNum", m);
            if (byMonth.containsKey(m)) {
                Object[] row = byMonth.get(m);
                entry.put("revenue", row[1]);
                entry.put("orders", row[2]);
            } else {
                entry.put("revenue", 0);
                entry.put("orders", 0);
            }
            result.add(entry);
        }
        return ResponseEntity.ok(ApiResponse.success(result));
    }

    // ── DASHBOARD TOP PRODUCTS ─────────────────────────────────
    @GetMapping("/dashboard/top-products")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> getTopProducts(
            @RequestParam(defaultValue = "5") int size) {
        Pageable pageable = PageRequest.of(0, size);
        List<Object[]> raw = productRepository.topProductsBySales(pageable);

        // Fallback: if no order data yet, use soldCount
        if (raw.isEmpty()) {
            List<com.fashion.entity.Product> products = productRepository.findTopBySoldCount(pageable);
            List<Map<String, Object>> fallback = new ArrayList<>();
            for (com.fashion.entity.Product p : products) {
                Map<String, Object> m = new LinkedHashMap<>();
                m.put("id", p.getId());
                m.put("name", p.getName());
                m.put("thumbnailUrl", p.getThumbnailUrl());
                m.put("category", p.getCategoryName());
                m.put("sold", p.getSoldCount());
                m.put("revenue", p.getEffectivePrice()
                        .multiply(BigDecimal.valueOf(p.getSoldCount())));
                fallback.add(m);
            }
            return ResponseEntity.ok(ApiResponse.success(fallback));
        }

        List<Map<String, Object>> result = new ArrayList<>();
        for (Object[] row : raw) {
            Map<String, Object> m = new LinkedHashMap<>();
            m.put("id", row[0]);
            m.put("name", row[1]);
            m.put("thumbnailUrl", row[2]);
            m.put("category", row[3]);
            m.put("sold", row[4]);
            m.put("revenue", row[5]);
            result.add(m);
        }
        return ResponseEntity.ok(ApiResponse.success(result));
    }

    // ── REPORTS ───────────────────────────────────────────────

    @GetMapping("/reports/revenue")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getRevenueReport(
            @RequestParam(defaultValue = "0") int year) {
        int targetYear = year > 0 ? year : Year.now().getValue();

        // Monthly revenue
        List<Object[]> raw = orderRepository.revenueByMonth(targetYear);
        Map<Integer, Object[]> byMonth = new LinkedHashMap<>();
        for (Object[] row : raw) byMonth.put(((Number) row[0]).intValue(), row);

        String[] monthNames = {"T1","T2","T3","T4","T5","T6","T7","T8","T9","T10","T11","T12"};
        List<Map<String, Object>> monthly = new ArrayList<>();
        long totalOrders = 0;
        BigDecimal totalRevenue = BigDecimal.ZERO;

        for (int m = 1; m <= 12; m++) {
            Map<String, Object> entry = new LinkedHashMap<>();
            entry.put("month", monthNames[m - 1]);
            BigDecimal rev = BigDecimal.ZERO;
            long orders = 0;
            if (byMonth.containsKey(m)) {
                Object[] row = byMonth.get(m);
                rev = ((BigDecimal) row[1]);
                orders = ((Number) row[2]).longValue();
            }
            entry.put("revenue", rev);
            // Estimate profit as 30% margin
            entry.put("profit", rev.multiply(BigDecimal.valueOf(0.30)));
            entry.put("orders", orders);
            monthly.add(entry);
            totalRevenue = totalRevenue.add(rev);
            totalOrders += orders;
        }

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("year", targetYear);
        result.put("totalRevenue", totalRevenue);
        result.put("totalProfit", totalRevenue.multiply(BigDecimal.valueOf(0.30)));
        result.put("totalOrders", totalOrders);
        result.put("avgOrderValue", totalOrders > 0
                ? totalRevenue.divide(BigDecimal.valueOf(totalOrders), 0, java.math.RoundingMode.HALF_UP)
                : BigDecimal.ZERO);
        result.put("monthly", monthly);

        return ResponseEntity.ok(ApiResponse.success(result));
    }

    @GetMapping("/reports/sales")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getSalesReport() {
        // Top products
        List<Object[]> topRaw = productRepository.topProductsBySales(PageRequest.of(0, 10));
        List<Map<String, Object>> topProducts = new ArrayList<>();
        for (Object[] row : topRaw) {
            Map<String, Object> m = new LinkedHashMap<>();
            m.put("id", row[0]);
            m.put("name", row[1]);
            m.put("thumbnailUrl", row[2]);
            m.put("category", row[3]);
            m.put("sold", row[4]);
            m.put("revenue", row[5]);
            topProducts.add(m);
        }

        // Category revenue
        List<Object[]> catRaw = productRepository.revenueByCategory();
        List<Map<String, Object>> categories = new ArrayList<>();
        BigDecimal catTotal = catRaw.stream()
                .map(r -> (BigDecimal) r[1])
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        for (Object[] row : catRaw) {
            Map<String, Object> m = new LinkedHashMap<>();
            m.put("name", row[0]);
            BigDecimal rev = (BigDecimal) row[1];
            m.put("revenue", rev);
            double pct = catTotal.compareTo(BigDecimal.ZERO) > 0
                    ? rev.doubleValue() / catTotal.doubleValue() * 100 : 0;
            m.put("percentage", Math.round(pct));
            categories.add(m);
        }

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("topProducts", topProducts);
        result.put("categories", categories);
        return ResponseEntity.ok(ApiResponse.success(result));
    }

    @GetMapping("/reports/customers")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getCustomerReport() {
        long totalCustomers = userRepository.count();

        // Top customers by spending
        List<Object[]> topRaw = orderRepository.topCustomersBySpending(PageRequest.of(0, 10));
        List<Map<String, Object>> topCustomers = new ArrayList<>();
        for (Object[] row : topRaw) {
            Map<String, Object> m = new LinkedHashMap<>();
            m.put("id", row[0]);
            m.put("fullName", row[1]);
            m.put("email", row[2]);
            m.put("orders", row[3]);
            m.put("totalSpent", row[4]);
            topCustomers.add(m);
        }

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("totalCustomers", totalCustomers);
        result.put("topCustomers", topCustomers);
        return ResponseEntity.ok(ApiResponse.success(result));
    }

    @GetMapping("/reports/inventory")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getInventoryReport() {
        // Get all products with variants for inventory stats
        List<com.fashion.entity.Product> products = productRepository.findAll();
        long totalSku = 0;
        long lowStock = 0;
        long outOfStock = 0;
        long totalUnits = 0;

        Map<String, Long[]> categoryStock = new LinkedHashMap<>();

        for (com.fashion.entity.Product p : products) {
            String cat = p.getCategoryName() != null ? p.getCategoryName() : "Khác";
            categoryStock.putIfAbsent(cat, new Long[]{0L, 0L, 0L}); // [total, low, out]
            for (com.fashion.entity.ProductVariant v : p.getVariants()) {
                int qty = v.getStockQty() != null ? v.getStockQty() : 0;
                totalSku++;
                totalUnits += qty;
                Long[] cs = categoryStock.get(cat);
                cs[0]++;
                if (qty == 0) { outOfStock++; cs[2]++; }
                else if (qty <= 10) { lowStock++; cs[1]++; }
            }
        }

        List<Map<String, Object>> catList = new ArrayList<>();
        for (Map.Entry<String, Long[]> e : categoryStock.entrySet()) {
            Map<String, Object> m = new LinkedHashMap<>();
            m.put("category", e.getKey());
            m.put("total", e.getValue()[0]);
            m.put("low", e.getValue()[1]);
            m.put("outOfStock", e.getValue()[2]);
            catList.add(m);
        }

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("totalSku", totalSku);
        result.put("totalUnits", totalUnits);
        result.put("lowStock", lowStock);
        result.put("outOfStock", outOfStock);
        result.put("categories", catList);
        return ResponseEntity.ok(ApiResponse.success(result));
    }

    // ── PRODUCTS ───────────────────────────────────────────────
    @GetMapping("/products")
    public ResponseEntity<ApiResponse<Page<ProductResponse>>> getProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(ApiResponse.success(
                productService.getProducts(null, null, null, null, "newest", page, size)));
    }

    @PostMapping("/products")
    public ResponseEntity<ApiResponse<ProductResponse>> createProduct(
            @Valid @RequestBody ProductRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Tạo sản phẩm thành công",
                productService.create(request)));
    }

    @PutMapping("/products/{id}")
    public ResponseEntity<ApiResponse<ProductResponse>> updateProduct(
            @PathVariable Long id, @Valid @RequestBody ProductRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Cập nhật sản phẩm thành công",
                productService.update(id, request)));
    }

    @DeleteMapping("/products/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteProduct(@PathVariable Long id) {
        productService.delete(id);
        return ResponseEntity.ok(ApiResponse.success("Đã xóa sản phẩm", null));
    }

    // ── ORDERS ────────────────────────────────────────────────
    @GetMapping("/orders")
    public ResponseEntity<ApiResponse<Page<OrderResponse>>> getOrders(
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(ApiResponse.success(orderService.getAllOrders(status, page, size)));
    }

    @PutMapping("/orders/{id}/status")
    public ResponseEntity<ApiResponse<OrderResponse>> updateOrderStatus(
            @PathVariable Long id, @RequestBody Map<String, String> body) {
        return ResponseEntity.ok(ApiResponse.success("Cập nhật trạng thái đơn hàng",
                orderService.updateStatus(id, body.get("status"))));
    }

    // ── REVIEWS ──────────────────────────────────────────────
    @GetMapping("/reviews")
    public ResponseEntity<ApiResponse<Page<ReviewResponse>>> getPendingReviews(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(ApiResponse.success(reviewService.getPendingReviews(page, size)));
    }

    @PutMapping("/reviews/{id}/approve")
    public ResponseEntity<ApiResponse<ReviewResponse>> approveReview(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success("Đã duyệt đánh giá",
                reviewService.approveReview(id)));
    }

    @DeleteMapping("/reviews/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteReview(@PathVariable Long id) {
        reviewService.deleteReview(id);
        return ResponseEntity.ok(ApiResponse.success("Đã xóa đánh giá", null));
    }

    // ── CUSTOMERS ────────────────────────────────────────────
    @GetMapping("/customers")
    public ResponseEntity<ApiResponse<Page<UserResponse>>> getCustomers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        var usersPage = userRepository.findAll(PageRequest.of(page, size))
                .map(u -> UserResponse.builder()
                        .id(u.getId())
                        .email(u.getEmail())
                        .phone(u.getPhone())
                        .fullName(u.getFullName())
                        .avatarUrl(u.getAvatarUrl())
                        .role(u.getRole())
                        .isActive(u.getIsActive())
                        .createdAt(u.getCreatedAt())
                        .build());
        return ResponseEntity.ok(ApiResponse.success(usersPage));
    }

    @GetMapping("/customers/{id}")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getCustomerDetail(@PathVariable Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new com.fashion.exception.ResourceNotFoundException("Người dùng", id));

        long orderCount = orderRepository.countByUserId(id);
        BigDecimal totalSpent = orderRepository.sumTotalByUserId(id);

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("id", user.getId());
        result.put("fullName", user.getFullName());
        result.put("email", user.getEmail());
        result.put("phone", user.getPhone());
        result.put("avatarUrl", user.getAvatarUrl());
        result.put("role", user.getRole());
        result.put("isActive", user.getIsActive());
        result.put("createdAt", user.getCreatedAt());
        result.put("totalOrders", orderCount);
        result.put("totalSpent", totalSpent != null ? totalSpent : BigDecimal.ZERO);

        return ResponseEntity.ok(ApiResponse.success(result));
    }

    @PutMapping("/customers/{id}/status")
    public ResponseEntity<ApiResponse<Void>> toggleCustomerStatus(
            @PathVariable Long id, @RequestBody Map<String, Boolean> body) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new com.fashion.exception.ResourceNotFoundException("Người dùng", id));
        user.setIsActive(body.get("isActive"));
        userRepository.save(user);
        return ResponseEntity.ok(ApiResponse.success("Cập nhật trạng thái thành công", null));
    }

    // ── BANNERS ──────────────────────────────────────────────
    @GetMapping("/banners")
    public ResponseEntity<ApiResponse<Object>> getBanners() {
        return ResponseEntity.ok(ApiResponse.success(bannerRepository.findAllByOrderBySortOrderAsc()));
    }

    @PostMapping("/banners")
    public ResponseEntity<ApiResponse<Banner>> createBanner(@RequestBody Banner banner) {
        return ResponseEntity.ok(ApiResponse.success(bannerRepository.save(banner)));
    }

    @PutMapping("/banners/{id}")
    public ResponseEntity<ApiResponse<Banner>> updateBanner(
            @PathVariable Long id, @RequestBody Banner request) {
        Banner banner = bannerRepository.findById(id)
                .orElseThrow(() -> new com.fashion.exception.ResourceNotFoundException("Banner", id));
        banner.setTitle(request.getTitle());
        banner.setImageUrl(request.getImageUrl());
        banner.setLinkUrl(request.getLinkUrl());
        banner.setIsActive(request.getIsActive());
        banner.setSortOrder(request.getSortOrder());
        return ResponseEntity.ok(ApiResponse.success(bannerRepository.save(banner)));
    }

    @DeleteMapping("/banners/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteBanner(@PathVariable Long id) {
        bannerRepository.deleteById(id);
        return ResponseEntity.ok(ApiResponse.success("Đã xóa banner", null));
    }

    // ── INVENTORY ────────────────────────────────────────────
    @GetMapping("/inventory")
    public ResponseEntity<ApiResponse<Page<ProductResponse>>> getInventory(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(ApiResponse.success(
                productService.getProducts(null, null, null, null, "newest", page, size)));
    }

    @PutMapping("/inventory/variants/{variantId}")
    public ResponseEntity<ApiResponse<Void>> updateVariantStock(
            @PathVariable Long variantId,
            @RequestBody Map<String, Integer> body) {
        com.fashion.entity.ProductVariant variant = productVariantRepository.findById(variantId)
                .orElseThrow(() -> new com.fashion.exception.ResourceNotFoundException("Biến thể", variantId));
        variant.setStockQty(body.get("stockQty"));
        productVariantRepository.save(variant);
        return ResponseEntity.ok(ApiResponse.success("Cập nhật tồn kho thành công", null));
    }

    // ── PROMOTIONS ───────────────────────────────────────────
    @GetMapping("/promotions")
    public ResponseEntity<ApiResponse<List<Promotion>>> getPromotions() {
        return ResponseEntity.ok(ApiResponse.success(promotionRepository.findAll()));
    }

    @PostMapping("/promotions")
    public ResponseEntity<ApiResponse<Promotion>> createPromotion(@RequestBody Promotion promotion) {
        promotion.setId(null);
        promotion.setUsedCount(0);
        return ResponseEntity.ok(ApiResponse.success("Tạo coupon thành công",
                promotionRepository.save(promotion)));
    }

    @PutMapping("/promotions/{id}")
    public ResponseEntity<ApiResponse<Promotion>> updatePromotion(
            @PathVariable Long id, @RequestBody Promotion request) {
        Promotion existing = promotionRepository.findById(id)
                .orElseThrow(() -> new com.fashion.exception.ResourceNotFoundException("Coupon", id));
        existing.setCode(request.getCode());
        existing.setDescription(request.getDescription());
        existing.setDiscountType(request.getDiscountType());
        existing.setDiscountValue(request.getDiscountValue());
        existing.setMinOrderValue(request.getMinOrderValue());
        existing.setMaxDiscount(request.getMaxDiscount());
        existing.setUsageLimit(request.getUsageLimit());
        existing.setStartDate(request.getStartDate());
        existing.setEndDate(request.getEndDate());
        existing.setIsActive(request.getIsActive());
        return ResponseEntity.ok(ApiResponse.success("Cập nhật coupon thành công",
                promotionRepository.save(existing)));
    }

    @PatchMapping("/promotions/{id}/toggle")
    public ResponseEntity<ApiResponse<Promotion>> togglePromotion(@PathVariable Long id) {
        Promotion existing = promotionRepository.findById(id)
                .orElseThrow(() -> new com.fashion.exception.ResourceNotFoundException("Coupon", id));
        existing.setIsActive(!existing.getIsActive());
        return ResponseEntity.ok(ApiResponse.success("Cập nhật trạng thái coupon",
                promotionRepository.save(existing)));
    }

    @DeleteMapping("/promotions/{id}")
    public ResponseEntity<ApiResponse<Void>> deletePromotion(@PathVariable Long id) {
        promotionRepository.deleteById(id);
        return ResponseEntity.ok(ApiResponse.success("Đã xóa coupon", null));
    }
}
