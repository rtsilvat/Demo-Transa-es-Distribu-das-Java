package com.banking.demo.infrastructure.persistence;

import com.banking.demo.application.port.OutboxRepository;
import com.banking.demo.domain.OutboxEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JpaOutboxRepository extends JpaRepository<OutboxEvent, Long>, OutboxRepository {

    @Query("SELECT o FROM OutboxEvent o WHERE o.publishedAt IS NULL ORDER BY o.createdAt ASC")
    List<OutboxEvent> findUnpublished(org.springframework.data.domain.Pageable pageable);

    @Override
    default List<OutboxEvent> findUnpublished(int limit) {
        return findUnpublished(org.springframework.data.domain.Pageable.ofSize(limit));
    }
}
