package com.edms.ai.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "t_core_ai_processing_queue")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AiProcessingQueue {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "queue_uuid")
    private UUID queueUuid;

    @Column(name = "document_uuid", nullable = false)
    private UUID documentUuid;

    // CLASSIFICATION, SUMMARY, OCR, EMBEDDING, TAGGING
    @Column(name = "task_type", nullable = false, length = 30)
    private String taskType;

    // PENDING, PROCESSING, COMPLETED, FAILED
    @Column(name = "status", nullable = false, length = 20)
    @Builder.Default
    private String status = "PENDING";

    @Column(name = "priority", nullable = false)
    @Builder.Default
    private Integer priority = 5;

    @Column(name = "retry_count", nullable = false)
    @Builder.Default
    private Integer retryCount = 0;

    @Column(name = "max_retries", nullable = false)
    @Builder.Default
    private Integer maxRetries = 3;

    @Column(name = "error_message", columnDefinition = "TEXT")
    private String errorMessage;

    @Column(name = "started_at")
    private Instant startedAt;

    @Column(name = "completed_at")
    private Instant completedAt;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    public void startProcessing() {
        this.status = "PROCESSING";
        this.startedAt = Instant.now();
    }

    public void complete() {
        this.status = "COMPLETED";
        this.completedAt = Instant.now();
    }

    public void fail(String errorMessage) {
        this.retryCount++;
        if (this.retryCount >= this.maxRetries) {
            this.status = "FAILED";
        } else {
            this.status = "PENDING";
        }
        this.errorMessage = errorMessage;
    }
}
