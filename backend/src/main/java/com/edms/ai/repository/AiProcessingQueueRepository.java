package com.edms.ai.repository;

import com.edms.ai.domain.AiProcessingQueue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface AiProcessingQueueRepository extends JpaRepository<AiProcessingQueue, UUID> {

    @Query("SELECT q FROM AiProcessingQueue q WHERE q.status = 'PENDING' ORDER BY q.priority DESC, q.createdAt ASC")
    List<AiProcessingQueue> findPendingTasks();

    List<AiProcessingQueue> findByDocumentUuidOrderByCreatedAtDesc(UUID documentUuid);

    long countByStatus(String status);
}
