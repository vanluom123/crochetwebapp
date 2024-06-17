package org.crochet.repository;

import org.crochet.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, String>, JpaSpecificationExecutor<Product> {

    @Query("""
                select p from Product p
                where p.isHome = true
                order by p.createdDate desc
                limit ?1
            """)
    List<Product> findLimitedNumProductByCreatedDateDesc(int limited);
}