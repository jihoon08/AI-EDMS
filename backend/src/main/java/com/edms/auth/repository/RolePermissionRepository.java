package com.edms.auth.repository;

import com.edms.auth.domain.RolePermission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Set;
import java.util.UUID;

public interface RolePermissionRepository extends JpaRepository<RolePermission, UUID> {

    @Query("SELECT rp FROM RolePermission rp JOIN FETCH rp.permission " +
           "WHERE rp.roleUuid IN :roleUuids")
    List<RolePermission> findByRoleUuidsWithPermission(@Param("roleUuids") Set<UUID> roleUuids);
}
