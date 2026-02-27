package com.edms.folder.controller;

import com.edms.common.dto.CommonApiResponse;
import com.edms.document.dto.DocumentDto;
import com.edms.document.service.DocumentService;
import com.edms.folder.dto.FolderDto;
import com.edms.folder.service.FolderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/folders")
@RequiredArgsConstructor
public class FolderController {

    private final FolderService folderService;
    private final DocumentService documentService;

    @GetMapping("/tree")
    public CommonApiResponse<List<FolderDto.TreeNode>> tree() {
        return CommonApiResponse.success(folderService.getFolderTree());
    }

    @GetMapping
    public CommonApiResponse<List<FolderDto.Response>> list(
            @RequestParam(required = false) UUID parentUuid) {
        return CommonApiResponse.success(folderService.getChildren(parentUuid));
    }

    @GetMapping("/{folderUuid}")
    public CommonApiResponse<FolderDto.Response> get(@PathVariable UUID folderUuid) {
        return CommonApiResponse.success(folderService.getFolder(folderUuid));
    }

    @PostMapping
    public CommonApiResponse<FolderDto.Response> create(
            @Valid @RequestBody FolderDto.CreateRequest request,
            @RequestHeader(value = "X-User-UUID", defaultValue = "00000000-0000-0000-0000-000000000000") UUID userUuid) {
        return CommonApiResponse.success(folderService.createFolder(request, userUuid));
    }

    @PutMapping("/{folderUuid}")
    public CommonApiResponse<FolderDto.Response> update(
            @PathVariable UUID folderUuid,
            @Valid @RequestBody FolderDto.UpdateRequest request,
            @RequestHeader(value = "X-User-UUID", defaultValue = "00000000-0000-0000-0000-000000000000") UUID userUuid) {
        return CommonApiResponse.success(folderService.updateFolder(folderUuid, request, userUuid));
    }

    @DeleteMapping("/{folderUuid}")
    public CommonApiResponse<Void> delete(
            @PathVariable UUID folderUuid,
            @RequestHeader(value = "X-User-UUID", defaultValue = "00000000-0000-0000-0000-000000000000") UUID userUuid) {
        folderService.deleteFolder(folderUuid, userUuid);
        return CommonApiResponse.success();
    }

    @GetMapping("/{folderUuid}/documents")
    public CommonApiResponse<Page<DocumentDto.Response>> documents(
            @PathVariable UUID folderUuid,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return CommonApiResponse.success(documentService.getDocumentsByFolder(folderUuid, pageable));
    }
}
