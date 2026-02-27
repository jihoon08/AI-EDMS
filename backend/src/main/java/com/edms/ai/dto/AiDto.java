package com.edms.ai.dto;

import com.edms.ai.domain.AiClassification;
import com.edms.ai.domain.AiProcessingQueue;
import com.edms.ai.domain.AiQaHistory;
import com.edms.ai.domain.AiSummary;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

public class AiDto {

    @Getter
    @Builder
    public static class ClassificationResponse {
        private UUID classificationUuid;
        private UUID documentUuid;
        private String predictedType;
        private Double confidence;
        private String modelVersion;
        private Boolean verifiedFlag;
        private String createdAt;

        public static ClassificationResponse from(AiClassification c) {
            return ClassificationResponse.builder()
                    .classificationUuid(c.getClassificationUuid())
                    .documentUuid(c.getDocumentUuid())
                    .predictedType(c.getPredictedType())
                    .confidence(c.getConfidence())
                    .modelVersion(c.getModelVersion())
                    .verifiedFlag(c.getVerifiedFlag())
                    .createdAt(c.getCreatedAt() != null ? c.getCreatedAt().toString() : null)
                    .build();
        }
    }

    @Getter
    @Builder
    public static class SummaryResponse {
        private UUID summaryUuid;
        private UUID documentUuid;
        private String summaryType;
        private String content;
        private String modelVersion;
        private String createdAt;

        public static SummaryResponse from(AiSummary s) {
            return SummaryResponse.builder()
                    .summaryUuid(s.getSummaryUuid())
                    .documentUuid(s.getDocumentUuid())
                    .summaryType(s.getSummaryType())
                    .content(s.getContent())
                    .modelVersion(s.getModelVersion())
                    .createdAt(s.getCreatedAt() != null ? s.getCreatedAt().toString() : null)
                    .build();
        }
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class QaRequest {
        private String question;
    }

    @Getter
    @Builder
    public static class QaResponse {
        private UUID qaUuid;
        private String question;
        private String answer;
        private Object sourceDocuments;
        private String modelVersion;
        private String feedback;
        private String createdAt;

        public static QaResponse from(AiQaHistory h) {
            return QaResponse.builder()
                    .qaUuid(h.getQaUuid())
                    .question(h.getQuestion())
                    .answer(h.getAnswer())
                    .sourceDocuments(h.getSourceDocuments())
                    .modelVersion(h.getModelVersion())
                    .feedback(h.getFeedback())
                    .createdAt(h.getCreatedAt() != null ? h.getCreatedAt().toString() : null)
                    .build();
        }
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FeedbackRequest {
        private String feedback; // HELPFUL, NOT_HELPFUL
    }

    @Getter
    @Builder
    public static class ProcessingQueueResponse {
        private UUID queueUuid;
        private UUID documentUuid;
        private String taskType;
        private String status;
        private Integer priority;
        private Integer retryCount;
        private String errorMessage;
        private String createdAt;

        public static ProcessingQueueResponse from(AiProcessingQueue q) {
            return ProcessingQueueResponse.builder()
                    .queueUuid(q.getQueueUuid())
                    .documentUuid(q.getDocumentUuid())
                    .taskType(q.getTaskType())
                    .status(q.getStatus())
                    .priority(q.getPriority())
                    .retryCount(q.getRetryCount())
                    .errorMessage(q.getErrorMessage())
                    .createdAt(q.getCreatedAt() != null ? q.getCreatedAt().toString() : null)
                    .build();
        }
    }

    @Getter
    @Builder
    public static class DocumentAiResults {
        private List<ClassificationResponse> classifications;
        private List<SummaryResponse> summaries;
        private List<ProcessingQueueResponse> processingQueue;
    }

    @Getter
    @Builder
    public static class AiStatusResponse {
        private long pending;
        private long processing;
        private long completed;
        private long failed;
    }
}
