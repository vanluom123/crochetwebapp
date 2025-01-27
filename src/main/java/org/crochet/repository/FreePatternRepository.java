package org.crochet.repository;

import jakarta.persistence.QueryHint;
import org.crochet.model.FreePattern;
import org.crochet.payload.response.FreePatternResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static org.hibernate.jpa.HibernateHints.HINT_FETCH_SIZE;
import static org.hibernate.jpa.HibernateHints.HINT_READ_ONLY;

@Repository
public interface FreePatternRepository extends JpaRepository<FreePattern, String>,
        JpaSpecificationExecutor<FreePattern> {

    @Query("""
            SELECT
              f
            FROM
              FreePattern f
              JOIN FETCH f.category
            WHERE
              f.id =:id
            """)
    Optional<FreePattern> findFrepById(String id);

    @Query("""
            SELECT
              new org.crochet.payload.response.FreePatternResponse (
                fp.id,
                fp.name,
                fp.description,
                fp.author,
                fp.status,
                i.fileContent,
                u.name,
                u.imageUrl,
                u.id
              )
            FROM
              FreePattern fp
              JOIN fp.images i WITH i.order = 0
              JOIN User u ON fp.createdBy = u.id
            WHERE
              fp.isHome = TRUE
            """)
    List<FreePatternResponse> findLimitedNumFreePattern(Pageable pageable);

    @Query("""
            SELECT
              f
            FROM
              FreePattern f
              JOIN f.colfreps colFrep
              JOIN colFrep.collection c
            WHERE
              c.user.id =:userId
              AND f.id =:frepId
            """)
    Optional<FreePattern> findFrepInCollection(@Param("userId") String userId,
                                               @Param("frepId") String frepId);

    @Query(value = """
            SELECT
              new org.crochet.payload.response.FreePatternResponse (
                fp.id,
                fp.name,
                fp.description,
                fp.author,
                fp.status,
                i.fileContent,
                u.name,
                u.imageUrl,
                u.id
              )
            FROM
              FreePattern fp
              JOIN User u ON fp.createdBy = u.id
              JOIN fp.images i
            WITH
              i.order = 0
            WHERE
              fp.id IN :ids
            """)
    @QueryHints(value = {
            @QueryHint(name = HINT_FETCH_SIZE, value = "50"),
            @QueryHint(name = HINT_READ_ONLY, value = "true")
    })
    Page<FreePatternResponse> getFrepByIds(@Param("ids") List<String> ids, Pageable pageable);

    @Query(value = """
            SELECT
              new org.crochet.payload.response.FreePatternResponse (
                fp.id,
                fp.name,
                fp.description,
                fp.author,
                fp.status,
                i.fileContent,
                u.name,
                u.imageUrl,
                u.id
              )
            FROM
              FreePattern fp
              JOIN User u ON fp.createdBy = u.id
              JOIN fp.images i
            WITH
              i.order = 0
            """)
    @QueryHints(value = {
            @QueryHint(name = HINT_FETCH_SIZE, value = "50"),
            @QueryHint(name = HINT_READ_ONLY, value = "true")
    })
    Page<FreePatternResponse> getFrepWithPageable(Pageable pageable);

    @Query("select f.id from FreePattern f order by f.createdDate desc")
    List<String> getFreePatternIds(Pageable pageable);

    @Query("""
            SELECT
              new org.crochet.payload.response.FreePatternResponse (
                fp.id,
                fp.name,
                fp.description,
                fp.author,
                fp.status,
                i.fileContent,
                u.name,
                u.imageUrl,
                u.id
              )
            FROM
              FreePattern fp
              JOIN User u ON fp.createdBy = u.id
              JOIN fp.images i WITH i.order = 0
            WHERE
              u.id =:userId
            """)
    List<FreePatternResponse> getFrepsByCreateByWithUser(@Param("userId") String userId);

    @Query("""
            SELECT
              new org.crochet.payload.response.FreePatternResponse (
                fp.id,
                fp.name,
                fp.description,
                fp.author,
                fp.status,
                i.fileContent,
                u.name,
                u.imageUrl,
                u.id
              )
            FROM
              FreePattern fp
              JOIN User u ON fp.createdBy = u.id
              JOIN fp.images i WITH i.order = 0
            WHERE
              u.id =:userId
              AND fp.id IN :ids
            """)
    Page<FreePatternResponse> getByUserAndIds(@Param("userId") String userId, @Param("ids") List<String> ids,
                                              Pageable pageable);

    @Query("""
            SELECT
              new org.crochet.payload.response.FreePatternResponse (
                fp.id,
                fp.name,
                fp.description,
                fp.author,
                fp.status,
                i.fileContent
              )
            FROM
              FreePattern fp
              JOIN fp.images i WITH i.order = 0
              JOIN fp.colfreps colFrep
              JOIN colFrep.collection c
            WHERE
              c.user.id =:userId
              AND c.id =:colId
            """)
    List<FreePatternResponse> getFrepsByCollection(@Param("userId") String userId,
                                                   @Param("colId") String collectionId);

    @Transactional
    @Modifying
    @Query("""
            DELETE FROM FreePattern f
            WHERE
              f.id IN :ids
              AND f.createdBy =:userId
            """)
    void deleteAllByIdAndCreatedBy(
            @Param("ids") List<String> ids,
            @Param("userId") String userId);
}