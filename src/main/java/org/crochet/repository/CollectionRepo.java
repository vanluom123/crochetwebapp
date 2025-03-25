package org.crochet.repository;

import org.crochet.model.Collection;
import org.crochet.payload.response.CollectionResponse;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CollectionRepo extends JpaRepository<Collection, String> {
    @Query("""
            SELECT
              new org.crochet.payload.response.CollectionResponse (c.id, c.name, c.avatar, COUNT(cf.id), u.id)
            FROM
              Collection c
              LEFT JOIN User u ON u.id = c.user.id
              LEFT JOIN ColFrep cf ON cf.collection.id = c.id
            WHERE
              u.id =:userId
            GROUP BY
              c.id,
              c.name,
              c.avatar
            """)
    List<CollectionResponse> getAllByUserId(@Param("userId") String userId);

    @Query("""
            SELECT
              new org.crochet.payload.response.CollectionResponse (c.id, c.name, c.avatar, COUNT(cf.id), u.id)
            FROM
              Collection c
              LEFT JOIN ColFrep cf ON cf.collection.id = c.id
              LEFT JOIN User u ON u.id = c.user.id
            WHERE
              c.id =:cid and c.user.id = :userId
            GROUP BY
              c.id,
              c.name,
              c.avatar
            """)
    Optional<CollectionResponse> getColById(@Param("userId") String userId,
                                            @Param("cid") String cid);

    @EntityGraph(attributePaths = {"user", "colfreps", "colfreps.freePattern", "colfreps.freePattern.images"})
    @Query("""
            SELECT
              c
            FROM
              Collection c
            WHERE
              c.id =:cid
            """)
    Optional<Collection> findColById(@Param("cid") String cid);

    @Query("""
            SELECT (count(c.id) > 0)
            FROM Collection c
            JOIN User u ON u.id = c.user.id
            WHERE c.name = :name AND u.id = :userId
            """)
    boolean existsCollectionByName(@Param("userId") String userId, @Param("name") String collectionName);
}