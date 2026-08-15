package com.fashion.service;

import com.fashion.dto.request.BlogPostRequest;
import com.fashion.dto.response.BlogPostResponse;
import com.fashion.entity.BlogPost;
import com.fashion.exception.BusinessException;
import com.fashion.exception.ResourceNotFoundException;
import com.fashion.repository.BlogPostRepository;
import com.fashion.util.LocaleUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.Normalizer;
import java.time.LocalDate;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BlogService {

    private final BlogPostRepository blogPostRepository;

    private static final Pattern NONLATIN = Pattern.compile("[^\\w-]");
    private static final Pattern WHITESPACE = Pattern.compile("[\\s]");
    private static final Pattern EDGESDHASHES = Pattern.compile("(^-|-$)");

    // ── PUBLIC METHODS ────────────────────────────────────────────────────────

    @Transactional(readOnly = true)
    public Page<BlogPostResponse> getPublicPosts(int page, int size, String search, String lang) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("sortOrder").ascending().and(Sort.by("date").descending()));
        Page<BlogPost> postsPage;

        if (search != null && !search.trim().isEmpty()) {
            postsPage = blogPostRepository.searchPublicPosts(search.trim(), pageable);
        } else {
            postsPage = blogPostRepository.findByIsActiveTrue(pageable);
        }

        return postsPage.map(post -> toLocalizedResponse(post, lang));
    }

    @Transactional(readOnly = true)
    public BlogPostResponse getPostBySlug(String slug, String lang) {
        BlogPost post = blogPostRepository.findBySlugAndIsActiveTrue(slug)
                .orElseThrow(() -> new ResourceNotFoundException("Bài viết không tìm thấy với slug: " + slug));
        return toLocalizedResponse(post, lang);
    }

    @Transactional(readOnly = true)
    public List<BlogPostResponse> getRecentPosts(String lang) {
        return blogPostRepository.findTop5ByIsActiveTrueOrderByDateDesc()
                .stream()
                .map(post -> toLocalizedResponse(post, lang))
                .collect(Collectors.toList());
    }

    // ── ADMIN METHODS ─────────────────────────────────────────────────────────

    @Transactional(readOnly = true)
    public Page<BlogPostResponse> getAdminPosts(int page, int size, String search) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("sortOrder").ascending().and(Sort.by("id").descending()));
        Page<BlogPost> postsPage;

        if (search != null && !search.trim().isEmpty()) {
            postsPage = blogPostRepository.searchAdminPosts(search.trim(), pageable);
        } else {
            postsPage = blogPostRepository.findAll(pageable);
        }

        return postsPage.map(this::toFullResponse);
    }

    @Transactional(readOnly = true)
    public BlogPostResponse getPostById(Long id) {
        BlogPost post = blogPostRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Bài viết", id));
        return toFullResponse(post);
    }

    @Transactional
    public BlogPostResponse createPost(BlogPostRequest request) {
        String baseSlug = (request.getSlug() != null && !request.getSlug().trim().isEmpty())
                ? toSlug(request.getSlug())
                : toSlug(request.getTitle());

        String uniqueSlug = generateUniqueSlug(baseSlug, null);

        BlogPost post = BlogPost.builder()
                .title(request.getTitle().trim())
                .titleEn(request.getTitleEn() != null ? request.getTitleEn().trim() : null)
                .slug(uniqueSlug)
                .excerpt(request.getExcerpt() != null ? request.getExcerpt().trim() : null)
                .excerptEn(request.getExcerptEn() != null ? request.getExcerptEn().trim() : null)
                .content(request.getContent())
                .contentEn(request.getContentEn())
                .imageUrl(request.getImageUrl().trim())
                .date(request.getDate() != null ? request.getDate() : LocalDate.now())
                .isActive(request.getIsActive() != null ? request.getIsActive() : true)
                .sortOrder(request.getSortOrder() != null ? request.getSortOrder() : 0)
                .build();

        return toFullResponse(blogPostRepository.save(post));
    }

    @Transactional
    public BlogPostResponse updatePost(Long id, BlogPostRequest request) {
        BlogPost post = blogPostRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Bài viết", id));

        String baseSlug = (request.getSlug() != null && !request.getSlug().trim().isEmpty())
                ? toSlug(request.getSlug())
                : toSlug(request.getTitle());

        String uniqueSlug = generateUniqueSlug(baseSlug, id);

        post.setTitle(request.getTitle().trim());
        post.setTitleEn(request.getTitleEn() != null ? request.getTitleEn().trim() : null);
        post.setSlug(uniqueSlug);
        post.setExcerpt(request.getExcerpt() != null ? request.getExcerpt().trim() : null);
        post.setExcerptEn(request.getExcerptEn() != null ? request.getExcerptEn().trim() : null);
        post.setContent(request.getContent());
        post.setContentEn(request.getContentEn());
        post.setImageUrl(request.getImageUrl().trim());
        if (request.getDate() != null) {
            post.setDate(request.getDate());
        }
        if (request.getIsActive() != null) {
            post.setIsActive(request.getIsActive());
        }
        if (request.getSortOrder() != null) {
            post.setSortOrder(request.getSortOrder());
        }

        return toFullResponse(blogPostRepository.save(post));
    }

    @Transactional
    public void deletePost(Long id) {
        if (!blogPostRepository.existsById(id)) {
            throw new ResourceNotFoundException("Bài viết", id);
        }
        blogPostRepository.deleteById(id);
    }

    @Transactional
    public BlogPostResponse toggleActive(Long id) {
        BlogPost post = blogPostRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Bài viết", id));
        post.setIsActive(!post.getIsActive());
        return toFullResponse(blogPostRepository.save(post));
    }

    // ── MAPPERS & UTILS ───────────────────────────────────────────────────────

    private BlogPostResponse toFullResponse(BlogPost post) {
        return BlogPostResponse.builder()
                .id(post.getId())
                .title(post.getTitle())
                .titleEn(post.getTitleEn())
                .slug(post.getSlug())
                .excerpt(post.getExcerpt())
                .excerptEn(post.getExcerptEn())
                .content(post.getContent())
                .contentEn(post.getContentEn())
                .imageUrl(post.getImageUrl())
                .date(post.getDate())
                .isActive(post.getIsActive())
                .sortOrder(post.getSortOrder())
                .createdAt(post.getCreatedAt())
                .build();
    }

    private BlogPostResponse toLocalizedResponse(BlogPost post, String lang) {
        return BlogPostResponse.builder()
                .id(post.getId())
                .title(LocaleUtils.resolve(post.getTitle(), post.getTitleEn(), lang))
                .titleEn(post.getTitleEn())
                .slug(post.getSlug())
                .excerpt(LocaleUtils.resolveNullable(post.getExcerpt(), post.getExcerptEn(), lang))
                .excerptEn(post.getExcerptEn())
                .content(LocaleUtils.resolveNullable(post.getContent(), post.getContentEn(), lang))
                .contentEn(post.getContentEn())
                .imageUrl(post.getImageUrl())
                .date(post.getDate())
                .isActive(post.getIsActive())
                .sortOrder(post.getSortOrder())
                .createdAt(post.getCreatedAt())
                .build();
    }

    public static String toSlug(String input) {
        if (input == null || input.isBlank()) return "bai-viet";
        String nowhitespace = WHITESPACE.matcher(input).replaceAll("-");
        String normalized = Normalizer.normalize(nowhitespace, Normalizer.Form.NFD);
        // Replace Vietnamese 'đ' / 'Đ'
        normalized = normalized.replaceAll("[đĐ]", "d");
        String slug = NONLATIN.matcher(normalized).replaceAll("");
        slug = EDGESDHASHES.matcher(slug).replaceAll("");
        slug = slug.replaceAll("-+", "-");
        return slug.toLowerCase(Locale.ENGLISH);
    }

    private String generateUniqueSlug(String baseSlug, Long excludeId) {
        String slug = baseSlug;
        int count = 1;
        while (excludeId == null ? blogPostRepository.existsBySlug(slug) : blogPostRepository.existsBySlugAndIdNot(slug, excludeId)) {
            slug = baseSlug + "-" + count++;
        }
        return slug;
    }
}
