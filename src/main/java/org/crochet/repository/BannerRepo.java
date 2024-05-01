package org.crochet.repository;

import org.crochet.model.Banner;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface BannerRepo extends JpaRepository<Banner, UUID> {
}