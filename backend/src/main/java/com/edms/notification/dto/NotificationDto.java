package com.edms.notification.dto;

import com.edms.notification.domain.Alert;
import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

public class NotificationDto {

    @Getter
    @Builder
    public static class AlertResponse {
        private UUID alertUuid;
        private String alertType;
        private String title;
        private String message;
        private String link;
        private String referenceType;
        private UUID referenceUuid;
        private Boolean readFlag;
        private String readAt;
        private String createdAt;

        public static AlertResponse from(Alert a) {
            return AlertResponse.builder()
                    .alertUuid(a.getAlertUuid())
                    .alertType(a.getAlertType())
                    .title(a.getTitle())
                    .message(a.getMessage())
                    .link(a.getLink())
                    .referenceType(a.getReferenceType())
                    .referenceUuid(a.getReferenceUuid())
                    .readFlag(a.getReadFlag())
                    .readAt(a.getReadAt() != null ? a.getReadAt().toString() : null)
                    .createdAt(a.getCreatedAt() != null ? a.getCreatedAt().toString() : null)
                    .build();
        }
    }

    @Getter
    @Builder
    public static class UnreadCountResponse {
        private long count;
    }
}
