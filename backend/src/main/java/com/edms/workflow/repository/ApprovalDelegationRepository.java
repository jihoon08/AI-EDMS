package com.edms.workflow.repository;

import com.edms.workflow.domain.ApprovalDelegation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ApprovalDelegationRepository extends JpaRepository<ApprovalDelegation, UUID> {

    @Query("SELECT d FROM ApprovalDelegation d WHERE d.delegatorUuid = :delegatorUuid " +
            "AND d.activeFlag = true AND d.startDate <= :today AND d.endDate >= :today")
    Optional<ApprovalDelegation> findActiveDelegation(
            @Param("delegatorUuid") UUID delegatorUuid,
            @Param("today") LocalDate today);

    List<ApprovalDelegation> findByDelegatorUuidOrderByCreatedAtDesc(UUID delegatorUuid);

    List<ApprovalDelegation> findByDelegateUuidAndActiveFlagTrueOrderByCreatedAtDesc(UUID delegateUuid);
}
