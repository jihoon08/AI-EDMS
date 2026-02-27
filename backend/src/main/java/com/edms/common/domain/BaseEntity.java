package com.edms.common.domain;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;
import java.util.UUID;

@Getter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class BaseEntity {

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "created_by_uuid")
    private UUID createdByUuid;

    @LastModifiedDate
    @Column(name = "updated_at")
    private Instant updatedAt;

    @Column(name = "updated_by_uuid")
    private UUID updatedByUuid;

    public void setCreatedByUuid(UUID createdByUuid) {
        this.createdByUuid = createdByUuid;
    }

    public void setUpdatedByUuid(UUID updatedByUuid) {
        this.updatedByUuid = updatedByUuid;
    }
}
