package com.edms.folder.domain;

import com.edms.common.domain.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "t_core_folder", schema = "edms")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Folder extends BaseEntity {

    @Id
    @Column(name = "folder_uuid")
    private UUID folderUuid;

    @Column(name = "parent_uuid")
    private UUID parentUuid;

    @Column(name = "folder_name", nullable = false)
    private String folderName;

    @Column(name = "materialized_path", nullable = false, length = 2000)
    private String materializedPath;

    @Column(nullable = false)
    private Integer depth;

    @Column(length = 500)
    private String description;

    @Column(name = "owner_uuid")
    private UUID ownerUuid;

    @Column(name = "inherit_permission", nullable = false)
    private Boolean inheritPermission;

    @Column(name = "sort_order", nullable = false)
    private Integer sortOrder;

    @Column(name = "deleted_flag", nullable = false)
    private Boolean deletedFlag;

    @Column(name = "deleted_at")
    private Instant deletedAt;

    @Version
    @Column(name = "version_seq", nullable = false)
    private Long versionSeq;

    @PrePersist
    void prePersist() {
        if (this.folderUuid == null) this.folderUuid = UUID.randomUUID();
        if (this.inheritPermission == null) this.inheritPermission = true;
        if (this.sortOrder == null) this.sortOrder = 0;
        if (this.deletedFlag == null) this.deletedFlag = false;
    }

    @Builder
    public Folder(String folderName, UUID parentUuid, String materializedPath,
                  Integer depth, String description, UUID ownerUuid) {
        this.folderName = folderName;
        this.parentUuid = parentUuid;
        this.materializedPath = materializedPath;
        this.depth = depth;
        this.description = description;
        this.ownerUuid = ownerUuid;
    }

    public void rename(String newName) {
        this.folderName = newName;
    }

    public void move(UUID newParentUuid, String newPath, Integer newDepth) {
        this.parentUuid = newParentUuid;
        this.materializedPath = newPath;
        this.depth = newDepth;
    }

    public void softDelete() {
        this.deletedFlag = true;
        this.deletedAt = Instant.now();
    }
}
