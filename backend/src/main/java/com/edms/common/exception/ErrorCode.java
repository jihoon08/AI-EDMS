package com.edms.common.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    // C: 공통
    RESOURCE_NOT_FOUND("C001", "요청한 리소스를 찾을 수 없습니다", HttpStatus.NOT_FOUND),
    INVALID_INPUT("C002", "잘못된 입력입니다", HttpStatus.BAD_REQUEST),
    INTERNAL_ERROR("C003", "내부 서버 오류가 발생했습니다", HttpStatus.INTERNAL_SERVER_ERROR),
    DUPLICATE_RESOURCE("C004", "이미 존재하는 리소스입니다", HttpStatus.CONFLICT),
    OPTIMISTIC_LOCK_CONFLICT("C005", "다른 사용자가 데이터를 수정했습니다", HttpStatus.CONFLICT),
    INVALID_STATE_TRANSITION("C006", "허용되지 않는 상태 변경입니다", HttpStatus.BAD_REQUEST),

    COMMON_NOT_FOUND("C007", "요청한 데이터를 찾을 수 없습니다", HttpStatus.NOT_FOUND),

    // A: 인증
    INVALID_TOKEN("A001", "유효하지 않은 토큰입니다", HttpStatus.UNAUTHORIZED),
    EXPIRED_TOKEN("A002", "만료된 토큰입니다", HttpStatus.UNAUTHORIZED),
    AUTHENTICATION_REQUIRED("A003", "인증이 필요합니다", HttpStatus.UNAUTHORIZED),
    ACCESS_DENIED("A004", "접근 권한이 없습니다", HttpStatus.FORBIDDEN),
    USER_NOT_FOUND("A005", "사용자를 찾을 수 없습니다", HttpStatus.NOT_FOUND),
    AUTH_ACCESS_DENIED("A006", "접근이 거부되었습니다", HttpStatus.FORBIDDEN),

    // D: 문서
    DOCUMENT_NOT_FOUND("D001", "문서를 찾을 수 없습니다", HttpStatus.NOT_FOUND),
    DOCUMENT_LOCKED("D002", "문서가 잠겨 있습니다", HttpStatus.CONFLICT),
    DOCUMENT_VERSION_CONFLICT("D003", "문서 버전이 충돌합니다", HttpStatus.CONFLICT),
    INVALID_FILE_TYPE("D004", "지원하지 않는 파일 형식입니다", HttpStatus.BAD_REQUEST),
    FILE_SIZE_EXCEEDED("D005", "파일 용량을 초과했습니다", HttpStatus.BAD_REQUEST),

    // F: 폴더
    FOLDER_NOT_FOUND("F001", "폴더를 찾을 수 없습니다", HttpStatus.NOT_FOUND),
    FOLDER_HAS_CHILDREN("F002", "하위 항목이 있는 폴더는 삭제할 수 없습니다", HttpStatus.BAD_REQUEST),

    // W: 결재
    WORKFLOW_NOT_FOUND("W001", "결재를 찾을 수 없습니다", HttpStatus.NOT_FOUND),
    WORKFLOW_ALREADY_PROCESSED("W002", "이미 처리된 결재입니다", HttpStatus.CONFLICT),
    WORKFLOW_INVALID_APPROVER("W003", "결재 권한이 없습니다", HttpStatus.FORBIDDEN),

    // I: AI
    AI_SERVICE_UNAVAILABLE("I001", "AI 서비스를 사용할 수 없습니다", HttpStatus.SERVICE_UNAVAILABLE),
    AI_PROCESSING_FAILED("I002", "AI 처리에 실패했습니다", HttpStatus.INTERNAL_SERVER_ERROR),

    // N: 알림
    NOTIFICATION_TEMPLATE_NOT_FOUND("N001", "알림 템플릿을 찾을 수 없습니다", HttpStatus.NOT_FOUND),
    NOTIFICATION_SEND_FAILED("N002", "알림 발송에 실패했습니다", HttpStatus.INTERNAL_SERVER_ERROR),

    // B: 배치
    BATCH_ALREADY_RUNNING("B001", "배치 작업이 이미 실행 중입니다", HttpStatus.CONFLICT),

    // S: 저장소
    STORAGE_UPLOAD_FAILED("S001", "파일 업로드에 실패했습니다", HttpStatus.INTERNAL_SERVER_ERROR),
    STORAGE_DOWNLOAD_FAILED("S002", "파일 다운로드에 실패했습니다", HttpStatus.INTERNAL_SERVER_ERROR);

    private final String code;
    private final String defaultMessage;
    private final HttpStatus httpStatus;
}
