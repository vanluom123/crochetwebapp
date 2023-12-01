package org.crochet.repository;

import org.crochet.model.BlogFile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface BlogFileRepository extends JpaRepository<BlogFile, UUID> {
}