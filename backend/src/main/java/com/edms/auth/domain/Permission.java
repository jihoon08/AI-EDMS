package com.edms.auth.domain;

import com.edms.common.domain.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Table(name = "t_auth_permission", schema = "edms")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Permission extends BaseEntity {

    @Id
    @Column(name = "permission_uuid")
    private UUID permissionUuid;

    @Column(name = "permission_code", nullable = false, unique = true, length = 100)
    private String permissionCode;

    @Column(name = "permission_name", nullable = false, length = 200)
    private String permissionName;

    @Column(length = 500)
    private String description;

    @Column(nullable = false, length = 50)
    private String category;

    @PrePersist
    void prePersist() {
        if (this.permissionUuid == null) this.permissionUuid = UUID.randomUUID();
    }
}
