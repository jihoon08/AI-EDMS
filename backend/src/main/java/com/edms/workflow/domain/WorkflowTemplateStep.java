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
@Table(name = "t_core_workflow_template_step")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WorkflowTemplateStep {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "step_uuid")
    private UUID stepUuid;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "template_uuid", nullable = false)
    private WorkflowTemplate template;

    @Column(name = "step_order", nullable = false)
    private Integer stepOrder;

    @Column(name = "step_name", nullable = false, length = 100)
    private String stepName;

    // SEQUENTIAL, PARALLEL
    @Column(name = "step_type", nullable = false, length = 20)
    @Builder.Default
    private String stepType = "SEQUENTIAL";

    // SPECIFIC_USER, ROLE
    @Column(name = "approver_type", nullable = false, length = 20)
    private String approverType;

    @Column(name = "approver_uuid")
    private UUID approverUuid;

    @Column(name = "approver_role", length = 50)
    private String approverRole;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "created_by_uuid")
    private UUID createdByUuid;

    public void setTemplate(WorkflowTemplate template) {
        this.template = template;
    }
}
