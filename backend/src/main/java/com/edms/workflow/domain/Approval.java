package com.edms.workflow.domain;

import com.edms.common.domain.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "t_core_approval")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Approval extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "approval_uuid")
    private UUID approvalUuid;

    @Column(name = "document_uuid", nullable = false)
    private UUID documentUuid;

    @Column(name = "template_uuid")
    private UUID templateUuid;

    @Column(name = "requester_uuid", nullable = false)
    private UUID requesterUuid;

    @Column(name = "status", nullable = false, length = 20)
    @Builder.Default
    private String status = "PENDING";

    @Column(name = "title", nullable = false, length = 500)
    private String title;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "current_step", nullable = false)
    @Builder.Default
    private Integer currentStep = 1;

    @Column(name = "total_steps", nullable = false)
    private Integer totalSteps;

    @Column(name = "completed_at")
    private Instant completedAt;

    @Column(name = "cancelled_at")
    private Instant cancelledAt;

    @Version
    @Column(name = "version_seq", nullable = false)
    @Builder.Default
    private Long versionSeq = 0L;

    @OneToMany(mappedBy = "approval", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @OrderBy("stepOrder ASC")
    @Builder.Default
    private List<ApprovalStep> steps = new ArrayList<>();

    public void addStep(ApprovalStep step) {
        steps.add(step);
        step.setApproval(this);
    }

    // 승인 처리
    public void approve(UUID approverUuid, String comment) {
        validatePendingOrInProgress();
        ApprovalStep step = getCurrentStepEntity();
        validateApprover(step, approverUuid);

        step.approve(comment);

        if (currentStep >= totalSteps) {
            this.status = "APPROVED";
            this.completedAt = Instant.now();
        } else {
            this.currentStep++;
            this.status = "IN_PROGRESS";
        }
    }

    // 반려 처리
    public void reject(UUID approverUuid, String comment) {
        validatePendingOrInProgress();
        ApprovalStep step = getCurrentStepEntity();
        validateApprover(step, approverUuid);

        step.reject(comment);
        this.status = "REJECTED";
        this.completedAt = Instant.now();
    }

    // 취소 처리 (요청자만 가능)
    public void cancel(UUID requesterUuid) {
        if (!this.requesterUuid.equals(requesterUuid)) {
            throw new IllegalArgumentException("요청자만 결재를 취소할 수 있습니다");
        }
        if ("APPROVED".equals(status) || "REJECTED".equals(status) || "CANCELLED".equals(status)) {
            throw new IllegalStateException("이미 완료된 결재는 취소할 수 없습니다");
        }
        this.status = "CANCELLED";
        this.cancelledAt = Instant.now();
    }

    private void validatePendingOrInProgress() {
        if (!"PENDING".equals(status) && !"IN_PROGRESS".equals(status)) {
            throw new IllegalStateException("대기 또는 진행 중인 결재만 처리할 수 있습니다");
        }
    }

    private ApprovalStep getCurrentStepEntity() {
        return steps.stream()
                .filter(s -> s.getStepOrder().equals(currentStep))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("현재 결재 단계를 찾을 수 없습니다"));
    }

    private void validateApprover(ApprovalStep step, UUID approverUuid) {
        // 지정 결재자 또는 대리결재자
        boolean isApprover = step.getApproverUuid().equals(approverUuid);
        boolean isDelegate = step.getDelegateUuid() != null && step.getDelegateUuid().equals(approverUuid);
        if (!isApprover && !isDelegate) {
            throw new IllegalArgumentException("지정된 결재자만 처리할 수 있습니다");
        }
    }
}
