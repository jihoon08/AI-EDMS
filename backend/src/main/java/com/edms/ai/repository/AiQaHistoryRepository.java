package com.edms.ai.repository;

import com.edms.ai.domain.AiQaHistory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface AiQaHistoryRepository extends JpaRepository<AiQaHistory, UUID> {
    Page<AiQaHistory> findByUserUuidOrderByCreatedAtDesc(UUID userUuid, Pageable pageable);
}
