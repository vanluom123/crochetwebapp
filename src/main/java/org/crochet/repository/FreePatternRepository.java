package org.crochet.repository;

import org.crochet.model.FreePattern;
import org.crochet.payload.response.FreePatternOnHome;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface FreePatternRepository extends JpaRepository<FreePattern, String>, JpaSpecificationExecutor<FreePattern> {

    @Query("""
            select new org.crochet.payload.response.FreePatternOnHome(fp.id, fp.name, fp.description, fp.author, fp.status, i.fileContent, u.name, u.imageUrl)
            from FreePattern fp
            left join fp.images i
            join User u on fp.createdBy = u.id
            where fp.isHome = true and i.order = 0
            """)
    List<FreePatternOnHome> findLimitedNumFreePattern(Pageable pageable);

    @Query("""
            select f
            from FreePattern f
            join f.colfreps colFrep
            join colFrep.collection c
            where c.user.id = :userId and f.id = :frepId
            """)
    Optional<FreePattern> findFrepInCollection(@Param("userId") String userId,
                                               @Param("frepId") String frepId);

    @EntityGraph(attributePaths = {"images", "category"})
    @Query("""
            select f
            from FreePattern f
            where f.id = :id
            """)
    Optional<FreePattern> getDetail(@Param("id") String id);

    @Query("""
            select new org.crochet.payload.response.FreePatternOnHome(fp.id, fp.name, fp.description, fp.author, fp.status, i.fileContent, u.name, u.imageUrl)
            from FreePattern fp
            left join fp.images i
            join User u on fp.createdBy = u.id
            where fp.id in :ids
                  and i.order = 0
            """)
    Page<FreePatternOnHome> getFreePatternOnHomeWithIds(@Param("ids") List<String> ids, Pageable pageable);

    @Query("select f.id from FreePattern f order by f.createdDate desc")
    List<String> getFreePatternIds(Pageable pageable);

    @Query("""
            select new org.crochet.payload.response.FreePatternOnHome(fp.id, fp.name, fp.description, fp.author, fp.status, i.fileContent, u.name, u.imageUrl)
            from FreePattern fp
            left join fp.images i
            join User u on fp.createdBy = u.id
            where u.id = :userId and i.order = 0
            """)
    List<FreePatternOnHome> getFrepsByCreateByWithUser(@Param("userId") String userId);

    @Query("""
            select new org.crochet.payload.response.FreePatternOnHome(fp.id, fp.name, fp.description, fp.author, fp.status, i.fileContent)
            from FreePattern fp
            left join fp.images i
            join fp.colfreps colFrep
            join colFrep.collection c
            where c.user.id = :userId and c.id = :colId and i.order = 0
            """)
    List<FreePatternOnHome> getFrepsByCollection(@Param("userId") String userId,
                                                 @Param("colId") String collectionId);

    @Transactional
    @Modifying
    @Query("delete from FreePattern f where f.id in :ids and f.createdBy = :userId")
    void deleteAllByIdAndCreatedBy(@Param("ids") List<String> ids,
                                   @Param("userId") String userId);
}