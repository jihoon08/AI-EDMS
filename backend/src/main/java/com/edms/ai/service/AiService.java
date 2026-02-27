package com.edms.ai.service;

import com.edms.ai.domain.*;
import com.edms.ai.dto.AiDto;
import com.edms.ai.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class AiService {

    private final AiClassificationRepository classificationRepository;
    private final AiSummaryRepository summaryRepository;
    private final AiQaHistoryRepository qaHistoryRepository;
    private final AiProcessingQueueRepository queueRepository;

    // ===== 문서별 AI 결과 조회 =====

    @Transactional(readOnly = true)
    public AiDto.DocumentAiResults getDocumentAiResults(UUID documentUuid) {
        List<AiDto.ClassificationResponse> classifications = classificationRepository
                .findByDocumentUuidOrderByCreatedAtDesc(documentUuid).stream()
                .map(AiDto.ClassificationResponse::from).toList();

        List<AiDto.SummaryResponse> summaries = summaryRepository
                .findByDocumentUuidOrderByCreatedAtDesc(documentUuid).stream()
                .map(AiDto.SummaryResponse::from).toList();

        List<AiDto.ProcessingQueueResponse> queue = queueRepository
                .findByDocumentUuidOrderByCreatedAtDesc(documentUuid).stream()
                .map(AiDto.ProcessingQueueResponse::from).toList();

        return AiDto.DocumentAiResults.builder()
                .classifications(classifications)
                .summaries(summaries)
                .processingQueue(queue)
                .build();
    }

    // ===== AI Q&A (RAG) =====

    @Transactional
    public AiDto.QaResponse askQuestion(UUID userUuid, String question) {
        // dev 환경: 스텁 응답 (실제 Claude API 연동은 Phase 5에서 환경 변수 설정 시 활성화)
        String answer = generateStubAnswer(question);
        List<Map<String, String>> sources = List.of(
                Map.of("title", "관련 문서 예시", "documentUuid", UUID.randomUUID().toString())
        );

        AiQaHistory qa = AiQaHistory.builder()
                .userUuid(userUuid)
                .question(question)
                .answer(answer)
                .sourceDocuments(sources)
                .modelVersion("stub-v1")
                .build();
        AiQaHistory saved = qaHistoryRepository.save(qa);
        log.info("AI Q&A: user={}, question={}", userUuid, question);

        return AiDto.QaResponse.from(saved);
    }

    @Transactional(readOnly = true)
    public Page<AiDto.QaResponse> getQaHistory(UUID userUuid, int page, int size) {
        return qaHistoryRepository.findByUserUuidOrderByCreatedAtDesc(userUuid, PageRequest.of(page, size))
                .map(AiDto.QaResponse::from);
    }

    @Transactional
    public void setFeedback(UUID qaUuid, String feedback) {
        qaHistoryRepository.findById(qaUuid).ifPresent(qa -> qa.setFeedback(feedback));
    }

    // ===== AI 처리 큐 =====

    @Transactional
    public void enqueueDocumentProcessing(UUID documentUuid) {
        String[] taskTypes = {"CLASSIFICATION", "SUMMARY", "EMBEDDING"};
        for (String taskType : taskTypes) {
            AiProcessingQueue queue = AiProcessingQueue.builder()
                    .documentUuid(documentUuid)
                    .taskType(taskType)
                    .build();
            queueRepository.save(queue);
        }
        log.info("AI 처리 큐 등록: document={}", documentUuid);
    }

    @Transactional(readOnly = true)
    public AiDto.AiStatusResponse getAiStatus() {
        return AiDto.AiStatusResponse.builder()
                .pending(queueRepository.countByStatus("PENDING"))
                .processing(queueRepository.countByStatus("PROCESSING"))
                .completed(queueRepository.countByStatus("COMPLETED"))
                .failed(queueRepository.countByStatus("FAILED"))
                .build();
    }

    // ===== AI 분류 검증 =====

    @Transactional
    public void verifyClassification(UUID classificationUuid, UUID verifiedByUuid) {
        classificationRepository.findById(classificationUuid)
                .ifPresent(c -> c.verify(verifiedByUuid));
    }

    // ===== private =====

    private String generateStubAnswer(String question) {
        return "현재 AI 서비스는 개발 모드입니다. 질문: \"" + question + "\"에 대한 답변을 생성하려면 " +
                "Claude API 키를 설정해주세요. (application.yml의 app.ai.claude.api-key)";
    }
}
