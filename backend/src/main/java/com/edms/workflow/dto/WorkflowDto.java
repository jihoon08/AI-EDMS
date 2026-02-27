package com.edms.workflow.dto;

import com.edms.workflow.domain.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

public class WorkflowDto {

    // ===== 결재 템플릿 =====

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CreateTemplateRequest {
        private String templateName;
        private String description;
        private String documentType;
        private List<TemplateStepRequest> steps;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TemplateStepRequest {
        private Integer stepOrder;
        private String stepName;
        private String stepType;
        private String approverType;
        private UUID approverUuid;
        private String approverRole;
    }

    @Getter
    @Builder
    public static class TemplateResponse {
        private UUID templateUuid;
        private String templateName;
        private String description;
        private String documentType;
        private Boolean activeFlag;
        private List<TemplateStepResponse> steps;
        private String createdAt;

        public static TemplateResponse from(WorkflowTemplate t) {
            return TemplateResponse.builder()
                    .templateUuid(t.getTemplateUuid())
                    .templateName(t.getTemplateName())
                    .description(t.getDescription())
                    .documentType(t.getDocumentType())
                    .activeFlag(t.getActiveFlag())
                    .steps(t.getSteps().stream().map(TemplateStepResponse::from).toList())
                    .createdAt(t.getCreatedAt() != null ? t.getCreatedAt().toString() : null)
                    .build();
        }
    }

    @Getter
    @Builder
    public static class TemplateStepResponse {
        private UUID stepUuid;
        private Integer stepOrder;
        private String stepName;
        private String stepType;
        private String approverType;
        private UUID approverUuid;
        private String approverRole;

        public static TemplateStepResponse from(WorkflowTemplateStep s) {
            return TemplateStepResponse.builder()
                    .stepUuid(s.getStepUuid())
                    .stepOrder(s.getStepOrder())
                    .stepName(s.getStepName())
                    .stepType(s.getStepType())
                    .approverType(s.getApproverType())
                    .approverUuid(s.getApproverUuid())
                    .approverRole(s.getApproverRole())
                    .build();
        }
    }

    // ===== 결재 요청 =====

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CreateApprovalRequest {
        private UUID documentUuid;
        private UUID templateUuid;
        private String title;
        private String description;
        private List<ApproverRequest> approvers;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ApproverRequest {
        private Integer stepOrder;
        private String stepName;
        private UUID approverUuid;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProcessRequest {
        private String comment;
    }

    @Getter
    @Builder
    public static class ApprovalResponse {
        private UUID approvalUuid;
        private UUID documentUuid;
        private UUID templateUuid;
        private UUID requesterUuid;
        private String requesterName;
        private String status;
        private String title;
        private String description;
        private Integer currentStep;
        private Integer totalSteps;
        private String completedAt;
        private String cancelledAt;
        private List<ApprovalStepResponse> steps;
        private String createdAt;

        public static ApprovalResponse from(Approval a, String requesterName) {
            return ApprovalResponse.builder()
                    .approvalUuid(a.getApprovalUuid())
                    .documentUuid(a.getDocumentUuid())
                    .templateUuid(a.getTemplateUuid())
                    .requesterUuid(a.getRequesterUuid())
                    .requesterName(requesterName)
                    .status(a.getStatus())
                    .title(a.getTitle())
                    .description(a.getDescription())
                    .currentStep(a.getCurrentStep())
                    .totalSteps(a.getTotalSteps())
                    .completedAt(a.getCompletedAt() != null ? a.getCompletedAt().toString() : null)
                    .cancelledAt(a.getCancelledAt() != null ? a.getCancelledAt().toString() : null)
                    .steps(a.getSteps().stream().map(ApprovalStepResponse::from).toList())
                    .createdAt(a.getCreatedAt() != null ? a.getCreatedAt().toString() : null)
                    .build();
        }
    }

    @Getter
    @Builder
    public static class ApprovalStepResponse {
        private UUID stepUuid;
        private Integer stepOrder;
        private String stepName;
        private String stepType;
        private UUID approverUuid;
        private UUID delegateUuid;
        private String status;
        private String comment;
        private String decidedAt;

        public static ApprovalStepResponse from(ApprovalStep s) {
            return ApprovalStepResponse.builder()
                    .stepUuid(s.getStepUuid())
                    .stepOrder(s.getStepOrder())
                    .stepName(s.getStepName())
                    .stepType(s.getStepType())
                    .approverUuid(s.getApproverUuid())
                    .delegateUuid(s.getDelegateUuid())
                    .status(s.getStatus())
                    .comment(s.getComment())
                    .decidedAt(s.getDecidedAt() != null ? s.getDecidedAt().toString() : null)
                    .build();
        }
    }

    @Getter
    @Builder
    public static class ApprovalHistoryResponse {
        private UUID historyUuid;
        private UUID approvalUuid;
        private Integer stepOrder;
        private String action;
        private UUID actorUuid;
        private String comment;
        private String createdAt;

        public static ApprovalHistoryResponse from(ApprovalHistory h) {
            return ApprovalHistoryResponse.builder()
                    .historyUuid(h.getHistoryUuid())
                    .approvalUuid(h.getApprovalUuid())
                    .stepOrder(h.getStepOrder())
                    .action(h.getAction())
                    .actorUuid(h.getActorUuid())
                    .comment(h.getComment())
                    .createdAt(h.getCreatedAt() != null ? h.getCreatedAt().toString() : null)
                    .build();
        }
    }

    // ===== 카운트 =====

    @Getter
    @Builder
    public static class CountResponse {
        private long myRequests;
        private long pendingApprovals;
        private long approved;
        private long rejected;
    }

    // ===== 대리결재 =====

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CreateDelegationRequest {
        private UUID delegateUuid;
        private String startDate;
        private String endDate;
        private String reason;
    }

    @Getter
    @Builder
    public static class DelegationResponse {
        private UUID delegationUuid;
        private UUID delegatorUuid;
        private UUID delegateUuid;
        private String startDate;
        private String endDate;
        private String reason;
        private Boolean activeFlag;
        private String createdAt;

        public static DelegationResponse from(ApprovalDelegation d) {
            return DelegationResponse.builder()
                    .delegationUuid(d.getDelegationUuid())
                    .delegatorUuid(d.getDelegatorUuid())
                    .delegateUuid(d.getDelegateUuid())
                    .startDate(d.getStartDate().toString())
                    .endDate(d.getEndDate().toString())
                    .reason(d.getReason())
                    .activeFlag(d.getActiveFlag())
                    .createdAt(d.getCreatedAt() != null ? d.getCreatedAt().toString() : null)
                    .build();
        }
    }
}
