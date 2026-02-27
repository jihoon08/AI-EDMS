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
@Table(name = "t_core_ai_summary")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AiSummary {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "summary_uuid")
    private UUID summaryUuid;

    @Column(name = "document_uuid", nullable = false)
    private UUID documentUuid;

    // ONE_LINE, DETAILED, KEY_POINTS
    @Column(name = "summary_type", nullable = false, length = 20)
    private String summaryType;

    @Column(name = "content", nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(name = "model_version", length = 50)
    private String modelVersion;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;
}
