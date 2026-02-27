package com.edms.common.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class RequestLoggingFilter extends OncePerRequestFilter {

    private static final String TRACE_ID = "traceId";

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain) throws ServletException, IOException {
        try {
            String traceId = Optional.ofNullable(request.getHeader("X-Trace-Id"))
                    .filter(s -> !s.isBlank())
                    .orElse(UUID.randomUUID().toString().replace("-", "").substring(0, 16));

            MDC.put(TRACE_ID, traceId);
            MDC.put("requestUri", request.getRequestURI());
            MDC.put("requestMethod", request.getMethod());

            String userUuid = request.getHeader("X-User-UUID");
            if (userUuid != null && !userUuid.isBlank()) {
                MDC.put("userId", userUuid);
            }

            response.setHeader("X-Trace-Id", traceId);

            long start = System.currentTimeMillis();
            chain.doFilter(request, response);
            long elapsed = System.currentTimeMillis() - start;

            log.info("{} {} {} {}ms", request.getMethod(), request.getRequestURI(),
                    response.getStatus(), elapsed);
        } finally {
            MDC.clear();
        }
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        return path.startsWith("/actuator")
                || path.endsWith(".js")
                || path.endsWith(".css")
                || path.endsWith(".ico");
    }
}
