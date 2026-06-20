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
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
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
    @Transactional(readOnly = true)
    public List<HomepageResponse.BannerSlide> getBannerSlides() {
        return bannerRepository
                .findByPositionAndIsActiveTrueOrderBySortOrderAsc("HOME_HERO")
                .stream()
                .map(this::toBannerSlide)
                .collect(Collectors.toList());
    }

    // -------------------------------------------------------
    // Collection Tabs (categories with imageUrl, used in CollectionTabs)
    // -------------------------------------------------------
    @Transactional(readOnly = true)
    public List<HomepageResponse.CollectionItem> getCollections() {
        List<String> collectionSlugs = List.of(
                "dream-team-winner", "ao-chong-nang", "bst-sip-emmm",
                "ao-giu-nhiet-xtra-heat", "jeans-collection", "bst-business-casual",
                "yody-sport-nhe-tenh", "everyday-basics"
        );

        return categoryRepository.findByIsActiveTrueOrderBySortOrderAsc()
                .stream()
                .filter(c -> collectionSlugs.contains(c.getSlug()))
                .sorted((a, b) -> Integer.compare(
                        collectionSlugs.indexOf(a.getSlug()),
                        collectionSlugs.indexOf(b.getSlug())))
                .map(this::toCollectionItem)
                .collect(Collectors.toList());
    }

    // -------------------------------------------------------
    // Product Sections (6 sections for homepage)
    // -------------------------------------------------------
    @Transactional(readOnly = true)
    public List<HomepageResponse.ProductSection> getProductSections() {
        // Each entry: [slug, displayTitle, viewMoreLink]
        List<String[]> sectionDefs = List.of(
                new String[]{"easyoffice",     "EASY OFFICE",               "/collection/EASYOFFICE"},
                new String[]{"polo-all-in-one","POLO ALL-IN-ONE",            "/collection/POLO-ALL-IN-ONE"},
                new String[]{"sale-homepage",  "TIẾT KIỆM LÊN ĐẾN 400K",   "/collection/tiet-kiem-len-den-400k"},
                new String[]{"kid-homepage",   "TỦ ĐỒ CHO BÉ",              "/collection/kid-20"},
                new String[]{"sun-protection", "BST ÁO CHỐNG NẮNG",         "/collection/ao-chong-nang"},
                new String[]{"jean-flex",      "BST JEAN FLEX",              "/collection/jeans-collection"}
        );

        return sectionDefs.stream().map(def -> {
            String slug         = def[0];
            String title        = def[1];
            String viewMoreLink = def[2];

            Category category = categoryRepository.findBySlug(slug).orElse(null);
            if (category == null) {
                return HomepageResponse.ProductSection.builder()
                        .id(slug).title(title).viewMoreLink(viewMoreLink)
                        .products(List.of())
                        .build();
            }

            Specification<Product> spec = (root, q, cb) ->
                    cb.and(
                            cb.isTrue(root.get("isActive")),
                            cb.equal(root.get("category").get("id"), category.getId())
                    );

            List<HomepageResponse.ProductCard> products =
                    productRepository.findAll(spec,
                            PageRequest.of(0, 8, Sort.by("id").ascending()))
                            .stream()
                            .map(this::toProductCard)
                            .collect(Collectors.toList());

            return HomepageResponse.ProductSection.builder()
                    .id(slug)
                    .title(title)
                    .viewMoreLink(viewMoreLink)
                    .products(products)
                    .build();
        }).collect(Collectors.toList());
    }

    // -------------------------------------------------------
    // Blog Posts
    // -------------------------------------------------------
    @Transactional(readOnly = true)
    public List<HomepageResponse.BlogPostItem> getBlogPosts() {
        return blogPostRepository.findByIsActiveTrueOrderBySortOrderAsc()
                .stream()
                .map(this::toBlogPostItem)
                .collect(Collectors.toList());
    }

    // -------------------------------------------------------
    // Mappers
    // -------------------------------------------------------
    private HomepageResponse.BannerSlide toBannerSlide(Banner b) {
        return HomepageResponse.BannerSlide.builder()
                .id(b.getId())
                .badge(b.getBadge())
                .badgeColor(b.getBadgeColor())
                .titleText(b.getTitleText())
                .subtitle(b.getSubtitle())
                .ctaText(b.getCtaText())
                .textColor(b.getTextColor())
                .overlayGradient(b.getOverlayGradient())
                .imageUrl(b.getImageUrl())
                .linkUrl(b.getLinkUrl())
                .sortOrder(b.getSortOrder())
                .build();
    }

    private HomepageResponse.CollectionItem toCollectionItem(Category c) {
        return HomepageResponse.CollectionItem.builder()
                .id(c.getId())
                .name(c.getName())
                .slug(c.getSlug())
                .imageUrl(c.getImageUrl())
                .build();
    }

    private HomepageResponse.ProductCard toProductCard(Product p) {
        return HomepageResponse.ProductCard.builder()
                .id(p.getId())
                .name(p.getName())
                .slug(p.getSlug())
                // effectivePrice = salePrice if exists, else basePrice (current displayed price)
                .price(p.getEffectivePrice())
                // originalPrice = basePrice shown as strikethrough (only when there's a sale)
                .originalPrice(p.getSalePrice() != null ? p.getBasePrice() : null)
                .image(p.getThumbnailUrl())
                .build();
    }

    private HomepageResponse.BlogPostItem toBlogPostItem(BlogPost bp) {
        return HomepageResponse.BlogPostItem.builder()
                .id(bp.getId())
                .title(bp.getTitle())
                .slug(bp.getSlug())
                .date(bp.getDate())
                .image(bp.getImageUrl())
                .build();
    }
}
