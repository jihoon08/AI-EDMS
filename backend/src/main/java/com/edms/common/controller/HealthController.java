package com.edms.common.controller;

import com.edms.common.dto.CommonApiResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.Map;

@RestController
public class HealthController {

    @GetMapping("/api/health")
    public CommonApiResponse<Map<String, Object>> health() {
        return CommonApiResponse.success(Map.of(
                "status", "UP",
                "timestamp", Instant.now().toString()
        ));
    }

    @GetMapping("/api/v1/health")
    public CommonApiResponse<Map<String, Object>> healthV1() {
        return CommonApiResponse.success(Map.of(
                "status", "UP",
                "version", "1.0.0",
                "timestamp", Instant.now().toString()
        ));
    }
}
