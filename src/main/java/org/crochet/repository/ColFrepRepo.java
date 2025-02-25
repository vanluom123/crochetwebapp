package org.crochet.repository;

import org.crochet.model.ColFrep;
import org.crochet.model.Collection;
import org.crochet.model.FreePattern;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface ColFrepRepo extends JpaRepository<ColFrep, String> {
    @Transactional
    @Modifying
    @Query("delete from ColFrep c where c.freePattern.id = :frepId")
    void removeByFreePattern(@Param("frepId") String frepId);

    @Query("select count(c.id) from ColFrep c where c.collection.id = :id")
    long countByCollectionId(@Param("id") String id);

    @Query("""
            SELECT cf.freePattern
            FROM ColFrep cf
            WHERE cf.collection.id = :collectionId
            ORDER BY cf.createdDate DESC
            """)
    List<FreePattern> findFirstFreePatternByCollectionId(@Param("collectionId") String collectionId);

    @Query("""
            SELECT cf.collection
            FROM ColFrep cf
            JOIN cf.collection.user u ON cf.collection.user.id = u.id
            WHERE cf.freePattern.id = :frepId
              AND u.id = :userId
            """)
    Collection findColByUserAndFreePattern(@Param("userId") String userId, @Param("frepId") String frepId);
}
