package com.edms.share.controller;

import com.edms.common.dto.CommonApiResponse;
import com.edms.share.dto.ShareDto;
import com.edms.share.service.ShareService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/shares")
@RequiredArgsConstructor
public class ShareController {

    private final ShareService shareService;

    // 공유 링크
    @PostMapping("/links")
    public CommonApiResponse<ShareDto.ShareLinkResponse> createShareLink(
            @RequestBody ShareDto.CreateShareLinkRequest request,
            @RequestHeader(value = "X-User-UUID", defaultValue = "00000000-0000-0000-0000-000000000000") UUID userUuid) {
        return CommonApiResponse.success(shareService.createShareLink(request, userUuid));
    }

    @GetMapping("/links/document/{documentUuid}")
    public CommonApiResponse<List<ShareDto.ShareLinkResponse>> getShareLinks(
            @PathVariable UUID documentUuid) {
        return CommonApiResponse.success(shareService.getShareLinks(documentUuid));
    }

    @DeleteMapping("/links/{linkUuid}")
    public CommonApiResponse<Void> deactivateShareLink(@PathVariable UUID linkUuid) {
        shareService.deactivateShareLink(linkUuid);
        return CommonApiResponse.success();
    }

    // 사용자 직접 공유
    @PostMapping("/users")
    public CommonApiResponse<ShareDto.DocumentShareResponse> shareWithUser(
            @RequestBody ShareDto.CreateDocumentShareRequest request,
            @RequestHeader(value = "X-User-UUID", defaultValue = "00000000-0000-0000-0000-000000000000") UUID userUuid) {
        return CommonApiResponse.success(shareService.shareWithUser(request, userUuid));
    }

    @GetMapping("/users/document/{documentUuid}")
    public CommonApiResponse<List<ShareDto.DocumentShareResponse>> getDocumentShares(
            @PathVariable UUID documentUuid) {
        return CommonApiResponse.success(shareService.getDocumentShares(documentUuid));
    }

    @DeleteMapping("/users/{shareUuid}")
    public CommonApiResponse<Void> removeShare(@PathVariable UUID shareUuid) {
        shareService.removeShare(shareUuid);
        return CommonApiResponse.success();
    }

    // 접근 요청
    @PostMapping("/access-requests")
    public CommonApiResponse<ShareDto.AccessRequestResponse> createAccessRequest(
            @RequestBody ShareDto.CreateAccessRequestRequest request,
            @RequestHeader(value = "X-User-UUID", defaultValue = "00000000-0000-0000-0000-000000000000") UUID requesterUuid,
            @RequestParam UUID ownerUuid) {
        return CommonApiResponse.success(shareService.createAccessRequest(request, requesterUuid, ownerUuid));
    }

    @GetMapping("/access-requests/pending")
    public CommonApiResponse<Page<ShareDto.AccessRequestResponse>> getPendingRequests(
            @RequestHeader(value = "X-User-UUID", defaultValue = "00000000-0000-0000-0000-000000000000") UUID userUuid,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return CommonApiResponse.success(shareService.getPendingRequests(userUuid, pageable));
    }

    @GetMapping("/access-requests/mine")
    public CommonApiResponse<Page<ShareDto.AccessRequestResponse>> getMyRequests(
            @RequestHeader(value = "X-User-UUID", defaultValue = "00000000-0000-0000-0000-000000000000") UUID userUuid,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return CommonApiResponse.success(shareService.getMyRequests(userUuid, pageable));
    }

    @PatchMapping("/access-requests/{requestUuid}/approve")
    public CommonApiResponse<ShareDto.AccessRequestResponse> approveRequest(
            @PathVariable UUID requestUuid,
            @RequestHeader(value = "X-User-UUID", defaultValue = "00000000-0000-0000-0000-000000000000") UUID userUuid) {
        return CommonApiResponse.success(shareService.approveRequest(requestUuid, userUuid));
    }

    @PatchMapping("/access-requests/{requestUuid}/reject")
    public CommonApiResponse<ShareDto.AccessRequestResponse> rejectRequest(
            @PathVariable UUID requestUuid,
            @RequestBody ShareDto.DecideAccessRequest request,
            @RequestHeader(value = "X-User-UUID", defaultValue = "00000000-0000-0000-0000-000000000000") UUID userUuid) {
        return CommonApiResponse.success(shareService.rejectRequest(requestUuid, userUuid, request.getRejectReason()));
    }
}
