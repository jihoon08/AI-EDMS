package com.edms.workflow.service;

import com.edms.auth.domain.User;
import com.edms.auth.repository.UserRepository;
import com.edms.common.exception.BusinessException;
import com.edms.common.exception.ErrorCode;
import com.edms.workflow.domain.*;
import com.edms.workflow.dto.WorkflowDto;
import com.edms.workflow.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class WorkflowService {

    private final WorkflowTemplateRepository templateRepository;
    private final ApprovalRepository approvalRepository;
    private final ApprovalHistoryRepository historyRepository;
    private final ApprovalDelegationRepository delegationRepository;
    private final UserRepository userRepository;

    // ===== 결재 템플릿 =====

    @Transactional(readOnly = true)
    public List<WorkflowDto.TemplateResponse> getTemplates() {
        return templateRepository.findAllActive().stream()
                .map(WorkflowDto.TemplateResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public WorkflowDto.TemplateResponse getTemplate(UUID templateUuid) {
        WorkflowTemplate template = templateRepository.findById(templateUuid)
                .orElseThrow(() -> new BusinessException(ErrorCode.COMMON_NOT_FOUND, "결재 템플릿을 찾을 수 없습니다"));
        return WorkflowDto.TemplateResponse.from(template);
    }

    @Transactional
    public WorkflowDto.TemplateResponse createTemplate(WorkflowDto.CreateTemplateRequest request, UUID createdByUuid) {
        WorkflowTemplate template = WorkflowTemplate.builder()
                .templateName(request.getTemplateName())
                .description(request.getDescription())
                .documentType(request.getDocumentType())
                .build();
        template.setCreatedByUuid(createdByUuid);

        if (request.getSteps() != null) {
            for (WorkflowDto.TemplateStepRequest stepReq : request.getSteps()) {
                WorkflowTemplateStep step = WorkflowTemplateStep.builder()
                        .stepOrder(stepReq.getStepOrder())
                        .stepName(stepReq.getStepName())
                        .stepType(stepReq.getStepType() != null ? stepReq.getStepType() : "SEQUENTIAL")
                        .approverType(stepReq.getApproverType())
                        .approverUuid(stepReq.getApproverUuid())
                        .approverRole(stepReq.getApproverRole())
                        .createdByUuid(createdByUuid)
                        .build();
                template.addStep(step);
            }
        }

        WorkflowTemplate saved = templateRepository.save(template);
        log.info("결재 템플릿 생성: uuid={}, name={}", saved.getTemplateUuid(), saved.getTemplateName());
        return WorkflowDto.TemplateResponse.from(saved);
    }

    // ===== 결재 요청/승인/반려 =====

    @Transactional
    public WorkflowDto.ApprovalResponse createApproval(WorkflowDto.CreateApprovalRequest request, UUID requesterUuid) {
        Approval approval = Approval.builder()
                .documentUuid(request.getDocumentUuid())
                .templateUuid(request.getTemplateUuid())
                .requesterUuid(requesterUuid)
                .title(request.getTitle())
                .description(request.getDescription())
                .totalSteps(request.getApprovers().size())
                .build();
        approval.setCreatedByUuid(requesterUuid);

        for (WorkflowDto.ApproverRequest approverReq : request.getApprovers()) {
            // 대리결재 확인
            UUID actualApproverUuid = approverReq.getApproverUuid();
            UUID delegateUuid = null;
            ApprovalDelegation delegation = delegationRepository
                    .findActiveDelegation(actualApproverUuid, LocalDate.now())
                    .orElse(null);
            if (delegation != null) {
                delegateUuid = delegation.getDelegateUuid();
            }

            ApprovalStep step = ApprovalStep.builder()
                    .stepOrder(approverReq.getStepOrder())
                    .stepName(approverReq.getStepName())
                    .approverUuid(actualApproverUuid)
                    .delegateUuid(delegateUuid)
                    .build();
            approval.addStep(step);
        }

        Approval saved = approvalRepository.save(approval);

        // 이력 기록
        recordHistory(saved.getApprovalUuid(), null, "REQUEST", requesterUuid, null);

        log.info("결재 요청 생성: uuid={}, document={}, requester={}",
                saved.getApprovalUuid(), saved.getDocumentUuid(), requesterUuid);

        String requesterName = getUserName(requesterUuid);
        return WorkflowDto.ApprovalResponse.from(saved, requesterName);
    }

    @Transactional
    public WorkflowDto.ApprovalResponse approveStep(UUID approvalUuid, UUID approverUuid, WorkflowDto.ProcessRequest request) {
        Approval approval = getApprovalWithSteps(approvalUuid);
        int stepOrder = approval.getCurrentStep();
        approval.approve(approverUuid, request != null ? request.getComment() : null);

        recordHistory(approvalUuid, stepOrder, "APPROVE", approverUuid,
                request != null ? request.getComment() : null);

        Approval saved = approvalRepository.save(approval);
        log.info("결재 승인: uuid={}, step={}, approver={}, status={}",
                approvalUuid, stepOrder, approverUuid, saved.getStatus());

        String requesterName = getUserName(saved.getRequesterUuid());
        return WorkflowDto.ApprovalResponse.from(saved, requesterName);
    }

    @Transactional
    public WorkflowDto.ApprovalResponse rejectStep(UUID approvalUuid, UUID approverUuid, WorkflowDto.ProcessRequest request) {
        if (request == null || request.getComment() == null || request.getComment().isBlank()) {
            throw new BusinessException(ErrorCode.INVALID_INPUT, "반려 사유를 입력해주세요");
        }

        Approval approval = getApprovalWithSteps(approvalUuid);
        int stepOrder = approval.getCurrentStep();
        approval.reject(approverUuid, request.getComment());

        recordHistory(approvalUuid, stepOrder, "REJECT", approverUuid, request.getComment());

        Approval saved = approvalRepository.save(approval);
        log.info("결재 반려: uuid={}, step={}, approver={}", approvalUuid, stepOrder, approverUuid);

        String requesterName = getUserName(saved.getRequesterUuid());
        return WorkflowDto.ApprovalResponse.from(saved, requesterName);
    }

    @Transactional
    public WorkflowDto.ApprovalResponse cancelApproval(UUID approvalUuid, UUID requesterUuid) {
        Approval approval = getApprovalWithSteps(approvalUuid);
        approval.cancel(requesterUuid);

        recordHistory(approvalUuid, null, "CANCEL", requesterUuid, null);

        Approval saved = approvalRepository.save(approval);
        log.info("결재 취소: uuid={}, requester={}", approvalUuid, requesterUuid);

        String requesterName = getUserName(saved.getRequesterUuid());
        return WorkflowDto.ApprovalResponse.from(saved, requesterName);
    }

    // ===== 조회 =====

    @Transactional(readOnly = true)
    public WorkflowDto.ApprovalResponse getApproval(UUID approvalUuid) {
        Approval approval = getApprovalWithSteps(approvalUuid);
        String requesterName = getUserName(approval.getRequesterUuid());
        return WorkflowDto.ApprovalResponse.from(approval, requesterName);
    }

    @Transactional(readOnly = true)
    public Page<WorkflowDto.ApprovalResponse> getMyRequests(UUID requesterUuid, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return approvalRepository.findByRequesterUuidOrderByCreatedAtDesc(requesterUuid, pageable)
                .map(a -> WorkflowDto.ApprovalResponse.from(a, getUserName(a.getRequesterUuid())));
    }

    @Transactional(readOnly = true)
    public Page<WorkflowDto.ApprovalResponse> getPendingApprovals(UUID approverUuid, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return approvalRepository.findPendingByApprover(approverUuid, pageable)
                .map(a -> WorkflowDto.ApprovalResponse.from(a, getUserName(a.getRequesterUuid())));
    }

    @Transactional(readOnly = true)
    public WorkflowDto.CountResponse getCounts(UUID userUuid) {
        long myRequests = approvalRepository.countByRequesterUuid(userUuid);
        long pending = approvalRepository.countPendingByApprover(userUuid);
        long approved = approvalRepository.countByRequesterUuidAndStatus(userUuid, "APPROVED");
        long rejected = approvalRepository.countByRequesterUuidAndStatus(userUuid, "REJECTED");

        return WorkflowDto.CountResponse.builder()
                .myRequests(myRequests)
                .pendingApprovals(pending)
                .approved(approved)
                .rejected(rejected)
                .build();
    }

    @Transactional(readOnly = true)
    public List<WorkflowDto.ApprovalHistoryResponse> getHistory(UUID approvalUuid) {
        return historyRepository.findByApprovalUuidOrderByCreatedAtAsc(approvalUuid).stream()
                .map(WorkflowDto.ApprovalHistoryResponse::from)
                .toList();
    }

    // ===== 대리결재 =====

    @Transactional
    public WorkflowDto.DelegationResponse createDelegation(WorkflowDto.CreateDelegationRequest request, UUID delegatorUuid) {
        ApprovalDelegation delegation = ApprovalDelegation.builder()
                .delegatorUuid(delegatorUuid)
                .delegateUuid(request.getDelegateUuid())
                .startDate(LocalDate.parse(request.getStartDate()))
                .endDate(LocalDate.parse(request.getEndDate()))
                .reason(request.getReason())
                .build();
        delegation.setCreatedByUuid(delegatorUuid);

        ApprovalDelegation saved = delegationRepository.save(delegation);
        log.info("대리결재 위임: delegator={}, delegate={}, period={}/{}",
                delegatorUuid, request.getDelegateUuid(), request.getStartDate(), request.getEndDate());
        return WorkflowDto.DelegationResponse.from(saved);
    }

    @Transactional(readOnly = true)
    public List<WorkflowDto.DelegationResponse> getMyDelegations(UUID delegatorUuid) {
        return delegationRepository.findByDelegatorUuidOrderByCreatedAtDesc(delegatorUuid).stream()
                .map(WorkflowDto.DelegationResponse::from)
                .toList();
    }

    @Transactional
    public void cancelDelegation(UUID delegationUuid, UUID delegatorUuid) {
        ApprovalDelegation delegation = delegationRepository.findById(delegationUuid)
                .orElseThrow(() -> new BusinessException(ErrorCode.COMMON_NOT_FOUND, "위임을 찾을 수 없습니다"));
        if (!delegation.getDelegatorUuid().equals(delegatorUuid)) {
            throw new BusinessException(ErrorCode.AUTH_ACCESS_DENIED, "본인의 위임만 취소할 수 있습니다");
        }
        delegation.deactivate();
        delegationRepository.save(delegation);
    }

    // ===== private =====

    private Approval getApprovalWithSteps(UUID approvalUuid) {
        return approvalRepository.findByIdWithSteps(approvalUuid)
                .orElseThrow(() -> new BusinessException(ErrorCode.COMMON_NOT_FOUND, "결재를 찾을 수 없습니다"));
    }

    private void recordHistory(UUID approvalUuid, Integer stepOrder, String action, UUID actorUuid, String comment) {
        ApprovalHistory history = ApprovalHistory.builder()
                .approvalUuid(approvalUuid)
                .stepOrder(stepOrder)
                .action(action)
                .actorUuid(actorUuid)
                .comment(comment)
                .build();
        historyRepository.save(history);
    }

    private String getUserName(UUID userUuid) {
        return userRepository.findById(userUuid)
                .map(User::getName)
                .orElse("알 수 없음");
    }
}
