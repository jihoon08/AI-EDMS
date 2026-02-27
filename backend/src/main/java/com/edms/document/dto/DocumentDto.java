package com.edms.document.dto;

import com.edms.document.domain.Document;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

public class DocumentDto {

    @Getter
    @Builder
    public static class Response {
        private UUID documentUuid;
        private String documentNumber;
        private String title;
        private String description;
        private String documentType;
        private String status;
        private String securityLevel;
        private UUID folderUuid;
        private UUID ownerUuid;
        private Integer currentVersion;
        private String fileName;
        private Long fileSize;
        private String contentType;
        private String storageKey;
        private String retentionPeriod;
        private Instant createdAt;
        private Instant updatedAt;

        public static Response from(Document doc) {
            return Response.builder()
                    .documentUuid(doc.getDocumentUuid())
                    .documentNumber(doc.getDocumentNumber())
                    .title(doc.getTitle())
                    .description(doc.getDescription())
                    .documentType(doc.getDocumentType())
                    .status(doc.getStatus())
                    .securityLevel(doc.getSecurityLevel())
                    .folderUuid(doc.getFolderUuid())
                    .ownerUuid(doc.getOwnerUuid())
                    .currentVersion(doc.getCurrentVersion())
                    .fileName(doc.getFileName())
                    .fileSize(doc.getFileSize())
                    .contentType(doc.getContentType())
                    .storageKey(doc.getStorageKey())
                    .retentionPeriod(doc.getRetentionPeriod())
                    .createdAt(doc.getCreatedAt())
                    .updatedAt(doc.getUpdatedAt())
                    .build();
        }
    }

    @Getter
    public static class UploadRequest {
        @NotBlank(message = "제목을 입력해주세요")
        private String title;
        private String description;
        @NotBlank(message = "문서 유형을 선택해주세요")
        private String documentType;
        private String securityLevel;
        private UUID folderUuid;
        private String retentionPeriod;
    }

    @Getter
    public static class UpdateRequest {
        @NotBlank(message = "제목을 입력해주세요")
        private String title;
        private String description;
        private String documentType;
        private String securityLevel;
        private UUID folderUuid;
    }

    @Getter
    public static class StatusChangeRequest {
        @NotBlank(message = "변경할 상태를 입력해주세요")
        private String status;
    }

    @Getter
    @Builder
    public static class VersionResponse {
        private UUID versionUuid;
        private Integer versionNumber;
        private String fileName;
        private Long fileSize;
        private String contentType;
        private String changeSummary;
        private Instant createdAt;
        private UUID createdByUuid;
    }
}
