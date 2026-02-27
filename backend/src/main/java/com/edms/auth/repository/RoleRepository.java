package com.edms.auth.repository;

import com.edms.auth.domain.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RoleRepository extends JpaRepository<Role, UUID> {

    Optional<Role> findByRoleCode(String roleCode);

    @Query("SELECT r FROM Role r WHERE r.activeFlag = true ORDER BY r.sortOrder")
    List<Role> findAllActive();
}
