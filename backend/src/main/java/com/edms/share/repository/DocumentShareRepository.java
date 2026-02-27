package com.edms.share.repository;

import com.edms.share.domain.DocumentShare;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface DocumentShareRepository extends JpaRepository<DocumentShare, UUID> {

    List<DocumentShare> findByDocumentUuid(UUID documentUuid);

    List<DocumentShare> findBySharedWithUuid(UUID sharedWithUuid);

    boolean existsByDocumentUuidAndSharedWithUuid(UUID documentUuid, UUID sharedWithUuid);
}
