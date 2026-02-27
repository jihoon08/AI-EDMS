package com.edms.common.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;
import org.slf4j.MDC;

import java.time.Instant;

@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CommonApiResponse<T> {

    private final boolean success;
    private final T data;
    private final ApiError error;
    private final Instant timestamp;
    private final String traceId;

    public static <T> CommonApiResponse<T> success(T data) {
        return CommonApiResponse.<T>builder()
                .success(true)
                .data(data)
                .timestamp(Instant.now())
                .traceId(MDC.get("traceId"))
                .build();
    }

    public static CommonApiResponse<Void> success() {
        return CommonApiResponse.<Void>builder()
                .success(true)
                .timestamp(Instant.now())
                .traceId(MDC.get("traceId"))
                .build();
    }

    public static <T> CommonApiResponse<T> error(ApiError error) {
        return CommonApiResponse.<T>builder()
                .success(false)
                .error(error)
                .timestamp(Instant.now())
                .traceId(MDC.get("traceId"))
                .build();
    }

    @Getter
    @Builder
    public static class ApiError {
        private final String code;
        private final String message;
        private final Object details;
    }
}
