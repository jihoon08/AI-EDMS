package com.edms.auth.controller;

import com.edms.auth.dto.AuthDto;
import com.edms.auth.service.AuthService;
import com.edms.common.dto.CommonApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public CommonApiResponse<AuthDto.LoginResponse> login(@RequestBody AuthDto.LoginRequest request) {
        return CommonApiResponse.success(authService.login(request.getEmail()));
    }

    @GetMapping("/me")
    public CommonApiResponse<AuthDto.UserInfo> me(Authentication authentication) {
        UUID userUuid = (UUID) authentication.getPrincipal();
        return CommonApiResponse.success(authService.getCurrentUser(userUuid));
    }

    @GetMapping("/roles")
    public CommonApiResponse<List<AuthDto.RoleResponse>> roles() {
        return CommonApiResponse.success(authService.getAllRoles());
    }
}
