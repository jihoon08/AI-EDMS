package com.edms.share.repository;

import com.edms.share.domain.ShareLink;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ShareLinkRepository extends JpaRepository<ShareLink, UUID> {

    Optional<ShareLink> findByLinkToken(String linkToken);

    @Query("SELECT sl FROM ShareLink sl WHERE sl.documentUuid = :documentUuid AND sl.activeFlag = true")
    List<ShareLink> findActiveByDocumentUuid(@Param("documentUuid") UUID documentUuid);
}
