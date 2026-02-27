package com.edms.auth.controller;

import com.edms.auth.dto.AuthDto;
import com.edms.auth.service.AuthService;
import com.edms.common.dto.CommonApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final AuthService authService;

    @GetMapping
    public CommonApiResponse<Page<AuthDto.UserResponse>> list(
            @RequestParam(required = false) String keyword,
            @PageableDefault(size = 20, sort = "name", direction = Sort.Direction.ASC) Pageable pageable) {
        return CommonApiResponse.success(authService.getUsers(keyword, pageable));
    }

    @GetMapping("/{userUuid}")
    public CommonApiResponse<AuthDto.UserResponse> get(@PathVariable UUID userUuid) {
        return CommonApiResponse.success(authService.getUser(userUuid));
    }

    @PutMapping("/{userUuid}")
    public CommonApiResponse<AuthDto.UserResponse> update(
            @PathVariable UUID userUuid,
            @RequestBody AuthDto.UpdateUserRequest request) {
        return CommonApiResponse.success(authService.updateUser(userUuid, request));
    }

    @PatchMapping("/{userUuid}/toggle-active")
    public CommonApiResponse<Void> toggleActive(@PathVariable UUID userUuid) {
        authService.toggleUserActive(userUuid);
        return CommonApiResponse.success();
    }

    @PostMapping("/{userUuid}/roles/{roleCode}")
    public CommonApiResponse<Void> assignRole(
            @PathVariable UUID userUuid,
            @PathVariable String roleCode) {
        authService.assignRole(userUuid, roleCode);
        return CommonApiResponse.success();
    }
}
