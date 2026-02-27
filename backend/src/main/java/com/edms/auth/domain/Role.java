package com.edms.auth.domain;

import com.edms.common.domain.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Table(name = "t_auth_role", schema = "edms")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Role extends BaseEntity {

    @Id
    @Column(name = "role_uuid")
    private UUID roleUuid;

    @Column(name = "role_code", nullable = false, unique = true, length = 50)
    private String roleCode;

    @Column(name = "role_name", nullable = false, length = 100)
    private String roleName;

    @Column(length = 500)
    private String description;

    @Column(name = "sort_order", nullable = false)
    private Integer sortOrder;

    @Column(name = "active_flag", nullable = false)
    private Boolean activeFlag;

    @PrePersist
    void prePersist() {
        if (this.roleUuid == null) this.roleUuid = UUID.randomUUID();
        if (this.sortOrder == null) this.sortOrder = 0;
        if (this.activeFlag == null) this.activeFlag = true;
    }
}
