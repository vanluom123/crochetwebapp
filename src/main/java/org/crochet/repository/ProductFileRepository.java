package org.crochet.repository;

import org.crochet.model.ProductFile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ProductFileRepository extends JpaRepository<ProductFile, UUID> {
}