package org.crochet.repository;

import org.crochet.model.BlogPost;
import org.crochet.payload.response.BlogPostResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BlogPostRepository extends JpaRepository<BlogPost, String>, JpaSpecificationExecutor<BlogPost> {

    @Query("""
            SELECT
              new org.crochet.payload.response.BlogPostResponse (
                p.id,
                p.title,
                p.content,
                f.fileContent,
                p.createdDate
              )
            FROM
              BlogPost p
              JOIN p.files f WITH f.order = 0
            WHERE
              p.home = TRUE
            """)
    List<BlogPostResponse> findLimitedNumPosts(Pageable pageable);

    @Query("""
            SELECT
              p
            FROM
              BlogPost p
              LEFT JOIN FETCH p.files
            WHERE
              p.id = :id
            """)
    Optional<BlogPost> getDetail(String id);

    @Query("""
            SELECT
              new org.crochet.payload.response.BlogPostResponse (
                p.id,
                p.title,
                p.content,
                f.fileContent,
                p.createdDate
              )
            FROM
              BlogPost p
              JOIN p.files f WITH f.order = 0
            WHERE
              p.id IN :ids
            """)
    Page<BlogPostResponse> findBlogOnHomeWithIds(@Param("ids") List<String> ids, Pageable pageable);

    @Query("""
            SELECT
              p.id
            FROM
              BlogPost p
            ORDER BY
              p.createdDate DESC
            """)
    List<String> getBlogIds(Pageable pageable);
}
