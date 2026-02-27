package com.edms.share.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "t_core_document_share", schema = "edms")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DocumentShare {

    @Id
    @Column(name = "share_uuid")
    private UUID shareUuid;

    @Column(name = "document_uuid", nullable = false)
    private UUID documentUuid;

    @Column(name = "shared_with_uuid", nullable = false)
    private UUID sharedWithUuid;

    @Column(name = "permission_level", nullable = false, length = 20)
    private String permissionLevel;

    @Column(length = 500)
    private String message;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "created_by_uuid")
    private UUID createdByUuid;

    @PrePersist
    void prePersist() {
        if (this.shareUuid == null) this.shareUuid = UUID.randomUUID();
        if (this.createdAt == null) this.createdAt = Instant.now();
        if (this.permissionLevel == null) this.permissionLevel = "READ";
    }

    @Builder
    public DocumentShare(UUID documentUuid, UUID sharedWithUuid,
                         String permissionLevel, String message, UUID createdByUuid) {
        this.documentUuid = documentUuid;
        this.sharedWithUuid = sharedWithUuid;
        this.permissionLevel = permissionLevel != null ? permissionLevel : "READ";
        this.message = message;
        this.createdByUuid = createdByUuid;
    }
}
