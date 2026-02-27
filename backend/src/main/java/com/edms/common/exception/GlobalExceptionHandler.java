package com.edms.common.exception;

import com.edms.common.dto.CommonApiResponse;
import com.edms.common.dto.CommonApiResponse.ApiError;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<CommonApiResponse<Void>> handleBusiness(BusinessException ex) {
        log.warn("BusinessException: code={}, message={}", ex.getErrorCode().getCode(), ex.getMessage());
        ApiError error = ApiError.builder()
                .code(ex.getErrorCode().getCode())
                .message(ex.getMessage())
                .details(ex.getDetails())
                .build();
        return ResponseEntity
                .status(ex.getErrorCode().getHttpStatus())
                .body(CommonApiResponse.error(error));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<CommonApiResponse<Void>> handleValidation(MethodArgumentNotValidException ex) {
        Map<String, String> fieldErrors = ex.getBindingResult().getFieldErrors().stream()
                .collect(Collectors.toMap(
                        FieldError::getField,
                        fe -> fe.getDefaultMessage() != null ? fe.getDefaultMessage() : "invalid",
                        (a, b) -> a
                ));
        log.warn("ValidationException: {}", fieldErrors);
        ApiError error = ApiError.builder()
                .code(ErrorCode.INVALID_INPUT.getCode())
                .message(ErrorCode.INVALID_INPUT.getDefaultMessage())
                .details(fieldErrors)
                .build();
        return ResponseEntity.badRequest().body(CommonApiResponse.error(error));
    }

    @ExceptionHandler(ObjectOptimisticLockingFailureException.class)
    public ResponseEntity<CommonApiResponse<Void>> handleOptimisticLock(ObjectOptimisticLockingFailureException ex) {
        log.warn("OptimisticLockException: {}", ex.getMessage());
        ErrorCode ec = ErrorCode.OPTIMISTIC_LOCK_CONFLICT;
        ApiError error = ApiError.builder()
                .code(ec.getCode())
                .message(ec.getDefaultMessage())
                .build();
        return ResponseEntity.status(ec.getHttpStatus()).body(CommonApiResponse.error(error));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<CommonApiResponse<Void>> handleUnexpected(Exception ex) {
        log.error("UnexpectedException", ex);
        ErrorCode ec = ErrorCode.INTERNAL_ERROR;
        ApiError error = ApiError.builder()
                .code(ec.getCode())
                .message(ec.getDefaultMessage())
                .build();
        return ResponseEntity.status(ec.getHttpStatus()).body(CommonApiResponse.error(error));
    }
}
