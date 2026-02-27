package com.edms.workflow.domain;

import com.edms.common.domain.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "t_core_workflow_template")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WorkflowTemplate extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "template_uuid")
    private UUID templateUuid;

    @Column(name = "template_name", nullable = false, length = 200)
    private String templateName;

    @Column(name = "description", length = 500)
    private String description;

    @Column(name = "document_type", length = 50)
    private String documentType;

    @Column(name = "active_flag", nullable = false)
    @Builder.Default
    private Boolean activeFlag = true;

    @Version
    @Column(name = "version_seq", nullable = false)
    @Builder.Default
    private Long versionSeq = 0L;

    @OneToMany(mappedBy = "template", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @OrderBy("stepOrder ASC")
    @Builder.Default
    private List<WorkflowTemplateStep> steps = new ArrayList<>();

    public void addStep(WorkflowTemplateStep step) {
        steps.add(step);
        step.setTemplate(this);
    }

    public void update(String templateName, String description, String documentType) {
        this.templateName = templateName;
        this.description = description;
        this.documentType = documentType;
    }

    public void deactivate() {
        this.activeFlag = false;
    }
}
