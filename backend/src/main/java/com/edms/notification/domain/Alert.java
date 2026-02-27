package com.edms.notification.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "t_noti_alert")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Alert {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "alert_uuid")
    private UUID alertUuid;

    @Column(name = "user_uuid", nullable = false)
    private UUID userUuid;

    // APPROVAL_REQUEST, APPROVAL_APPROVED, APPROVAL_REJECTED, DOCUMENT_SHARED, ACCESS_REQUEST, SYSTEM
    @Column(name = "alert_type", nullable = false, length = 50)
    private String alertType;

    @Column(name = "title", nullable = false, length = 500)
    private String title;

    @Column(name = "message", columnDefinition = "TEXT")
    private String message;

    @Column(name = "link", length = 1000)
    private String link;

    @Column(name = "reference_type", length = 30)
    private String referenceType;

    @Column(name = "reference_uuid")
    private UUID referenceUuid;

    @Column(name = "read_flag", nullable = false)
    @Builder.Default
    private Boolean readFlag = false;

    @Column(name = "read_at")
    private Instant readAt;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    public void markAsRead() {
        this.readFlag = true;
        this.readAt = Instant.now();
    }
}
