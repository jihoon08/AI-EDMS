package com.edms.workflow.repository;

import com.edms.workflow.domain.WorkflowTemplate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface WorkflowTemplateRepository extends JpaRepository<WorkflowTemplate, UUID> {

    @Query("SELECT t FROM WorkflowTemplate t WHERE t.activeFlag = true ORDER BY t.createdAt DESC")
    List<WorkflowTemplate> findAllActive();

    @Query("SELECT t FROM WorkflowTemplate t WHERE t.activeFlag = true AND t.documentType = :documentType")
    List<WorkflowTemplate> findByDocumentType(@Param("documentType") String documentType);
}
