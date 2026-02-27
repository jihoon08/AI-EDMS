package com.edms.ai.repository;

import com.edms.ai.domain.AiSummary;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface AiSummaryRepository extends JpaRepository<AiSummary, UUID> {
    List<AiSummary> findByDocumentUuidOrderByCreatedAtDesc(UUID documentUuid);
}
