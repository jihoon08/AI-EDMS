package com.edms.workflow.controller;

import com.edms.common.dto.CommonApiResponse;
import com.edms.workflow.dto.WorkflowDto;
import com.edms.workflow.service.WorkflowService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/workflows")
@RequiredArgsConstructor
public class WorkflowController {

    private final WorkflowService workflowService;

    // ===== 결재 템플릿 =====

    @GetMapping("/templates")
    public ResponseEntity<CommonApiResponse<List<WorkflowDto.TemplateResponse>>> getTemplates() {
        return ResponseEntity.ok(CommonApiResponse.success(workflowService.getTemplates()));
    }

    @GetMapping("/templates/{uuid}")
    public ResponseEntity<CommonApiResponse<WorkflowDto.TemplateResponse>> getTemplate(@PathVariable UUID uuid) {
        return ResponseEntity.ok(CommonApiResponse.success(workflowService.getTemplate(uuid)));
    }

    @PostMapping("/templates")
    public ResponseEntity<CommonApiResponse<WorkflowDto.TemplateResponse>> createTemplate(
            @RequestBody WorkflowDto.CreateTemplateRequest request,
            Authentication auth) {
        UUID userUuid = (UUID) auth.getPrincipal();
        return ResponseEntity.ok(CommonApiResponse.success(workflowService.createTemplate(request, userUuid)));
    }

    // ===== 결재 =====

    @PostMapping("/approvals")
    public ResponseEntity<CommonApiResponse<WorkflowDto.ApprovalResponse>> createApproval(
            @RequestBody WorkflowDto.CreateApprovalRequest request,
            Authentication auth) {
        UUID userUuid = (UUID) auth.getPrincipal();
        return ResponseEntity.ok(CommonApiResponse.success(workflowService.createApproval(request, userUuid)));
    }

    @GetMapping("/approvals/{uuid}")
    public ResponseEntity<CommonApiResponse<WorkflowDto.ApprovalResponse>> getApproval(@PathVariable UUID uuid) {
        return ResponseEntity.ok(CommonApiResponse.success(workflowService.getApproval(uuid)));
    }

    @PostMapping("/approvals/{uuid}/approve")
    public ResponseEntity<CommonApiResponse<WorkflowDto.ApprovalResponse>> approve(
            @PathVariable UUID uuid,
            @RequestBody(required = false) WorkflowDto.ProcessRequest request,
            Authentication auth) {
        UUID userUuid = (UUID) auth.getPrincipal();
        return ResponseEntity.ok(CommonApiResponse.success(workflowService.approveStep(uuid, userUuid, request)));
    }

    @PostMapping("/approvals/{uuid}/reject")
    public ResponseEntity<CommonApiResponse<WorkflowDto.ApprovalResponse>> reject(
            @PathVariable UUID uuid,
            @RequestBody WorkflowDto.ProcessRequest request,
            Authentication auth) {
        UUID userUuid = (UUID) auth.getPrincipal();
        return ResponseEntity.ok(CommonApiResponse.success(workflowService.rejectStep(uuid, userUuid, request)));
    }

    @PostMapping("/approvals/{uuid}/cancel")
    public ResponseEntity<CommonApiResponse<WorkflowDto.ApprovalResponse>> cancel(
            @PathVariable UUID uuid,
            Authentication auth) {
        UUID userUuid = (UUID) auth.getPrincipal();
        return ResponseEntity.ok(CommonApiResponse.success(workflowService.cancelApproval(uuid, userUuid)));
    }

    // ===== 목록 =====

    @GetMapping("/my-requests")
    public ResponseEntity<CommonApiResponse<Page<WorkflowDto.ApprovalResponse>>> getMyRequests(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            Authentication auth) {
        UUID userUuid = (UUID) auth.getPrincipal();
        return ResponseEntity.ok(CommonApiResponse.success(workflowService.getMyRequests(userUuid, page, size)));
    }

    @GetMapping("/pending")
    public ResponseEntity<CommonApiResponse<Page<WorkflowDto.ApprovalResponse>>> getPending(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            Authentication auth) {
        UUID userUuid = (UUID) auth.getPrincipal();
        return ResponseEntity.ok(CommonApiResponse.success(workflowService.getPendingApprovals(userUuid, page, size)));
    }

    @GetMapping("/counts")
    public ResponseEntity<CommonApiResponse<WorkflowDto.CountResponse>> getCounts(Authentication auth) {
        UUID userUuid = (UUID) auth.getPrincipal();
        return ResponseEntity.ok(CommonApiResponse.success(workflowService.getCounts(userUuid)));
    }

    @GetMapping("/approvals/{uuid}/history")
    public ResponseEntity<CommonApiResponse<List<WorkflowDto.ApprovalHistoryResponse>>> getHistory(
            @PathVariable UUID uuid) {
        return ResponseEntity.ok(CommonApiResponse.success(workflowService.getHistory(uuid)));
    }

    // ===== 대리결재 =====

    @PostMapping("/delegations")
    public ResponseEntity<CommonApiResponse<WorkflowDto.DelegationResponse>> createDelegation(
            @RequestBody WorkflowDto.CreateDelegationRequest request,
            Authentication auth) {
        UUID userUuid = (UUID) auth.getPrincipal();
        return ResponseEntity.ok(CommonApiResponse.success(workflowService.createDelegation(request, userUuid)));
    }

    @GetMapping("/delegations")
    public ResponseEntity<CommonApiResponse<List<WorkflowDto.DelegationResponse>>> getMyDelegations(
            Authentication auth) {
        UUID userUuid = (UUID) auth.getPrincipal();
        return ResponseEntity.ok(CommonApiResponse.success(workflowService.getMyDelegations(userUuid)));
    }

    @DeleteMapping("/delegations/{uuid}")
    public ResponseEntity<CommonApiResponse<Void>> cancelDelegation(
            @PathVariable UUID uuid,
            Authentication auth) {
        UUID userUuid = (UUID) auth.getPrincipal();
        workflowService.cancelDelegation(uuid, userUuid);
        return ResponseEntity.ok(CommonApiResponse.success(null));
    }
}
