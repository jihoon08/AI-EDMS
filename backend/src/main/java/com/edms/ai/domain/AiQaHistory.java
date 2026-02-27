package com.edms.ai.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

@Entity
@Table(name = "t_core_ai_qa_history")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AiQaHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "qa_uuid")
    private UUID qaUuid;

    @Column(name = "user_uuid", nullable = false)
    private UUID userUuid;

    @Column(name = "question", nullable = false, columnDefinition = "TEXT")
    private String question;

    @Column(name = "answer", nullable = false, columnDefinition = "TEXT")
    private String answer;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "source_documents", columnDefinition = "jsonb")
    private Object sourceDocuments;

    @Column(name = "model_version", length = 50)
    private String modelVersion;

    // HELPFUL, NOT_HELPFUL
    @Column(name = "feedback", length = 20)
    private String feedback;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    public void setFeedback(String feedback) {
        this.feedback = feedback;
    }
}
