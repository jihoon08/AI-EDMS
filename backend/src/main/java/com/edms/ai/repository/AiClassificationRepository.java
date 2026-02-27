package com.edms.ai.repository;

import com.edms.ai.domain.AiClassification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface AiClassificationRepository extends JpaRepository<AiClassification, UUID> {
    List<AiClassification> findByDocumentUuidOrderByCreatedAtDesc(UUID documentUuid);
}
