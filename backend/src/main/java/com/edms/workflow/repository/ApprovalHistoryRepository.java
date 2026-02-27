package com.edms.workflow.repository;

import com.edms.workflow.domain.ApprovalHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ApprovalHistoryRepository extends JpaRepository<ApprovalHistory, UUID> {

    List<ApprovalHistory> findByApprovalUuidOrderByCreatedAtAsc(UUID approvalUuid);
}
