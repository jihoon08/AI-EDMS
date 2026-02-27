package com.edms.document.repository;

import com.edms.document.domain.DocumentVersion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface DocumentVersionRepository extends JpaRepository<DocumentVersion, UUID> {

    List<DocumentVersion> findByDocumentUuidOrderByVersionNumberDesc(UUID documentUuid);
}
