package com.edms.workflow.domain;

import com.edms.common.domain.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "t_core_approval_delegation")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApprovalDelegation extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "delegation_uuid")
    private UUID delegationUuid;

    @Column(name = "delegator_uuid", nullable = false)
    private UUID delegatorUuid;

    @Column(name = "delegate_uuid", nullable = false)
    private UUID delegateUuid;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

    @Column(name = "reason", length = 500)
    private String reason;

    @Column(name = "active_flag", nullable = false)
    @Builder.Default
    private Boolean activeFlag = true;

    public boolean isActive() {
        if (!activeFlag) return false;
        LocalDate today = LocalDate.now();
        return !today.isBefore(startDate) && !today.isAfter(endDate);
    }

    public void deactivate() {
        this.activeFlag = false;
    }
}
