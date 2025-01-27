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
            SELECT
              new org.crochet.payload.response.CollectionResponse (c.id, c.name, c.avatar, COUNT(cf.id))
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
    List<CollectionResponse> getCollectionsByUserId(@Param("userId") String userId);

    @Query("""
            SELECT
              new org.crochet.payload.response.CollectionResponse (c.id, c.name, c.avatar, COUNT(cf.id))
            FROM
              Collection c
              LEFT JOIN User u ON u.id = c.user.id
              LEFT JOIN ColFrep cf ON cf.collection.id = c.id
            WHERE
              c.id =:cid
              AND u.id =:userId
            GROUP BY
              c.id,
              c.name,
              c.avatar
            """)
    Optional<CollectionResponse> getCollectionByUserId(@Param("cid") String cid, @Param("userId") String userId);

    @Query("""
            SELECT
              c
            FROM
              Collection c
              LEFT JOIN FETCH c.colfreps cf
              LEFT JOIN FETCH cf.freePattern fp
              LEFT JOIN FETCH fp.images
            WHERE
              c.id =:cid
              AND c.user.id =:userId
            """)
    Optional<Collection> findCollectionByUserId(@Param("cid") String cid, @Param("userId") String userId);

    @Query("""
            SELECT (count(c.id) > 0)
            FROM Collection c
            WHERE c.name = :name
            """)
    boolean existsCollectionByName(@Param("name") String collectionName);

    @Query("""
            SELECT c
            FROM Collection c
            LEFT JOIN FETCH c.colfreps cf
            WHERE c.id = :id
            """)
    Collection findColById(@Param("id") String id);
}