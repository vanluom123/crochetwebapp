package org.crochet.repository;

import jakarta.persistence.QueryHint;
import org.crochet.model.FreePattern;
import org.crochet.payload.response.FreePatternOnHome;
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
            select
              new org.crochet.payload.response.FreePatternOnHome(
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
            from
              FreePattern fp
              left join fp.images i with i.order = 0
              join User u on fp.createdBy = u.id
            where
              fp.isHome = true
            """)
    List<FreePatternOnHome> findLimitedNumFreePattern(Pageable pageable);

    @Query("""
            select
              f
            from
              FreePattern f
              join f.colfreps colFrep
              join colFrep.collection c
            where
              c.user.id = : userId
              and f.id = : frepId
            """)
    Optional<FreePattern> findFrepInCollection(@Param("userId") String userId,
                                               @Param("frepId") String frepId);

    @Query(value = """
            select new org.crochet.payload.response.FreePatternOnHome(
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
            from
                FreePattern fp
                join User u on fp.createdBy = u.id
                left join fp.images i with i.order = 0
            where
                fp.id in :ids
            """
    )
    @QueryHints(value = {
            @QueryHint(name = HINT_FETCH_SIZE, value = "50"),
            @QueryHint(name = HINT_READ_ONLY, value = "true")
    })
    Page<FreePatternOnHome> getFreePatternOnHomeWithIds(@Param("ids") List<String> ids, Pageable pageable);

    @Query("select f.id from FreePattern f order by f.createdDate desc")
    List<String> getFreePatternIds(Pageable pageable);

    @Query("""
            select
              new org.crochet.payload.response.FreePatternOnHome(
                fp.id, fp.name, fp.description, fp.author,
                fp.status, i.fileContent, u.name,
                u.imageUrl, u.id
              )
            from
              FreePattern fp
              left join fp.images i with i.order = 0
              join User u on fp.createdBy = u.id
            where
              u.id = : userId
            """)
    List<FreePatternOnHome> getFrepsByCreateByWithUser(@Param("userId") String userId);

    @Query("""
            select new org.crochet.payload.response.FreePatternOnHome(
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
            from
                FreePattern fp
                left join fp.images i with i.order = 0
                join User u on fp.createdBy = u.id
            where
                u.id = :userId
                and fp.id in :ids
            """)
    Page<FreePatternOnHome> getByUserAndIds(@Param("userId") String userId, @Param("ids") List<String> ids, Pageable pageable);

    @Query("""
            select
              new org.crochet.payload.response.FreePatternOnHome(
                fp.id, fp.name, fp.description, fp.author,
                fp.status, i.fileContent
              )
            from
              FreePattern fp
              left join fp.images i with i.order = 0
              join fp.colfreps colFrep
              join colFrep.collection c
            where
              c.user.id = : userId
              and c.id = : colId
            """)
    List<FreePatternOnHome> getFrepsByCollection(@Param("userId") String userId,
                                                 @Param("colId") String collectionId);

    @Transactional
    @Modifying
    @Query("""
            delete from
              FreePattern f
            where
              f.id in : ids
              and f.createdBy = : userId
            """)
    void deleteAllByIdAndCreatedBy(@Param("ids") List<String> ids,
                                   @Param("userId") String userId);
}