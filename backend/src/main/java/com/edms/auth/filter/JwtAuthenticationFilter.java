package com.edms.auth.filter;

import com.edms.auth.service.JwtTokenService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenService jwtTokenService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String token = extractToken(request);

        if (token != null && jwtTokenService.isValid(token)) {
            UUID userUuid = jwtTokenService.getUserUuid(token);
            List<String> roles = jwtTokenService.getRoles(token);

            var authorities = roles.stream()
                    .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                    .toList();

            var authentication = new UsernamePasswordAuthenticationToken(
                    userUuid, null, authorities);
            SecurityContextHolder.getContext().setAuthentication(authentication);

            // MDC와 RequestLoggingFilter에서 사용할 수 있도록 attribute 설정
            request.setAttribute("userUuid", userUuid);
        } else {
            // dev 모드: X-User-UUID 헤더 지원 (JWT 없을 때)
            String devUserUuid = request.getHeader("X-User-UUID");
            if (devUserUuid != null && !devUserUuid.isEmpty()) {
                try {
                    UUID userUuid = UUID.fromString(devUserUuid);
                    var authentication = new UsernamePasswordAuthenticationToken(
                            userUuid, null, List.of(new SimpleGrantedAuthority("ROLE_USER")));
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                    request.setAttribute("userUuid", userUuid);
                } catch (IllegalArgumentException ignored) {
                }
            }
        }

        filterChain.doFilter(request, response);
    }

    private String extractToken(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ")) {
            return header.substring(7);
        }
        return null;
    }
}
