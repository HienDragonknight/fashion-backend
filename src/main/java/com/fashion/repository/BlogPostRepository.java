package com.fashion.repository;

import com.fashion.entity.BlogPost;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface BlogPostRepository extends JpaRepository<BlogPost, Long> {

    List<BlogPost> findByIsActiveTrueOrderBySortOrderAsc();

    List<BlogPost> findTop5ByIsActiveTrueOrderByDateDesc();

    Optional<BlogPost> findBySlug(String slug);

    Optional<BlogPost> findBySlugAndIsActiveTrue(String slug);

    Page<BlogPost> findByIsActiveTrue(Pageable pageable);

    @Query("SELECT b FROM BlogPost b WHERE b.isActive = true AND (" +
           "LOWER(b.title) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(b.titleEn) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(b.excerpt) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(b.content) LIKE LOWER(CONCAT('%', :query, '%')))")
    Page<BlogPost> searchPublicPosts(@Param("query") String query, Pageable pageable);

    @Query("SELECT b FROM BlogPost b WHERE " +
           "LOWER(b.title) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(b.titleEn) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(b.slug) LIKE LOWER(CONCAT('%', :query, '%'))")
    Page<BlogPost> searchAdminPosts(@Param("query") String query, Pageable pageable);

    boolean existsBySlug(String slug);

    boolean existsBySlugAndIdNot(String slug, Long id);
}
