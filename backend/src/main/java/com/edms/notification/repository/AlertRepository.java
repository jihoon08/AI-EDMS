package com.edms.notification.repository;

import com.edms.notification.domain.Alert;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.UUID;

public interface AlertRepository extends JpaRepository<Alert, UUID> {

    Page<Alert> findByUserUuidOrderByCreatedAtDesc(UUID userUuid, Pageable pageable);

    Page<Alert> findByUserUuidAndReadFlagFalseOrderByCreatedAtDesc(UUID userUuid, Pageable pageable);

    long countByUserUuidAndReadFlagFalse(UUID userUuid);

    @Modifying
    @Query("UPDATE Alert a SET a.readFlag = true, a.readAt = CURRENT_TIMESTAMP WHERE a.userUuid = :userUuid AND a.readFlag = false")
    int markAllAsRead(@Param("userUuid") UUID userUuid);
}
