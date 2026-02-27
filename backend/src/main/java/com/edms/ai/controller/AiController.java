package com.edms.ai.controller;

import com.edms.ai.dto.AiDto;
import com.edms.ai.service.AiService;
import com.edms.common.dto.CommonApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/ai")
@RequiredArgsConstructor
public class AiController {

    private final AiService aiService;

    // 문서별 AI 결과
    @GetMapping("/documents/{documentUuid}")
    public ResponseEntity<CommonApiResponse<AiDto.DocumentAiResults>> getDocumentAiResults(
            @PathVariable UUID documentUuid) {
        return ResponseEntity.ok(CommonApiResponse.success(aiService.getDocumentAiResults(documentUuid)));
    }

    // Q&A 질문
    @PostMapping("/qa")
    public ResponseEntity<CommonApiResponse<AiDto.QaResponse>> askQuestion(
            @RequestBody AiDto.QaRequest request,
            Authentication auth) {
        UUID userUuid = (UUID) auth.getPrincipal();
        return ResponseEntity.ok(CommonApiResponse.success(aiService.askQuestion(userUuid, request.getQuestion())));
    }

    // Q&A 이력
    @GetMapping("/qa/history")
    public ResponseEntity<CommonApiResponse<Page<AiDto.QaResponse>>> getQaHistory(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            Authentication auth) {
        UUID userUuid = (UUID) auth.getPrincipal();
        return ResponseEntity.ok(CommonApiResponse.success(aiService.getQaHistory(userUuid, page, size)));
    }

    // Q&A 피드백
    @PatchMapping("/qa/{qaUuid}/feedback")
    public ResponseEntity<CommonApiResponse<Void>> setFeedback(
            @PathVariable UUID qaUuid,
            @RequestBody AiDto.FeedbackRequest request) {
        aiService.setFeedback(qaUuid, request.getFeedback());
        return ResponseEntity.ok(CommonApiResponse.success(null));
    }

    // AI 처리 현황
    @GetMapping("/status")
    public ResponseEntity<CommonApiResponse<AiDto.AiStatusResponse>> getAiStatus() {
        return ResponseEntity.ok(CommonApiResponse.success(aiService.getAiStatus()));
    }

    // AI 분류 검증
    @PatchMapping("/classifications/{uuid}/verify")
    public ResponseEntity<CommonApiResponse<Void>> verifyClassification(
            @PathVariable UUID uuid,
            Authentication auth) {
        UUID userUuid = (UUID) auth.getPrincipal();
        aiService.verifyClassification(uuid, userUuid);
        return ResponseEntity.ok(CommonApiResponse.success(null));
    }
}
