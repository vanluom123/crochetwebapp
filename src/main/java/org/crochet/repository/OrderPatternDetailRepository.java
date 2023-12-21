package org.crochet.repository;

import org.crochet.model.OrderPatternDetail;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface OrderPatternDetailRepository extends JpaRepository<OrderPatternDetail, UUID> {
    Optional<OrderPatternDetail> findByTransactionId(String transactionId);
}