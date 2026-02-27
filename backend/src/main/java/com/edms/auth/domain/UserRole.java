package com.edms.auth.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "t_auth_user_role", schema = "edms")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserRole {

    @Id
    @Column(name = "user_role_uuid")
    private UUID userRoleUuid;

    @Column(name = "user_uuid", nullable = false)
    private UUID userUuid;

    @Column(name = "role_uuid", nullable = false)
    private UUID roleUuid;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "created_by_uuid")
    private UUID createdByUuid;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "role_uuid", insertable = false, updatable = false)
    private Role role;

    @PrePersist
    void prePersist() {
        if (this.userRoleUuid == null) this.userRoleUuid = UUID.randomUUID();
        if (this.createdAt == null) this.createdAt = Instant.now();
    }

    public UserRole(UUID userUuid, UUID roleUuid) {
        this.userUuid = userUuid;
        this.roleUuid = roleUuid;
    }
}
