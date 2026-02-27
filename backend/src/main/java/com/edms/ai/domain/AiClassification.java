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
@Table(name = "t_core_ai_classification")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AiClassification {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "classification_uuid")
    private UUID classificationUuid;

    @Column(name = "document_uuid", nullable = false)
    private UUID documentUuid;

    @Column(name = "predicted_type", nullable = false, length = 50)
    private String predictedType;

    @Column(name = "confidence", nullable = false)
    private Double confidence;

    @Column(name = "model_version", length = 50)
    private String modelVersion;

    @Column(name = "verified_flag", nullable = false)
    @Builder.Default
    private Boolean verifiedFlag = false;

    @Column(name = "verified_by_uuid")
    private UUID verifiedByUuid;

    @Column(name = "verified_at")
    private Instant verifiedAt;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    public void verify(UUID verifiedByUuid) {
        this.verifiedFlag = true;
        this.verifiedByUuid = verifiedByUuid;
        this.verifiedAt = Instant.now();
    }
}
