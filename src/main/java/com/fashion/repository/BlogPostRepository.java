package com.fashion.repository;

import com.fashion.entity.BlogPost;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BlogPostRepository extends JpaRepository<BlogPost, Long> {
    List<BlogPost> findByIsActiveTrueOrderBySortOrderAsc();
}
