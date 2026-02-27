package com.edms.auth.service;

import com.edms.auth.domain.Role;
import com.edms.auth.domain.RolePermission;
import com.edms.auth.domain.User;
import com.edms.auth.domain.UserRole;
import com.edms.auth.dto.AuthDto;
import com.edms.auth.repository.*;
import com.edms.common.exception.BusinessException;
import com.edms.common.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserRoleRepository userRoleRepository;
    private final RolePermissionRepository rolePermissionRepository;
    private final JwtTokenService jwtTokenService;

    @Transactional
    public AuthDto.LoginResponse login(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        if (!user.getActiveFlag()) {
            throw new BusinessException(ErrorCode.AUTH_ACCESS_DENIED, "비활성화된 계정입니다");
        }

        user.recordLogin();

        List<String> roles = getUserRoles(user.getUserUuid());
        List<String> permissions = getUserPermissions(user.getUserUuid());

        String accessToken = jwtTokenService.generateAccessToken(user.getUserUuid(), email, roles);
        String refreshToken = jwtTokenService.generateRefreshToken(user.getUserUuid());

        log.info("로그인 성공: {} ({})", user.getName(), email);

        return AuthDto.LoginResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .user(AuthDto.UserInfo.from(user, roles, permissions))
                .build();
    }

    @Transactional(readOnly = true)
    public AuthDto.UserInfo getCurrentUser(UUID userUuid) {
        User user = userRepository.findById(userUuid)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
        List<String> roles = getUserRoles(userUuid);
        List<String> permissions = getUserPermissions(userUuid);
        return AuthDto.UserInfo.from(user, roles, permissions);
    }

    @Transactional(readOnly = true)
    public Page<AuthDto.UserResponse> getUsers(String keyword, Pageable pageable) {
        Page<User> users;
        if (keyword != null && !keyword.isBlank()) {
            users = userRepository.searchByKeyword(keyword, pageable);
        } else {
            users = userRepository.findAllActive(pageable);
        }
        return users.map(user -> {
            List<String> roles = getUserRoles(user.getUserUuid());
            return AuthDto.UserResponse.from(user, roles);
        });
    }

    @Transactional(readOnly = true)
    public AuthDto.UserResponse getUser(UUID userUuid) {
        User user = userRepository.findById(userUuid)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
        List<String> roles = getUserRoles(userUuid);
        return AuthDto.UserResponse.from(user, roles);
    }

    @Transactional
    public AuthDto.UserResponse updateUser(UUID userUuid, AuthDto.UpdateUserRequest request) {
        User user = userRepository.findById(userUuid)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
        user.updateProfile(request.getName(), request.getDepartment(),
                request.getPosition(), request.getPhone());
        List<String> roles = getUserRoles(userUuid);
        return AuthDto.UserResponse.from(user, roles);
    }

    @Transactional
    public void toggleUserActive(UUID userUuid) {
        User user = userRepository.findById(userUuid)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
        if (user.getActiveFlag()) {
            user.deactivate();
        } else {
            user.activate();
        }
    }

    @Transactional
    public void assignRole(UUID userUuid, String roleCode) {
        Role role = roleRepository.findByRoleCode(roleCode)
                .orElseThrow(() -> new BusinessException(ErrorCode.COMMON_NOT_FOUND, "역할을 찾을 수 없습니다: " + roleCode));
        UserRole userRole = new UserRole(userUuid, role.getRoleUuid());
        userRoleRepository.save(userRole);
    }

    @Transactional(readOnly = true)
    public List<AuthDto.RoleResponse> getAllRoles() {
        return roleRepository.findAllActive().stream()
                .map(r -> AuthDto.RoleResponse.builder()
                        .roleUuid(r.getRoleUuid())
                        .roleCode(r.getRoleCode())
                        .roleName(r.getRoleName())
                        .description(r.getDescription())
                        .build())
                .toList();
    }

    @Transactional(readOnly = true)
    public List<String> getUserRoles(UUID userUuid) {
        return userRoleRepository.findByUserUuidWithRole(userUuid).stream()
                .map(ur -> ur.getRole().getRoleCode())
                .toList();
    }

    @Transactional(readOnly = true)
    public List<String> getUserPermissions(UUID userUuid) {
        List<UserRole> userRoles = userRoleRepository.findByUserUuidWithRole(userUuid);
        if (userRoles.isEmpty()) return List.of();

        Set<UUID> roleUuids = userRoles.stream()
                .map(ur -> ur.getRole().getRoleUuid())
                .collect(Collectors.toSet());

        return rolePermissionRepository.findByRoleUuidsWithPermission(roleUuids).stream()
                .map(rp -> rp.getPermission().getPermissionCode())
                .distinct()
                .toList();
    }

    @Transactional(readOnly = true)
    public boolean hasPermission(UUID userUuid, String permissionCode) {
        return getUserPermissions(userUuid).contains(permissionCode);
    }

    @Transactional(readOnly = true)
    public boolean hasRole(UUID userUuid, String roleCode) {
        return getUserRoles(userUuid).contains(roleCode);
    }
}
