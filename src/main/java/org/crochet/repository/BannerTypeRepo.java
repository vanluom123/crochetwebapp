package org.crochet.repository;

import org.crochet.model.BannerType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface BannerTypeRepo extends JpaRepository<BannerType, UUID> {
    Optional<BannerType> findByName(String name);
}