package org.crochet.repository;

import org.crochet.model.Banner;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BannerRepo extends JpaRepository<Banner, String> {
}