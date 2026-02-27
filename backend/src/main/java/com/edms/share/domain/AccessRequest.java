package com.edms.share.domain;

import com.edms.common.domain.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "t_core_access_request", schema = "edms")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AccessRequest extends BaseEntity {

    @Id
    @Column(name = "request_uuid")
    private UUID requestUuid;

    @Column(name = "target_type", nullable = false, length = 20)
    private String targetType;

    @Column(name = "target_uuid", nullable = false)
    private UUID targetUuid;

    @Column(name = "requester_uuid", nullable = false)
    private UUID requesterUuid;

    @Column(name = "owner_uuid", nullable = false)
    private UUID ownerUuid;

    @Column(name = "request_type", nullable = false, length = 20)
    private String requestType;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String reason;

    @Column(name = "request_days", nullable = false)
    private Integer requestDays;

    @Column(nullable = false, length = 20)
    private String status;

    @Column(name = "decided_at")
    private Instant decidedAt;

    @Column(name = "decided_by_uuid")
    private UUID decidedByUuid;

    @Column(name = "reject_reason", length = 500)
    private String rejectReason;

    @Column(name = "granted_until")
    private Instant grantedUntil;

    @PrePersist
    void prePersist() {
        if (this.requestUuid == null) this.requestUuid = UUID.randomUUID();
        if (this.status == null) this.status = "PENDING";
        if (this.requestDays == null) this.requestDays = 30;
    }

    @Builder
    public AccessRequest(String targetType, UUID targetUuid, UUID requesterUuid,
                         UUID ownerUuid, String requestType, String reason, Integer requestDays) {
        this.targetType = targetType;
        this.targetUuid = targetUuid;
        this.requesterUuid = requesterUuid;
        this.ownerUuid = ownerUuid;
        this.requestType = requestType;
        this.reason = reason;
        this.requestDays = requestDays != null ? requestDays : 30;
    }

    public void approve(UUID decidedByUuid) {
        this.status = "APPROVED";
        this.decidedAt = Instant.now();
        this.decidedByUuid = decidedByUuid;
        this.grantedUntil = Instant.now().plusSeconds((long) requestDays * 24 * 3600);
    }

    public void reject(UUID decidedByUuid, String rejectReason) {
        this.status = "REJECTED";
        this.decidedAt = Instant.now();
        this.decidedByUuid = decidedByUuid;
        this.rejectReason = rejectReason;
    }
}
