package com.edms.document.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "t_core_document_version", schema = "edms")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DocumentVersion {

    @Id
    @Column(name = "version_uuid")
    private UUID versionUuid;

    @Column(name = "document_uuid", nullable = false)
    private UUID documentUuid;

    @Column(name = "version_number", nullable = false)
    private Integer versionNumber;

    @Column(name = "file_name", nullable = false, length = 500)
    private String fileName;

    @Column(name = "file_size", nullable = false)
    private Long fileSize;

    @Column(name = "content_type", nullable = false, length = 200)
    private String contentType;

    @Column(name = "storage_key", nullable = false, length = 1000)
    private String storageKey;

    @Column(name = "change_summary", length = 1000)
    private String changeSummary;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "created_by_uuid")
    private UUID createdByUuid;

    @PrePersist
    void prePersist() {
        if (this.versionUuid == null) this.versionUuid = UUID.randomUUID();
        if (this.createdAt == null) this.createdAt = Instant.now();
    }

    @Builder
    public DocumentVersion(UUID documentUuid, Integer versionNumber,
                           String fileName, Long fileSize, String contentType,
                           String storageKey, String changeSummary, UUID createdByUuid) {
        this.documentUuid = documentUuid;
        this.versionNumber = versionNumber;
        this.fileName = fileName;
        this.fileSize = fileSize;
        this.contentType = contentType;
        this.storageKey = storageKey;
        this.changeSummary = changeSummary;
        this.createdByUuid = createdByUuid;
    }
}
