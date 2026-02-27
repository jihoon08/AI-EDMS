package com.edms.share.domain;

import com.edms.common.domain.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "t_core_share_link", schema = "edms")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ShareLink extends BaseEntity {

    @Id
    @Column(name = "link_uuid")
    private UUID linkUuid;

    @Column(name = "document_uuid", nullable = false)
    private UUID documentUuid;

    @Column(name = "link_token", nullable = false, unique = true, length = 100)
    private String linkToken;

    @Column(name = "permission_level", nullable = false, length = 20)
    private String permissionLevel;

    @Column(name = "password_hash", length = 200)
    private String passwordHash;

    @Column(name = "require_login", nullable = false)
    private Boolean requireLogin;

    @Column(name = "max_access_count")
    private Integer maxAccessCount;

    @Column(name = "access_count", nullable = false)
    private Integer accessCount;

    @Column(name = "expires_at")
    private Instant expiresAt;

    @Column(name = "active_flag", nullable = false)
    private Boolean activeFlag;

    @PrePersist
    void prePersist() {
        if (this.linkUuid == null) this.linkUuid = UUID.randomUUID();
        if (this.permissionLevel == null) this.permissionLevel = "READ";
        if (this.requireLogin == null) this.requireLogin = false;
        if (this.accessCount == null) this.accessCount = 0;
        if (this.activeFlag == null) this.activeFlag = true;
    }

    @Builder
    public ShareLink(UUID documentUuid, String linkToken, String permissionLevel,
                     String passwordHash, Boolean requireLogin,
                     Integer maxAccessCount, Instant expiresAt) {
        this.documentUuid = documentUuid;
        this.linkToken = linkToken;
        this.permissionLevel = permissionLevel;
        this.passwordHash = passwordHash;
        this.requireLogin = requireLogin != null ? requireLogin : false;
        this.maxAccessCount = maxAccessCount;
        this.expiresAt = expiresAt;
    }

    public void incrementAccess() {
        this.accessCount++;
    }

    public void deactivate() {
        this.activeFlag = false;
    }

    public boolean isExpired() {
        return this.expiresAt != null && Instant.now().isAfter(this.expiresAt);
    }

    public boolean isMaxAccessReached() {
        return this.maxAccessCount != null && this.accessCount >= this.maxAccessCount;
    }
}
