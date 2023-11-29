package org.crochet.repository;

import org.crochet.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long>, JpaSpecificationExecutor<Product> {
    @Query("select p from Product p join fetch p.productImages where p.id = ?1")
    Optional<Product> findById(Long id);

    @Query("select p from Product p join fetch p.productImages")
    Page<Product> findAll(Specification<Product> spec, Pageable pageable);
}