package org.crochet.repository;

import org.crochet.model.BannerType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BannerTypeRepo extends JpaRepository<BannerType, String> {
    Optional<BannerType> findByName(String name);
}