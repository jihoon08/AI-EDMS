package com.edms.share.dto;

import com.edms.share.domain.AccessRequest;
import com.edms.share.domain.DocumentShare;
import com.edms.share.domain.ShareLink;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

public class ShareDto {

    // 공유 링크
    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CreateShareLinkRequest {
        private UUID documentUuid;
        private String permissionLevel;
        private String password;
        private Boolean requireLogin;
        private Integer maxAccessCount;
        private Integer expiresInDays;
    }

    @Getter
    @Builder
    public static class ShareLinkResponse {
        private UUID linkUuid;
        private UUID documentUuid;
        private String linkToken;
        private String shareUrl;
        private String permissionLevel;
        private Boolean requireLogin;
        private Boolean hasPassword;
        private Integer maxAccessCount;
        private Integer accessCount;
        private String expiresAt;
        private Boolean activeFlag;
        private String createdAt;

        public static ShareLinkResponse from(ShareLink sl, String baseUrl) {
            return ShareLinkResponse.builder()
                    .linkUuid(sl.getLinkUuid())
                    .documentUuid(sl.getDocumentUuid())
                    .linkToken(sl.getLinkToken())
                    .shareUrl(baseUrl + "/shared/" + sl.getLinkToken())
                    .permissionLevel(sl.getPermissionLevel())
                    .requireLogin(sl.getRequireLogin())
                    .hasPassword(sl.getPasswordHash() != null)
                    .maxAccessCount(sl.getMaxAccessCount())
                    .accessCount(sl.getAccessCount())
                    .expiresAt(sl.getExpiresAt() != null ? sl.getExpiresAt().toString() : null)
                    .activeFlag(sl.getActiveFlag())
                    .createdAt(sl.getCreatedAt() != null ? sl.getCreatedAt().toString() : null)
                    .build();
        }
    }

    // 사용자 직접 공유
    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CreateDocumentShareRequest {
        private UUID documentUuid;
        private UUID sharedWithUuid;
        private String permissionLevel;
        private String message;
    }

    @Getter
    @Builder
    public static class DocumentShareResponse {
        private UUID shareUuid;
        private UUID documentUuid;
        private UUID sharedWithUuid;
        private String permissionLevel;
        private String message;
        private String createdAt;

        public static DocumentShareResponse from(DocumentShare ds) {
            return DocumentShareResponse.builder()
                    .shareUuid(ds.getShareUuid())
                    .documentUuid(ds.getDocumentUuid())
                    .sharedWithUuid(ds.getSharedWithUuid())
                    .permissionLevel(ds.getPermissionLevel())
                    .message(ds.getMessage())
                    .createdAt(ds.getCreatedAt() != null ? ds.getCreatedAt().toString() : null)
                    .build();
        }
    }

    // 접근 요청
    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CreateAccessRequestRequest {
        private String targetType;
        private UUID targetUuid;
        private String requestType;
        private String reason;
        private Integer requestDays;
    }

    @Getter
    @Builder
    public static class AccessRequestResponse {
        private UUID requestUuid;
        private String targetType;
        private UUID targetUuid;
        private UUID requesterUuid;
        private UUID ownerUuid;
        private String requestType;
        private String reason;
        private Integer requestDays;
        private String status;
        private String decidedAt;
        private String rejectReason;
        private String grantedUntil;
        private String createdAt;

        public static AccessRequestResponse from(AccessRequest ar) {
            return AccessRequestResponse.builder()
                    .requestUuid(ar.getRequestUuid())
                    .targetType(ar.getTargetType())
                    .targetUuid(ar.getTargetUuid())
                    .requesterUuid(ar.getRequesterUuid())
                    .ownerUuid(ar.getOwnerUuid())
                    .requestType(ar.getRequestType())
                    .reason(ar.getReason())
                    .requestDays(ar.getRequestDays())
                    .status(ar.getStatus())
                    .decidedAt(ar.getDecidedAt() != null ? ar.getDecidedAt().toString() : null)
                    .rejectReason(ar.getRejectReason())
                    .grantedUntil(ar.getGrantedUntil() != null ? ar.getGrantedUntil().toString() : null)
                    .createdAt(ar.getCreatedAt() != null ? ar.getCreatedAt().toString() : null)
                    .build();
        }
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DecideAccessRequest {
        private String rejectReason;
    }
}
