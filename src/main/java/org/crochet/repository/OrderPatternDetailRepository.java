package org.crochet.repository;

import org.crochet.model.OrderPatternDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;
import java.util.UUID;

public interface OrderPatternDetailRepository extends JpaRepository<OrderPatternDetail, UUID> {
    @Query("select o from OrderPatternDetail o where o.transactionId = ?1")
    Optional<OrderPatternDetail> findByTransactionId(String transactionId);
}