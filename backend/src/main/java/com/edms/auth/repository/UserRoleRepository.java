package com.edms.auth.repository;

import com.edms.auth.domain.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface UserRoleRepository extends JpaRepository<UserRole, UUID> {

    @Query("SELECT ur FROM UserRole ur JOIN FETCH ur.role WHERE ur.userUuid = :userUuid")
    List<UserRole> findByUserUuidWithRole(@Param("userUuid") UUID userUuid);

    void deleteByUserUuidAndRoleUuid(UUID userUuid, UUID roleUuid);
}
