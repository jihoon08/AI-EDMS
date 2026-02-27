package com.edms.document.controller;

import com.edms.common.dto.CommonApiResponse;
import com.edms.document.dto.DocumentDto;
import com.edms.document.service.DocumentService;
import com.edms.storage.StorageService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/documents")
@RequiredArgsConstructor
public class DocumentController {

    private final DocumentService documentService;
    private final StorageService storageService;

    @GetMapping
    public CommonApiResponse<Page<DocumentDto.Response>> list(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String documentType,
            @RequestParam(required = false) String status,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return CommonApiResponse.success(
                documentService.getDocuments(keyword, documentType, status, pageable));
    }

    @GetMapping("/{documentUuid}")
    public CommonApiResponse<DocumentDto.Response> get(@PathVariable UUID documentUuid) {
        return CommonApiResponse.success(documentService.getDocument(documentUuid));
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public CommonApiResponse<DocumentDto.Response> upload(
            @Valid @RequestPart("metadata") DocumentDto.UploadRequest request,
            @RequestPart("file") MultipartFile file,
            @RequestHeader(value = "X-User-UUID", defaultValue = "00000000-0000-0000-0000-000000000000") UUID userUuid) {
        return CommonApiResponse.success(documentService.uploadDocument(request, file, userUuid));
    }

    @PutMapping("/{documentUuid}")
    public CommonApiResponse<DocumentDto.Response> update(
            @PathVariable UUID documentUuid,
            @Valid @RequestBody DocumentDto.UpdateRequest request,
            @RequestHeader(value = "X-User-UUID", defaultValue = "00000000-0000-0000-0000-000000000000") UUID userUuid) {
        return CommonApiResponse.success(documentService.updateDocument(documentUuid, request, userUuid));
    }

    @PatchMapping("/{documentUuid}/status")
    public CommonApiResponse<DocumentDto.Response> changeStatus(
            @PathVariable UUID documentUuid,
            @Valid @RequestBody DocumentDto.StatusChangeRequest request,
            @RequestHeader(value = "X-User-UUID", defaultValue = "00000000-0000-0000-0000-000000000000") UUID userUuid) {
        return CommonApiResponse.success(
                documentService.changeStatus(documentUuid, request.getStatus(), userUuid));
    }

    @DeleteMapping("/{documentUuid}")
    public CommonApiResponse<Void> delete(
            @PathVariable UUID documentUuid,
            @RequestHeader(value = "X-User-UUID", defaultValue = "00000000-0000-0000-0000-000000000000") UUID userUuid) {
        documentService.deleteDocument(documentUuid, userUuid);
        return CommonApiResponse.success();
    }

    @GetMapping("/{documentUuid}/download")
    public ResponseEntity<Resource> download(@PathVariable UUID documentUuid) {
        DocumentDto.Response doc = documentService.getDocument(documentUuid);
        Resource resource = storageService.download(doc.getStorageKey());
        String encodedName = URLEncoder.encode(doc.getFileName(), StandardCharsets.UTF_8)
                .replace("+", "%20");
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename*=UTF-8''" + encodedName)
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(resource);
    }

    // 버전 관리
    @GetMapping("/{documentUuid}/versions")
    public CommonApiResponse<List<DocumentDto.VersionResponse>> getVersions(
            @PathVariable UUID documentUuid) {
        return CommonApiResponse.success(documentService.getVersions(documentUuid));
    }

    @PostMapping(value = "/{documentUuid}/versions", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public CommonApiResponse<DocumentDto.VersionResponse> uploadNewVersion(
            @PathVariable UUID documentUuid,
            @RequestPart("file") MultipartFile file,
            @RequestParam(required = false) String changeSummary,
            @RequestHeader(value = "X-User-UUID", defaultValue = "00000000-0000-0000-0000-000000000000") UUID userUuid) {
        return CommonApiResponse.success(
                documentService.uploadNewVersion(documentUuid, file, changeSummary, userUuid));
    }
}
