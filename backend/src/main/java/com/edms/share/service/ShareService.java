package com.edms.share.service;

import com.edms.common.exception.BusinessException;
import com.edms.common.exception.ErrorCode;
import com.edms.share.domain.AccessRequest;
import com.edms.share.domain.DocumentShare;
import com.edms.share.domain.ShareLink;
import com.edms.share.dto.ShareDto;
import com.edms.share.repository.AccessRequestRepository;
import com.edms.share.repository.DocumentShareRepository;
import com.edms.share.repository.ShareLinkRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ShareService {

    private final ShareLinkRepository shareLinkRepository;
    private final DocumentShareRepository documentShareRepository;
    private final AccessRequestRepository accessRequestRepository;

    @Value("${app.base-url:http://localhost:3000}")
    private String baseUrl;

    // 공유 링크 생성
    @Transactional
    public ShareDto.ShareLinkResponse createShareLink(ShareDto.CreateShareLinkRequest request, UUID creatorUuid) {
        String token = UUID.randomUUID().toString().replace("-", "");
        Instant expiresAt = request.getExpiresInDays() != null
                ? Instant.now().plusSeconds((long) request.getExpiresInDays() * 24 * 3600)
                : null;

        ShareLink link = ShareLink.builder()
                .documentUuid(request.getDocumentUuid())
                .linkToken(token)
                .permissionLevel(request.getPermissionLevel() != null ? request.getPermissionLevel() : "READ")
                .passwordHash(request.getPassword())
                .requireLogin(request.getRequireLogin())
                .maxAccessCount(request.getMaxAccessCount())
                .expiresAt(expiresAt)
                .build();
        link.setCreatedByUuid(creatorUuid);

        shareLinkRepository.save(link);
        log.info("공유 링크 생성: {} (문서: {})", token, request.getDocumentUuid());
        return ShareDto.ShareLinkResponse.from(link, baseUrl);
    }

    @Transactional(readOnly = true)
    public List<ShareDto.ShareLinkResponse> getShareLinks(UUID documentUuid) {
        return shareLinkRepository.findActiveByDocumentUuid(documentUuid).stream()
                .map(sl -> ShareDto.ShareLinkResponse.from(sl, baseUrl))
                .toList();
    }

    @Transactional
    public void deactivateShareLink(UUID linkUuid) {
        ShareLink link = shareLinkRepository.findById(linkUuid)
                .orElseThrow(() -> new BusinessException(ErrorCode.COMMON_NOT_FOUND));
        link.deactivate();
    }

    // 사용자 직접 공유
    @Transactional
    public ShareDto.DocumentShareResponse shareWithUser(ShareDto.CreateDocumentShareRequest request, UUID creatorUuid) {
        DocumentShare share = DocumentShare.builder()
                .documentUuid(request.getDocumentUuid())
                .sharedWithUuid(request.getSharedWithUuid())
                .permissionLevel(request.getPermissionLevel())
                .message(request.getMessage())
                .createdByUuid(creatorUuid)
                .build();

        documentShareRepository.save(share);
        log.info("문서 공유: {} → 사용자 {}", request.getDocumentUuid(), request.getSharedWithUuid());
        return ShareDto.DocumentShareResponse.from(share);
    }

    @Transactional(readOnly = true)
    public List<ShareDto.DocumentShareResponse> getDocumentShares(UUID documentUuid) {
        return documentShareRepository.findByDocumentUuid(documentUuid).stream()
                .map(ShareDto.DocumentShareResponse::from)
                .toList();
    }

    @Transactional
    public void removeShare(UUID shareUuid) {
        documentShareRepository.deleteById(shareUuid);
    }

    // 접근 요청
    @Transactional
    public ShareDto.AccessRequestResponse createAccessRequest(
            ShareDto.CreateAccessRequestRequest request, UUID requesterUuid, UUID ownerUuid) {
        AccessRequest ar = AccessRequest.builder()
                .targetType(request.getTargetType())
                .targetUuid(request.getTargetUuid())
                .requesterUuid(requesterUuid)
                .ownerUuid(ownerUuid)
                .requestType(request.getRequestType())
                .reason(request.getReason())
                .requestDays(request.getRequestDays())
                .build();
        ar.setCreatedByUuid(requesterUuid);

        accessRequestRepository.save(ar);
        log.info("접근 권한 요청: {} → {} ({})", requesterUuid, request.getTargetUuid(), request.getTargetType());
        return ShareDto.AccessRequestResponse.from(ar);
    }

    @Transactional(readOnly = true)
    public Page<ShareDto.AccessRequestResponse> getPendingRequests(UUID ownerUuid, Pageable pageable) {
        return accessRequestRepository.findPendingByOwnerUuid(ownerUuid, pageable)
                .map(ShareDto.AccessRequestResponse::from);
    }

    @Transactional(readOnly = true)
    public Page<ShareDto.AccessRequestResponse> getMyRequests(UUID requesterUuid, Pageable pageable) {
        return accessRequestRepository.findByRequesterUuid(requesterUuid, pageable)
                .map(ShareDto.AccessRequestResponse::from);
    }

    @Transactional
    public ShareDto.AccessRequestResponse approveRequest(UUID requestUuid, UUID decidedByUuid) {
        AccessRequest ar = accessRequestRepository.findById(requestUuid)
                .orElseThrow(() -> new BusinessException(ErrorCode.COMMON_NOT_FOUND));
        ar.approve(decidedByUuid);
        log.info("접근 요청 승인: {}", requestUuid);
        return ShareDto.AccessRequestResponse.from(ar);
    }

    @Transactional
    public ShareDto.AccessRequestResponse rejectRequest(UUID requestUuid, UUID decidedByUuid, String reason) {
        AccessRequest ar = accessRequestRepository.findById(requestUuid)
                .orElseThrow(() -> new BusinessException(ErrorCode.COMMON_NOT_FOUND));
        ar.reject(decidedByUuid, reason);
        log.info("접근 요청 반려: {}", requestUuid);
        return ShareDto.AccessRequestResponse.from(ar);
    }
}
