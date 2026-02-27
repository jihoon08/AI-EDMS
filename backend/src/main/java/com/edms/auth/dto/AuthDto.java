package com.edms.auth.dto;

import com.edms.auth.domain.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

public class AuthDto {

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LoginRequest {
        private String email;
    }

    @Getter
    @Builder
    public static class LoginResponse {
        private String accessToken;
        private String refreshToken;
        private UserInfo user;
    }

    @Getter
    @Builder
    public static class UserInfo {
        private UUID userUuid;
        private String email;
        private String name;
        private String department;
        private String position;
        private String profileImage;
        private List<String> roles;
        private List<String> permissions;

        public static UserInfo from(User user, List<String> roles, List<String> permissions) {
            return UserInfo.builder()
                    .userUuid(user.getUserUuid())
                    .email(user.getEmail())
                    .name(user.getName())
                    .department(user.getDepartment())
                    .position(user.getPosition())
                    .profileImage(user.getProfileImage())
                    .roles(roles)
                    .permissions(permissions)
                    .build();
        }
    }

    @Getter
    @Builder
    public static class UserResponse {
        private UUID userUuid;
        private String email;
        private String name;
        private String department;
        private String position;
        private String phone;
        private String provider;
        private Boolean activeFlag;
        private String lastLoginAt;
        private String createdAt;
        private List<String> roles;

        public static UserResponse from(User user, List<String> roles) {
            return UserResponse.builder()
                    .userUuid(user.getUserUuid())
                    .email(user.getEmail())
                    .name(user.getName())
                    .department(user.getDepartment())
                    .position(user.getPosition())
                    .phone(user.getPhone())
                    .provider(user.getProvider())
                    .activeFlag(user.getActiveFlag())
                    .lastLoginAt(user.getLastLoginAt() != null ? user.getLastLoginAt().toString() : null)
                    .createdAt(user.getCreatedAt() != null ? user.getCreatedAt().toString() : null)
                    .roles(roles)
                    .build();
        }
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UpdateUserRequest {
        private String name;
        private String department;
        private String position;
        private String phone;
    }

    @Getter
    @Builder
    public static class RoleResponse {
        private UUID roleUuid;
        private String roleCode;
        private String roleName;
        private String description;
    }
}
