package com.fashion.repository;

import com.fashion.entity.Banner;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface BannerRepository extends JpaRepository<Banner, Long> {
    List<Banner> findByPositionAndIsActiveTrueOrderBySortOrderAsc(String position);
    List<Banner> findAllByOrderBySortOrderAsc();
}
