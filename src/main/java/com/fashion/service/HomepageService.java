package com.fashion.service;

import com.fashion.dto.response.HomepageResponse;
import com.fashion.entity.Banner;
import com.fashion.entity.BlogPost;
import com.fashion.entity.Category;
import com.fashion.entity.Product;
import com.fashion.repository.BannerRepository;
import com.fashion.repository.BlogPostRepository;
import com.fashion.repository.CategoryRepository;
import com.fashion.repository.ProductRepository;
import com.fashion.util.LocaleUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class HomepageService {

    private final BannerRepository      bannerRepository;
    private final CategoryRepository    categoryRepository;
    private final ProductRepository     productRepository;
    private final BlogPostRepository    blogPostRepository;

    // -------------------------------------------------------
    // Hero Banner Slides
    // -------------------------------------------------------

    /** Backward-compatible overload — defaults to Vietnamese. */
    @Transactional(readOnly = true)
    public List<HomepageResponse.BannerSlide> getBannerSlides() {
        return getBannerSlides(LocaleUtils.VI);
    }

    @Transactional(readOnly = true)
    public List<HomepageResponse.BannerSlide> getBannerSlides(String lang) {
        return bannerRepository
                .findByPositionAndIsActiveTrueOrderBySortOrderAsc("HOME_HERO")
                .stream()
                .map(b -> toBannerSlide(b, lang))
                .collect(Collectors.toList());
    }

    // -------------------------------------------------------
    // Collection Tabs (categories with imageUrl)
    // -------------------------------------------------------

    @Transactional(readOnly = true)
    public List<HomepageResponse.CollectionItem> getCollections() {
        return getCollections(LocaleUtils.VI);
    }

    @Transactional(readOnly = true)
    public List<HomepageResponse.CollectionItem> getCollections(String lang) {
        List<Category> activeCategories = categoryRepository.findByIsActiveTrueOrderBySortOrderAsc();
        
        return activeCategories.stream()
                .filter(c -> c.getImageUrl() != null && !c.getImageUrl().trim().isEmpty())
                .map(c -> toCollectionItem(c, lang))
                .collect(Collectors.toList());
    }

    // -------------------------------------------------------
    // Product Sections (dynamic sections based on database categories)
    // -------------------------------------------------------

    @Transactional(readOnly = true)
    public List<HomepageResponse.ProductSection> getProductSections() {
        return getProductSections(LocaleUtils.VI);
    }

    @Transactional(readOnly = true)
    public List<HomepageResponse.ProductSection> getProductSections(String lang) {
        List<Category> activeCategories = categoryRepository.findByIsActiveTrueOrderBySortOrderAsc();

        return activeCategories.stream()
                .map(category -> {
                    Specification<Product> spec = (root, q, cb) ->
                            cb.and(
                                    cb.isTrue(root.get("isActive")),
                                    cb.equal(root.get("category").get("id"), category.getId())
                            );

                    List<HomepageResponse.ProductCard> products =
                            productRepository.findAll(spec,
                                    PageRequest.of(0, 8, Sort.by("id").descending()))
                                    .stream()
                                    .map(p -> toProductCard(p, lang))
                                    .collect(Collectors.toList());

                    if (products.isEmpty()) {
                        return null;
                    }

                    String title = LocaleUtils.resolve(category.getName(), category.getNameEn(), lang);
                    String viewMoreLink = "/products?categoryId=" + category.getId();

                    return HomepageResponse.ProductSection.builder()
                            .id(String.valueOf(category.getId()))
                            .title(title)
                            .viewMoreLink(viewMoreLink)
                            .products(products)
                            .build();
                })
                .filter(java.util.Objects::nonNull)
                .collect(Collectors.toList());
    }

    // -------------------------------------------------------
    // Blog Posts
    // -------------------------------------------------------

    @Transactional(readOnly = true)
    public List<HomepageResponse.BlogPostItem> getBlogPosts() {
        return getBlogPosts(LocaleUtils.VI);
    }

    @Transactional(readOnly = true)
    public List<HomepageResponse.BlogPostItem> getBlogPosts(String lang) {
        return blogPostRepository.findByIsActiveTrueOrderBySortOrderAsc()
                .stream()
                .map(bp -> toBlogPostItem(bp, lang))
                .collect(Collectors.toList());
    }

    // -------------------------------------------------------
    // Mappers — all accept lang for bilingual resolution
    // -------------------------------------------------------

    private HomepageResponse.BannerSlide toBannerSlide(Banner b, String lang) {
        return HomepageResponse.BannerSlide.builder()
                .id(b.getId())
                .badge(LocaleUtils.resolve(b.getBadge(), b.getBadgeEn(), lang))
                .badgeEn(b.getBadgeEn())
                .badgeColor(b.getBadgeColor())
                .titleText(b.getTitleText())
                .subtitle(LocaleUtils.resolveNullable(b.getSubtitle(), b.getSubtitleEn(), lang))
                .subtitleEn(b.getSubtitleEn())
                .ctaText(LocaleUtils.resolve(b.getCtaText(), b.getCtaTextEn(), lang))
                .ctaTextEn(b.getCtaTextEn())
                .textColor(b.getTextColor())
                .overlayGradient(b.getOverlayGradient())
                .imageUrl(b.getImageUrl())
                .linkUrl(b.getLinkUrl())
                .sortOrder(b.getSortOrder())
                .build();
    }

    private HomepageResponse.CollectionItem toCollectionItem(Category c, String lang) {
        return HomepageResponse.CollectionItem.builder()
                .id(c.getId())
                .name(LocaleUtils.resolve(c.getName(), c.getNameEn(), lang))
                .nameEn(c.getNameEn())
                .slug(c.getSlug())
                .imageUrl(c.getImageUrl())
                .build();
    }

    private HomepageResponse.ProductCard toProductCard(Product p, String lang) {
        return HomepageResponse.ProductCard.builder()
                .id(p.getId())
                .name(LocaleUtils.resolve(p.getName(), p.getNameEn(), lang))
                .slug(p.getSlug())
                .price(p.getEffectivePrice())
                .originalPrice(p.getSalePrice() != null ? p.getBasePrice() : null)
                .image(p.getThumbnailUrl())
                .build();
    }

    private HomepageResponse.BlogPostItem toBlogPostItem(BlogPost bp, String lang) {
        return HomepageResponse.BlogPostItem.builder()
                .id(bp.getId())
                .title(LocaleUtils.resolve(bp.getTitle(), bp.getTitleEn(), lang))
                .titleEn(bp.getTitleEn())
                .slug(bp.getSlug())
                .date(bp.getDate())
                .image(bp.getImageUrl())
                .excerpt(LocaleUtils.resolveNullable(bp.getExcerpt(), bp.getExcerptEn(), lang))
                .build();
    }
}
