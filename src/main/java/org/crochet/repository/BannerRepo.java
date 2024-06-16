package org.crochet.repository;

import org.crochet.model.Banner;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface BannerRepo extends JpaRepository<Banner, String> {
    @Query("""
                select b from Banner b
                where b.active = true
            """)
    List<Banner> findActiveBanners();
}