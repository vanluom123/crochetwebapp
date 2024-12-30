package org.crochet.repository;

import org.crochet.model.ColFrep;
import org.crochet.model.FreePattern;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface ColFrepRepo extends JpaRepository<ColFrep, String> {
    @Transactional
    @Modifying
    @Query("delete from ColFrep c where c.freePattern = ?1")
    void removeByFreePattern(FreePattern freePattern);

    @Query("select count(c.id) from ColFrep c where c.collection.id = ?1")
    long countByCollectionId(String id);
}
