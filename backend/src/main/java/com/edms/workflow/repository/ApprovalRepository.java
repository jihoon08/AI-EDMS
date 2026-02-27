package com.edms.workflow.repository;

import com.edms.workflow.domain.Approval;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface ApprovalRepository extends JpaRepository<Approval, UUID> {

    @Query("SELECT a FROM Approval a LEFT JOIN FETCH a.steps WHERE a.approvalUuid = :uuid")
    Optional<Approval> findByIdWithSteps(@Param("uuid") UUID uuid);

    // 내 결재 요청 목록
    Page<Approval> findByRequesterUuidOrderByCreatedAtDesc(UUID requesterUuid, Pageable pageable);

    // 내가 결재해야 할 대기 목록
    @Query("SELECT DISTINCT a FROM Approval a JOIN a.steps s " +
            "WHERE (s.approverUuid = :approverUuid OR s.delegateUuid = :approverUuid) " +
            "AND s.status = 'PENDING' AND s.stepOrder = a.currentStep " +
            "AND a.status IN ('PENDING', 'IN_PROGRESS') " +
            "ORDER BY a.createdAt DESC")
    Page<Approval> findPendingByApprover(@Param("approverUuid") UUID approverUuid, Pageable pageable);

    // 문서별 결재 조회
    @Query("SELECT a FROM Approval a WHERE a.documentUuid = :documentUuid ORDER BY a.createdAt DESC")
    Page<Approval> findByDocumentUuid(@Param("documentUuid") UUID documentUuid, Pageable pageable);

    // 카운트
    long countByRequesterUuid(UUID requesterUuid);

    @Query("SELECT COUNT(DISTINCT a) FROM Approval a JOIN a.steps s " +
            "WHERE (s.approverUuid = :approverUuid OR s.delegateUuid = :approverUuid) " +
            "AND s.status = 'PENDING' AND s.stepOrder = a.currentStep " +
            "AND a.status IN ('PENDING', 'IN_PROGRESS')")
    long countPendingByApprover(@Param("approverUuid") UUID approverUuid);

    long countByRequesterUuidAndStatus(UUID requesterUuid, String status);
}
