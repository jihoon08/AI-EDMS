package com.edms.auth.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "t_auth_role_permission", schema = "edms")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RolePermission {

    @Id
    @Column(name = "role_permission_uuid")
    private UUID rolePermissionUuid;

    @Column(name = "role_uuid", nullable = false)
    private UUID roleUuid;

    @Column(name = "permission_uuid", nullable = false)
    private UUID permissionUuid;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "created_by_uuid")
    private UUID createdByUuid;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "permission_uuid", insertable = false, updatable = false)
    private Permission permission;

    @PrePersist
    void prePersist() {
        if (this.rolePermissionUuid == null) this.rolePermissionUuid = UUID.randomUUID();
        if (this.createdAt == null) this.createdAt = Instant.now();
    }
}
