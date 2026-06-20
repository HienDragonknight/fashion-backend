package com.fashion.service;

import com.fashion.dto.request.ProductRequest;
import com.fashion.dto.response.ProductResponse;
import com.fashion.entity.*;
import com.fashion.exception.BusinessException;
import com.fashion.exception.ResourceNotFoundException;
import com.fashion.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.text.Normalizer;
import java.util.List;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final BrandRepository brandRepository;
    private final ProductVariantRepository variantRepository;
    private final ReviewRepository reviewRepository;

    @Transactional(readOnly = true)
    public Page<ProductResponse> getProducts(
            Long categoryId, Long brandId, BigDecimal minPrice, BigDecimal maxPrice,
            String sort, int page, int size) {

        Specification<Product> spec = Specification.where(isActive());

        if (categoryId != null) spec = spec.and(hasCategory(categoryId));
        if (brandId != null) spec = spec.and(hasBrand(brandId));
        if (minPrice != null) spec = spec.and(priceGte(minPrice));
        if (maxPrice != null) spec = spec.and(priceLte(maxPrice));

        Sort sortObj = switch (sort != null ? sort : "newest") {
            case "price_asc" -> Sort.by("basePrice").ascending();
            case "price_desc" -> Sort.by("basePrice").descending();
            case "popular" -> Sort.by("soldCount").descending();
            case "rating" -> Sort.by("viewCount").descending();
            default -> Sort.by("createdAt").descending();
        };

        Pageable pageable = PageRequest.of(page, size, sortObj);
        return productRepository.findAll(spec, pageable).map(this::toResponse);
    }

    @Transactional(readOnly = true)
    public Page<ProductResponse> search(String query, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return productRepository.search(query, pageable).map(this::toResponse);
    }

    @Transactional
    public ProductResponse getBySlug(String slug) {
        Product product = productRepository.findBySlugAndIsActiveTrue(slug)
                .orElseThrow(() -> new ResourceNotFoundException("Sản phẩm không tìm thấy"));
        productRepository.incrementViewCount(product.getId());
        return toResponse(product);
    }

    @Transactional
    public ProductResponse create(ProductRequest request) {
        String slug = generateSlug(request.getName());
        if (productRepository.findBySlugAndIsActiveTrue(slug).isPresent()) {
            slug = slug + "-" + System.currentTimeMillis();
        }

        Product product = Product.builder()
                .name(request.getName())
                .slug(slug)
                .description(request.getDescription())
                .basePrice(request.getBasePrice())
                .salePrice(request.getSalePrice())
                .thumbnailUrl(request.getThumbnailUrl())
                .isActive(request.getIsActive())
                .isFeatured(request.getIsFeatured())
                .weightGrams(request.getWeightGrams())
                .build();

        if (request.getCategoryId() != null) {
            product.setCategory(categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("Danh mục", request.getCategoryId())));
        }
        if (request.getBrandId() != null) {
            product.setBrand(brandRepository.findById(request.getBrandId())
                    .orElseThrow(() -> new ResourceNotFoundException("Thương hiệu", request.getBrandId())));
        }

        // Images
        if (request.getImageUrls() != null) {
            for (int i = 0; i < request.getImageUrls().size(); i++) {
                product.getImages().add(ProductImage.builder()
                        .product(product)
                        .imageUrl(request.getImageUrls().get(i))
                        .sortOrder(i)
                        .build());
            }
        }

        // Variants
        if (request.getVariants() != null) {
            for (ProductRequest.VariantRequest vr : request.getVariants()) {
                if (variantRepository.existsBySku(vr.getSku())) {
                    throw new BusinessException("SKU đã tồn tại: " + vr.getSku());
                }
                product.getVariants().add(ProductVariant.builder()
                        .product(product)
                        .size(vr.getSize())
                        .color(vr.getColor())
                        .colorHex(vr.getColorHex())
                        .sku(vr.getSku())
                        .stockQty(vr.getStockQty())
                        .priceAdjustment(vr.getPriceAdjustment())
                        .build());
            }
        }

        return toResponse(productRepository.save(product));
    }

    @Transactional
    public ProductResponse update(Long id, ProductRequest request) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Sản phẩm", id));

        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setBasePrice(request.getBasePrice());
        product.setSalePrice(request.getSalePrice());
        product.setThumbnailUrl(request.getThumbnailUrl());
        product.setIsActive(request.getIsActive());
        product.setIsFeatured(request.getIsFeatured());
        product.setWeightGrams(request.getWeightGrams());

        if (request.getCategoryId() != null) {
            product.setCategory(categoryRepository.findById(request.getCategoryId()).orElse(null));
        }
        if (request.getBrandId() != null) {
            product.setBrand(brandRepository.findById(request.getBrandId()).orElse(null));
        }

        return toResponse(productRepository.save(product));
    }

    @Transactional
    public void delete(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Sản phẩm", id));
        product.setIsActive(false);
        productRepository.save(product);
    }

    public ProductResponse toResponse(Product product) {
        Double avgRating = reviewRepository.avgRatingByProductId(product.getId());
        Long reviewCount = reviewRepository.countByProductId(product.getId());

        return ProductResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .slug(product.getSlug())
                .description(product.getDescription())
                .basePrice(product.getBasePrice())
                .salePrice(product.getSalePrice())
                .effectivePrice(product.getEffectivePrice())
                .thumbnailUrl(product.getThumbnailUrl())
                .isActive(product.getIsActive())
                .isFeatured(product.getIsFeatured())
                .weightGrams(product.getWeightGrams())
                .soldCount(product.getSoldCount())
                .viewCount(product.getViewCount())
                .avgRating(avgRating)
                .reviewCount(reviewCount != null ? reviewCount : 0L)
                .imageUrls(product.getImages().stream()
                        .map(ProductImage::getImageUrl).toList())
                .variants(product.getVariants().stream().map(v ->
                        ProductResponse.VariantResponse.builder()
                                .id(v.getId())
                                .size(v.getSize())
                                .color(v.getColor())
                                .colorHex(v.getColorHex())
                                .sku(v.getSku())
                                .stockQty(v.getStockQty())
                                .priceAdjustment(v.getPriceAdjustment())
                                .label(v.getLabel())
                                .build()).toList())
                .brand(product.getBrand() != null ? ProductResponse.BrandInfo.builder()
                        .id(product.getBrand().getId())
                        .name(product.getBrand().getName())
                        .slug(product.getBrand().getSlug())
                        .logoUrl(product.getBrand().getLogoUrl())
                        .build() : null)
                .category(product.getCategory() != null ? ProductResponse.CategoryInfo.builder()
                        .id(product.getCategory().getId())
                        .name(product.getCategory().getName())
                        .slug(product.getCategory().getSlug())
                        .build() : null)
                .createdAt(product.getCreatedAt())
                .build();
    }

    public static String generateSlug(String input) {
        String normalized = Normalizer.normalize(input, Normalizer.Form.NFD);
        Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
        String slug = pattern.matcher(normalized).replaceAll("")
                .toLowerCase()
                .replaceAll("[^a-z0-9\\s-]", "")
                .replaceAll("\\s+", "-")
                .replaceAll("-+", "-")
                .replaceAll("đ", "d");
        return slug;
    }

    // Specifications
    private Specification<Product> isActive() {
        return (root, q, cb) -> cb.isTrue(root.get("isActive"));
    }

    private Specification<Product> hasCategory(Long categoryId) {
        return (root, q, cb) -> cb.equal(root.get("category").get("id"), categoryId);
    }

    private Specification<Product> hasBrand(Long brandId) {
        return (root, q, cb) -> cb.equal(root.get("brand").get("id"), brandId);
    }

    private Specification<Product> priceGte(BigDecimal min) {
        return (root, q, cb) -> cb.greaterThanOrEqualTo(root.get("basePrice"), min);
    }

    private Specification<Product> priceLte(BigDecimal max) {
        return (root, q, cb) -> cb.lessThanOrEqualTo(root.get("basePrice"), max);
    }
}
