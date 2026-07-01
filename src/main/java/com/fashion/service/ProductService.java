package com.fashion.service;

import com.fashion.dto.request.ProductRequest;
import com.fashion.dto.response.ProductResponse;
import com.fashion.entity.*;
import com.fashion.exception.BusinessException;
import com.fashion.exception.ResourceNotFoundException;
import com.fashion.repository.*;
import com.fashion.util.LocaleUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.text.Normalizer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

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
        return getProducts(categoryId, brandId, minPrice, maxPrice, null, sort, page, size, LocaleUtils.VI);
    }

    @Transactional(readOnly = true)
    public Page<ProductResponse> getProducts(
            Long categoryId, Long brandId, BigDecimal minPrice, BigDecimal maxPrice,
            String sort, int page, int size, String lang) {
        return getProducts(categoryId, brandId, minPrice, maxPrice, null, sort, page, size, lang);
    }

    @Transactional(readOnly = true)
    public Page<ProductResponse> getProducts(
            Long categoryId, Long brandId, BigDecimal minPrice, BigDecimal maxPrice,
            String gender, String sort, int page, int size, String lang) {
        return getProducts(categoryId, brandId, minPrice, maxPrice, gender, null, sort, page, size, lang);
    }

    @Transactional(readOnly = true)
    public Page<ProductResponse> getProducts(
            Long categoryId, Long brandId, BigDecimal minPrice, BigDecimal maxPrice,
            String gender, Integer minDiscountPercent, String sort, int page, int size, String lang) {

        Specification<Product> spec = Specification.where(isActive());

        if (categoryId != null) spec = spec.and(hasCategory(categoryId));
        if (brandId != null) spec = spec.and(hasBrand(brandId));
        if (minPrice != null) spec = spec.and(priceGte(minPrice));
        if (maxPrice != null) spec = spec.and(priceLte(maxPrice));
        if (gender != null && !gender.isBlank()) spec = spec.and(hasGenderTag(gender.toUpperCase()));
        if (minDiscountPercent != null && minDiscountPercent > 0) spec = spec.and(hasMinDiscount(minDiscountPercent));

        Sort sortObj = switch (sort != null ? sort : "newest") {
            case "price_asc" -> Sort.by("basePrice").ascending();
            case "price_desc" -> Sort.by("basePrice").descending();
            case "popular" -> Sort.by("soldCount").descending();
            case "rating" -> Sort.by("viewCount").descending();
            default -> Sort.by("createdAt").descending();
        };

        Pageable pageable = PageRequest.of(page, size, sortObj);
        return toPageWithBatchRating(productRepository.findAll(spec, pageable), lang);
    }

    @Transactional(readOnly = true)
    public Page<ProductResponse> search(String query, int page, int size) {
        return search(query, page, size, LocaleUtils.VI);
    }

    @Transactional(readOnly = true)
    public Page<ProductResponse> search(String query, int page, int size, String lang) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return toPageWithBatchRating(productRepository.search(query, pageable), lang);
    }

    @Transactional(readOnly = true)
    public ProductResponse getById(Long id) {
        return getById(id, LocaleUtils.VI);
    }

    @Transactional(readOnly = true)
    public ProductResponse getById(Long id, String lang) {
        // JOIN FETCH variants+images+brand+category + batch review in 1 go
        Product product = productRepository.findByIdWithDetails(id)
                .orElseThrow(() -> new ResourceNotFoundException("Sản phẩm", id));
        // Use batch methods with single-element list to avoid 2 separate COUNT/AVG queries
        List<Long> ids = List.of(id);
        Map<Long, Double> avgMap = reviewRepository.avgRatingForProducts(ids).stream()
                .collect(Collectors.toMap(r -> ((Number) r[0]).longValue(), r -> ((Number) r[1]).doubleValue()));
        Map<Long, Long> cntMap = reviewRepository.countByProductIds(ids).stream()
                .collect(Collectors.toMap(r -> ((Number) r[0]).longValue(), r -> ((Number) r[1]).longValue()));
        return toResponse(product, avgMap, cntMap, lang);
    }

    @Transactional
    public ProductResponse getBySlug(String slug) {
        return getBySlug(slug, LocaleUtils.VI);
    }

    @Transactional
    public ProductResponse getBySlug(String slug, String lang) {
        Product product = productRepository.findBySlugAndIsActiveTrue(slug)
                .orElseThrow(() -> new ResourceNotFoundException("Sản phẩm không tìm thấy"));
        productRepository.incrementViewCount(product.getId());
        return toResponse(product, null, null, lang);
    }

    @Transactional
    public ProductResponse create(ProductRequest request) {
        String slug = generateSlug(request.getName());
        if (productRepository.findBySlugAndIsActiveTrue(slug).isPresent()) {
            slug = slug + "-" + System.currentTimeMillis();
        }

        String genderTagsStr = (request.getGenderTags() != null && !request.getGenderTags().isEmpty())
                ? String.join(",", request.getGenderTags())
                : null;

        Product product = Product.builder()
                .name(request.getName())
                .nameEn(request.getNameEn())
                .slug(slug)
                .description(request.getDescription())
                .descriptionEn(request.getDescriptionEn())
                .basePrice(request.getBasePrice())
                .salePrice(request.getSalePrice())
                .thumbnailUrl(request.getThumbnailUrl())
                .isActive(request.getIsActive())
                .isFeatured(request.getIsFeatured())
                .weightGrams(request.getWeightGrams())
                .genderTags(genderTagsStr)
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
                String variantImagesStr = vr.getImageUrls() != null ? String.join(",", vr.getImageUrls()) : null;
                product.getVariants().add(ProductVariant.builder()
                        .product(product)
                        .size(vr.getSize())
                        .color(vr.getColor())
                        .colorHex(vr.getColorHex())
                        .sku(vr.getSku())
                        .stockQty(vr.getStockQty())
                        .priceAdjustment(vr.getPriceAdjustment())
                        .imageUrls(variantImagesStr)
                        .build());
            }
        }

        return toResponse(productRepository.save(product));
    }

    @Transactional
    public ProductResponse update(Long id, ProductRequest request) {
        // Use findByIdWithDetails to eagerly load variants+images — avoids N+1 on save
        Product product = productRepository.findByIdWithDetails(id)
                .orElseThrow(() -> new ResourceNotFoundException("Sản phẩm", id));

        product.setName(request.getName());
        product.setNameEn(request.getNameEn());
        product.setDescription(request.getDescription());
        product.setDescriptionEn(request.getDescriptionEn());
        product.setBasePrice(request.getBasePrice());
        product.setSalePrice(request.getSalePrice());
        product.setThumbnailUrl(request.getThumbnailUrl());
        product.setIsActive(request.getIsActive());
        product.setIsFeatured(request.getIsFeatured());
        product.setWeightGrams(request.getWeightGrams());
        product.setGenderTags(
                (request.getGenderTags() != null && !request.getGenderTags().isEmpty())
                        ? String.join(",", request.getGenderTags())
                        : null
        );

        if (request.getCategoryId() != null) {
            product.setCategory(categoryRepository.findById(request.getCategoryId()).orElse(null));
        } else {
            product.setCategory(null);
        }
        if (request.getBrandId() != null) {
            product.setBrand(brandRepository.findById(request.getBrandId()).orElse(null));
        } else {
            product.setBrand(null);
        }

        // Sync product images
        product.getImages().clear();
        if (request.getImageUrls() != null) {
            for (int i = 0; i < request.getImageUrls().size(); i++) {
                product.getImages().add(ProductImage.builder()
                        .product(product)
                        .imageUrl(request.getImageUrls().get(i))
                        .sortOrder(i)
                        .build());
            }
        }

        // Sync variants by SKU — dùng copy list để tránh ConcurrentModificationException
        List<ProductVariant> existingVariants = new java.util.ArrayList<>(product.getVariants());
        List<ProductRequest.VariantRequest> incomingVariants = request.getVariants();

        if (incomingVariants != null) {
            // Xóa các variant không còn trong request
            List<ProductVariant> toRemove = existingVariants.stream()
                    .filter(ev -> incomingVariants.stream()
                            .noneMatch(iv -> ev.getSku().equalsIgnoreCase(iv.getSku())))
                    .toList();
            product.getVariants().removeAll(toRemove);

            for (ProductRequest.VariantRequest vr : incomingVariants) {
                ProductVariant existing = existingVariants.stream()
                        .filter(ev -> ev.getSku().equalsIgnoreCase(vr.getSku()))
                        .findFirst()
                        .orElse(null);

                String variantImagesStr = vr.getImageUrls() != null ? String.join(",", vr.getImageUrls()) : null;

                if (existing != null) {
                    existing.setSize(vr.getSize());
                    existing.setColor(vr.getColor());
                    existing.setColorHex(vr.getColorHex());
                    existing.setStockQty(vr.getStockQty());
                    existing.setPriceAdjustment(vr.getPriceAdjustment());
                    existing.setImageUrls(variantImagesStr);
                } else {
                    if (variantRepository.existsBySku(vr.getSku())) {
                        throw new BusinessException("SKU đã tồn tại ở sản phẩm khác: " + vr.getSku());
                    }
                    product.getVariants().add(ProductVariant.builder()
                            .product(product)
                            .size(vr.getSize())
                            .color(vr.getColor())
                            .colorHex(vr.getColorHex())
                            .sku(vr.getSku())
                            .stockQty(vr.getStockQty())
                            .priceAdjustment(vr.getPriceAdjustment())
                            .imageUrls(variantImagesStr)
                            .build());
                }
            }
        } else {
            product.getVariants().clear();
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


    /**
     * Locale-aware product → response mapping.
     * Pass {@code lang} from {@link com.fashion.util.LocaleUtils#fromHeader} to resolve bilingual fields.
     */
    public ProductResponse toResponse(Product product,
                                       Map<Long, Double> avgRatingMap,
                                       Map<Long, Long> reviewCountMap,
                                       String lang) {
        String resolvedLang = lang != null ? lang : LocaleUtils.VI;
        Double avgRating = avgRatingMap != null
                ? avgRatingMap.getOrDefault(product.getId(), null)
                : reviewRepository.avgRatingByProductId(product.getId());
        Long reviewCount = reviewCountMap != null
                ? reviewCountMap.getOrDefault(product.getId(), 0L)
                : reviewRepository.countByProductId(product.getId());

        return ProductResponse.builder()
                .id(product.getId())
                .name(LocaleUtils.resolve(product.getName(), product.getNameEn(), resolvedLang))
                .nameVi(product.getName())
                .nameEn(product.getNameEn())
                .slug(product.getSlug())
                .description(LocaleUtils.resolveNullable(product.getDescription(), product.getDescriptionEn(), resolvedLang))
                .descriptionVi(product.getDescription())
                .descriptionEn(product.getDescriptionEn())
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
                .genderTags(product.getGenderTags() != null && !product.getGenderTags().isEmpty()
                        ? List.of(product.getGenderTags().split(","))
                        : List.of())
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
                                .imageUrls(v.getImageUrls() != null && !v.getImageUrls().trim().isEmpty()
                                        ? List.of(v.getImageUrls().split(","))
                                        : List.of())
                                .build()).toList())
                .brand(product.getBrand() != null ? ProductResponse.BrandInfo.builder()
                        .id(product.getBrand().getId())
                        .name(LocaleUtils.resolve(product.getBrand().getName(), product.getBrand().getNameEn(), resolvedLang))
                        .nameEn(product.getBrand().getNameEn())
                        .slug(product.getBrand().getSlug())
                        .logoUrl(product.getBrand().getLogoUrl())
                        .build() : null)
                .category(product.getCategory() != null ? ProductResponse.CategoryInfo.builder()
                        .id(product.getCategory().getId())
                        .name(LocaleUtils.resolve(product.getCategory().getName(), product.getCategory().getNameEn(), resolvedLang))
                        .nameEn(product.getCategory().getNameEn())
                        .slug(product.getCategory().getSlug())
                        .build() : null)
                .createdAt(product.getCreatedAt())
                .build();
    }

    /** Overload: backwards-compatible, defaults to Vietnamese. */
    public ProductResponse toResponse(Product product,
                                       Map<Long, Double> avgRatingMap,
                                       Map<Long, Long> reviewCountMap) {
        return toResponse(product, avgRatingMap, reviewCountMap, LocaleUtils.VI);
    }

    /** Overload backward-compat — called from save/update (1 product) */
    public ProductResponse toResponse(Product product) {
        return toResponse(product, null, null, LocaleUtils.VI);
    }

    private Page<ProductResponse> toPageWithBatchRating(Page<Product> productPage) {
        return toPageWithBatchRating(productPage, LocaleUtils.VI);
    }

    private Page<ProductResponse> toPageWithBatchRating(Page<Product> productPage, String lang) {
        List<Long> ids = productPage.getContent().stream()
                .map(Product::getId).collect(Collectors.toList());

        if (ids.isEmpty()) return productPage.map(p -> toResponse(p, Map.of(), Map.of(), lang));

        // 2 query batch thay thế 2N query
        Map<Long, Double> avgRatingMap = reviewRepository.avgRatingForProducts(ids).stream()
                .collect(Collectors.toMap(
                        row -> ((Number) row[0]).longValue(),
                        row -> ((Number) row[1]).doubleValue()));
        Map<Long, Long> countMap = reviewRepository.countByProductIds(ids).stream()
                .collect(Collectors.toMap(
                        row -> ((Number) row[0]).longValue(),
                        row -> ((Number) row[1]).longValue()));

        return productPage.map(p -> toResponse(p, avgRatingMap, countMap, lang));
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

    /**
     * Filter products whose gender_tags contain the given tag.
     * Uses LIKE to support comma-separated storage (e.g. "MALE,UNISEX" matches MALE and UNISEX).
     */
    private Specification<Product> hasGenderTag(String tag) {
        return (root, query, cb) -> cb.like(root.get("genderTags"), "%" + tag + "%");
    }

    /**
     * Filter products with salePrice set AND discount >= minPercent.
     * discount% = (basePrice - salePrice) / basePrice * 100
     */
    private Specification<Product> hasMinDiscount(int minPercent) {
        return (root, query, cb) -> {
            // salePrice must not be null
            jakarta.persistence.criteria.Predicate hasSale = cb.isNotNull(root.get("salePrice"));
            // (basePrice - salePrice) / basePrice >= minPercent / 100
            // => basePrice - salePrice >= basePrice * minPercent / 100
            // => (basePrice - salePrice) * 100 >= basePrice * minPercent
            jakarta.persistence.criteria.Expression<BigDecimal> base = root.get("basePrice");
            jakarta.persistence.criteria.Expression<BigDecimal> sale = root.get("salePrice");
            jakarta.persistence.criteria.Expression<BigDecimal> diff = cb.diff(base, sale);
            jakarta.persistence.criteria.Expression<BigDecimal> diffX100 = cb.prod(diff, new BigDecimal("100"));
            jakarta.persistence.criteria.Expression<BigDecimal> threshold = cb.prod(base, new BigDecimal(minPercent));
            jakarta.persistence.criteria.Predicate enoughDiscount = cb.greaterThanOrEqualTo(diffX100, threshold);
            return cb.and(hasSale, enoughDiscount);
        };
    }

    private Specification<Product> searchQuery(String q) {
        return (root, query, cb) -> cb.or(
                cb.like(cb.lower(root.get("name")), "%" + q.toLowerCase() + "%"),
                cb.like(cb.lower(root.get("slug")), "%" + q.toLowerCase() + "%"),
                cb.like(cb.lower(root.get("description")), "%" + q.toLowerCase() + "%")
        );
    }

    private Specification<Product> hasActiveStatus(Boolean active) {
        return (root, query, cb) -> cb.equal(root.get("isActive"), active);
    }

    @Transactional(readOnly = true)
    public Page<ProductResponse> getAdminProducts(
            String search, Long categoryId, Long brandId, Boolean isActive,
            String gender, String sort, int page, int size) {

        Specification<Product> spec = Specification.where(null);

        if (search != null && !search.trim().isEmpty()) {
            spec = spec.and(searchQuery(search.trim()));
        }
        if (categoryId != null) {
            spec = spec.and(hasCategory(categoryId));
        }
        if (brandId != null) {
            spec = spec.and(hasBrand(brandId));
        }
        if (isActive != null) {
            spec = spec.and(hasActiveStatus(isActive));
        }
        if (gender != null && !gender.isBlank()) {
            spec = spec.and(hasGenderTag(gender.toUpperCase()));
        }
        
        Sort sortObj = switch (sort != null ? sort : "newest") {
            case "price_asc" -> Sort.by("basePrice").ascending();
            case "price_desc" -> Sort.by("basePrice").descending();
            case "name_asc" -> Sort.by("name").ascending();
            case "name_desc" -> Sort.by("name").descending();
            default -> Sort.by("createdAt").descending();
        };

        Pageable pageable = PageRequest.of(page, size, sortObj);
        return toPageWithBatchRating(productRepository.findAll(spec, pageable));
    }
}
