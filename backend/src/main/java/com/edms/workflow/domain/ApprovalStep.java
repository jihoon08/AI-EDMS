package com.edms.workflow.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "t_core_approval_step")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApprovalStep {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "step_uuid")
    private UUID stepUuid;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "approval_uuid", nullable = false)
    private Approval approval;

    @Column(name = "step_order", nullable = false)
    private Integer stepOrder;

    @Column(name = "step_name", nullable = false, length = 100)
    private String stepName;

    @Column(name = "step_type", nullable = false, length = 20)
    @Builder.Default
    private String stepType = "SEQUENTIAL";

    @Column(name = "approver_uuid", nullable = false)
    private UUID approverUuid;

    @Column(name = "delegate_uuid")
    private UUID delegateUuid;

    @Column(name = "status", nullable = false, length = 20)
    @Builder.Default
    private String status = "PENDING";

    @Column(name = "comment", columnDefinition = "TEXT")
    private String comment;

    @Column(name = "decided_at")
    private Instant decidedAt;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    public void setApproval(Approval approval) {
        this.approval = approval;
    }

    public void approve(String comment) {
        if (!"PENDING".equals(this.status)) {
            throw new IllegalStateException("대기 중인 단계만 승인할 수 있습니다");
        }
        this.status = "APPROVED";
        this.comment = comment;
        this.decidedAt = Instant.now();
    }

    public void reject(String comment) {
        if (!"PENDING".equals(this.status)) {
            throw new IllegalStateException("대기 중인 단계만 반려할 수 있습니다");
        }
        this.status = "REJECTED";
        this.comment = comment;
        this.decidedAt = Instant.now();
    }

    public void setDelegateUuid(UUID delegateUuid) {
        this.delegateUuid = delegateUuid;
    }
}
