package com.edms.share.repository;

import com.edms.share.domain.AccessRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.UUID;

public interface AccessRequestRepository extends JpaRepository<AccessRequest, UUID> {

    @Query("SELECT ar FROM AccessRequest ar WHERE ar.ownerUuid = :ownerUuid ORDER BY ar.createdAt DESC")
    Page<AccessRequest> findByOwnerUuid(@Param("ownerUuid") UUID ownerUuid, Pageable pageable);

    @Query("SELECT ar FROM AccessRequest ar WHERE ar.requesterUuid = :requesterUuid ORDER BY ar.createdAt DESC")
    Page<AccessRequest> findByRequesterUuid(@Param("requesterUuid") UUID requesterUuid, Pageable pageable);

    @Query("SELECT ar FROM AccessRequest ar WHERE ar.ownerUuid = :ownerUuid AND ar.status = 'PENDING' ORDER BY ar.createdAt DESC")
    Page<AccessRequest> findPendingByOwnerUuid(@Param("ownerUuid") UUID ownerUuid, Pageable pageable);
}
