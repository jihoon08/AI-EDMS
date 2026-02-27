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
@Table(name = "t_core_approval_history")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApprovalHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "history_uuid")
    private UUID historyUuid;

    @Column(name = "approval_uuid", nullable = false)
    private UUID approvalUuid;

    @Column(name = "step_order")
    private Integer stepOrder;

    // REQUEST, APPROVE, REJECT, CANCEL
    @Column(name = "action", nullable = false, length = 20)
    private String action;

    @Column(name = "actor_uuid", nullable = false)
    private UUID actorUuid;

    @Column(name = "comment", columnDefinition = "TEXT")
    private String comment;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;
}
