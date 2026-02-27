package com.edms.document.domain;

import com.edms.common.domain.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "t_core_document", schema = "edms")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Document extends BaseEntity {

    @Id
    @Column(name = "document_uuid")
    private UUID documentUuid;

    @Column(name = "document_number", nullable = false, unique = true)
    private String documentNumber;

    @Column(nullable = false, length = 500)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "document_type", nullable = false, length = 50)
    private String documentType;

    @Column(nullable = false, length = 20)
    private String status;

    @Column(name = "security_level", nullable = false, length = 20)
    private String securityLevel;

    @Column(name = "folder_uuid")
    private UUID folderUuid;

    @Column(name = "owner_uuid", nullable = false)
    private UUID ownerUuid;

    @Column(name = "current_version", nullable = false)
    private Integer currentVersion;

    @Column(name = "file_name", nullable = false, length = 500)
    private String fileName;

    @Column(name = "file_size", nullable = false)
    private Long fileSize;

    @Column(name = "content_type", nullable = false, length = 200)
    private String contentType;

    @Column(name = "storage_key", nullable = false, length = 1000)
    private String storageKey;

    @Column(name = "extracted_text", columnDefinition = "TEXT")
    private String extractedText;

    @Column(name = "retention_period", length = 20)
    private String retentionPeriod;

    @Column(name = "retention_expires_at")
    private Instant retentionExpiresAt;

    @Column(name = "deleted_flag", nullable = false)
    private Boolean deletedFlag;

    @Column(name = "deleted_at")
    private Instant deletedAt;

    @Version
    @Column(name = "version_seq", nullable = false)
    private Long versionSeq;

    @PrePersist
    void prePersist() {
        if (this.documentUuid == null) {
            this.documentUuid = UUID.randomUUID();
        }
        if (this.status == null) {
            this.status = "DRAFT";
        }
        if (this.securityLevel == null) {
            this.securityLevel = "INTERNAL";
        }
        if (this.currentVersion == null) {
            this.currentVersion = 1;
        }
        if (this.deletedFlag == null) {
            this.deletedFlag = false;
        }
    }

    @Builder
    public Document(String documentNumber, String title, String description,
                    String documentType, String securityLevel, UUID folderUuid,
                    UUID ownerUuid, String fileName, Long fileSize,
                    String contentType, String storageKey, String retentionPeriod) {
        this.documentNumber = documentNumber;
        this.title = title;
        this.description = description;
        this.documentType = documentType;
        this.securityLevel = securityLevel;
        this.folderUuid = folderUuid;
        this.ownerUuid = ownerUuid;
        this.fileName = fileName;
        this.fileSize = fileSize;
        this.contentType = contentType;
        this.storageKey = storageKey;
        this.retentionPeriod = retentionPeriod;
    }

    // 상태 전이 규칙
    public void changeStatus(String newStatus) {
        if (!isValidTransition(this.status, newStatus)) {
            throw new IllegalStateException(
                    String.format("상태 전이 불가: %s → %s", this.status, newStatus));
        }
        this.status = newStatus;
    }

    private boolean isValidTransition(String from, String to) {
        return switch (from) {
            case "DRAFT" -> "IN_REVIEW".equals(to) || "ARCHIVED".equals(to);
            case "IN_REVIEW" -> "APPROVED".equals(to) || "REJECTED".equals(to) || "DRAFT".equals(to);
            case "APPROVED" -> "PUBLISHED".equals(to) || "DRAFT".equals(to);
            case "REJECTED" -> "DRAFT".equals(to);
            case "PUBLISHED" -> "ARCHIVED".equals(to);
            default -> false;
        };
    }

    public void updateInfo(String title, String description, String documentType,
                           String securityLevel, UUID folderUuid) {
        this.title = title;
        if (description != null) this.description = description;
        if (documentType != null) this.documentType = documentType;
        if (securityLevel != null) this.securityLevel = securityLevel;
        this.folderUuid = folderUuid;
    }

    public void softDelete() {
        this.deletedFlag = true;
        this.deletedAt = Instant.now();
    }

    public void restore() {
        this.deletedFlag = false;
        this.deletedAt = null;
    }

    public void incrementVersion(String newFileName, Long newFileSize,
                                  String newContentType, String newStorageKey) {
        this.currentVersion++;
        this.fileName = newFileName;
        this.fileSize = newFileSize;
        this.contentType = newContentType;
        this.storageKey = newStorageKey;
    }

    public void setExtractedText(String text) {
        this.extractedText = text;
    }
}
