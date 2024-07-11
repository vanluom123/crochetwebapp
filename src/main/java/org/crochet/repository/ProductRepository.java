package org.crochet.repository;

import org.crochet.model.Product;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, String>, JpaSpecificationExecutor<Product> {

    @Query("""
            select p
            from Product p
            left join fetch p.images
            where p.isHome = true
            """)
    List<Product> findLimitedNumProduct(Pageable pageable);


    @Query("""
            select prod
            from Product prod
            join fetch prod.category c
            where c.id = ?1
            """)
    List<Product> findProductByCategory(String categoryId);

    @Query("""
            select p
            from Product p
            left join fetch p.images
            where p.id = ?1
            """)
    Optional<Product> getDetail(String id);
}