package org.crochet.repository;

import org.crochet.model.Collection;
import org.crochet.payload.response.CollectionResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CollectionRepo extends JpaRepository<Collection, String> {

    @Query("""
            SELECT new org.crochet.payload.response.CollectionResponse(c.id, c.name)
            FROM Collection c
            LEFT JOIN User u ON c.user.id = u.id
            WHERE u.id = :userId
            """)
    List<CollectionResponse> getCollectionsByUserId(@Param("userId") String userId);

    @Query("""
            SELECT new org.crochet.payload.response.CollectionResponse(c.id, c.name)
            FROM Collection c
            LEFT JOIN User u ON c.user.id = u.id
            WHERE c.id = :cid AND u.id = :userId
            """)
    Optional<CollectionResponse> getCollectionByUserId(@Param("cid") String cid, @Param("userId") String userId);

    @Query("""
            SELECT (count(c) > 0)
            FROM Collection c
            LEFT JOIN User u ON c.user.id = u.id
            WHERE c.name = :name
            """)
    boolean existsCollectionByName(@Param("name") String collectionName);
}