package com.edms.document.service;

import com.edms.common.exception.BusinessException;
import com.edms.common.exception.ErrorCode;
import com.edms.document.domain.Document;
import com.edms.document.domain.DocumentVersion;
import com.edms.document.dto.DocumentDto;
import com.edms.document.repository.DocumentRepository;
import com.edms.document.repository.DocumentVersionRepository;
import com.edms.storage.StorageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class DocumentService {

    private final DocumentRepository documentRepository;
    private final DocumentVersionRepository versionRepository;
    private final StorageService storageService;

    @Transactional(readOnly = true)
    public Page<DocumentDto.Response> getDocuments(String keyword, String documentType,
                                                    String status, Pageable pageable) {
        Page<Document> page;
        if (keyword != null && !keyword.isBlank()) {
            page = documentRepository.searchWithKeyword(keyword, documentType, status, pageable);
        } else {
            page = documentRepository.searchWithoutKeyword(documentType, status, pageable);
        }
        return page.map(DocumentDto.Response::from);
    }

    @Transactional(readOnly = true)
    public Page<DocumentDto.Response> getDocumentsByFolder(UUID folderUuid, Pageable pageable) {
        return documentRepository.findByFolderUuid(folderUuid, pageable)
                .map(DocumentDto.Response::from);
    }

    @Transactional(readOnly = true)
    public DocumentDto.Response getDocument(UUID documentUuid) {
        Document doc = findActiveDocument(documentUuid);
        return DocumentDto.Response.from(doc);
    }

    @Transactional
    public DocumentDto.Response uploadDocument(DocumentDto.UploadRequest request,
                                                MultipartFile file, UUID userUuid) {
        String docNumber = generateDocumentNumber();
        String storageKey = storageService.generateKey("documents", file.getOriginalFilename());

        try {
            storageService.upload(storageKey, file.getInputStream(), file.getSize(), file.getContentType());
        } catch (IOException e) {
            throw new BusinessException(ErrorCode.STORAGE_UPLOAD_FAILED, e.getMessage());
        }

        Document doc = Document.builder()
                .documentNumber(docNumber)
                .title(request.getTitle())
                .description(request.getDescription())
                .documentType(request.getDocumentType())
                .securityLevel(request.getSecurityLevel())
                .folderUuid(request.getFolderUuid())
                .ownerUuid(userUuid)
                .fileName(file.getOriginalFilename())
                .fileSize(file.getSize())
                .contentType(file.getContentType())
                .storageKey(storageKey)
                .retentionPeriod(request.getRetentionPeriod())
                .build();
        doc.setCreatedByUuid(userUuid);

        documentRepository.save(doc);

        // 첫 버전 기록
        DocumentVersion version = DocumentVersion.builder()
                .documentUuid(doc.getDocumentUuid())
                .versionNumber(1)
                .fileName(file.getOriginalFilename())
                .fileSize(file.getSize())
                .contentType(file.getContentType())
                .storageKey(storageKey)
                .changeSummary("최초 업로드")
                .createdByUuid(userUuid)
                .build();
        versionRepository.save(version);

        log.info("문서 등록 완료: {} ({})", docNumber, doc.getDocumentUuid());
        return DocumentDto.Response.from(doc);
    }

    @Transactional
    public DocumentDto.Response updateDocument(UUID documentUuid,
                                                DocumentDto.UpdateRequest request, UUID userUuid) {
        Document doc = findActiveDocument(documentUuid);
        doc.updateInfo(request.getTitle(), request.getDescription(),
                request.getDocumentType(), request.getSecurityLevel(), request.getFolderUuid());
        doc.setUpdatedByUuid(userUuid);
        return DocumentDto.Response.from(doc);
    }

    @Transactional
    public DocumentDto.Response changeStatus(UUID documentUuid,
                                              String newStatus, UUID userUuid) {
        Document doc = findActiveDocument(documentUuid);
        doc.changeStatus(newStatus);
        doc.setUpdatedByUuid(userUuid);
        return DocumentDto.Response.from(doc);
    }

    @Transactional
    public void deleteDocument(UUID documentUuid, UUID userUuid) {
        Document doc = findActiveDocument(documentUuid);
        doc.softDelete();
        doc.setUpdatedByUuid(userUuid);
        log.info("문서 삭제: {} ({})", doc.getDocumentNumber(), documentUuid);
    }

    @Transactional
    public DocumentDto.VersionResponse uploadNewVersion(UUID documentUuid, MultipartFile file,
                                                         String changeSummary, UUID userUuid) {
        Document doc = findActiveDocument(documentUuid);
        String storageKey = storageService.generateKey("documents", file.getOriginalFilename());

        try {
            storageService.upload(storageKey, file.getInputStream(), file.getSize(), file.getContentType());
        } catch (IOException e) {
            throw new BusinessException(ErrorCode.STORAGE_UPLOAD_FAILED, e.getMessage());
        }

        int newVersionNumber = doc.getCurrentVersion() + 1;
        doc.incrementVersion(file.getOriginalFilename(), file.getSize(),
                file.getContentType(), storageKey);
        doc.setUpdatedByUuid(userUuid);

        DocumentVersion version = DocumentVersion.builder()
                .documentUuid(documentUuid)
                .versionNumber(newVersionNumber)
                .fileName(file.getOriginalFilename())
                .fileSize(file.getSize())
                .contentType(file.getContentType())
                .storageKey(storageKey)
                .changeSummary(changeSummary)
                .createdByUuid(userUuid)
                .build();
        versionRepository.save(version);

        log.info("새 버전 업로드: {} v{}", doc.getDocumentNumber(), newVersionNumber);
        return DocumentDto.VersionResponse.builder()
                .versionUuid(version.getVersionUuid())
                .versionNumber(newVersionNumber)
                .fileName(file.getOriginalFilename())
                .fileSize(file.getSize())
                .contentType(file.getContentType())
                .changeSummary(changeSummary)
                .createdAt(version.getCreatedAt())
                .createdByUuid(userUuid)
                .build();
    }

    @Transactional(readOnly = true)
    public List<DocumentDto.VersionResponse> getVersions(UUID documentUuid) {
        return versionRepository.findByDocumentUuidOrderByVersionNumberDesc(documentUuid)
                .stream()
                .map(v -> DocumentDto.VersionResponse.builder()
                        .versionUuid(v.getVersionUuid())
                        .versionNumber(v.getVersionNumber())
                        .fileName(v.getFileName())
                        .fileSize(v.getFileSize())
                        .contentType(v.getContentType())
                        .changeSummary(v.getChangeSummary())
                        .createdAt(v.getCreatedAt())
                        .createdByUuid(v.getCreatedByUuid())
                        .build())
                .toList();
    }

    private Document findActiveDocument(UUID documentUuid) {
        return documentRepository.findActiveById(documentUuid)
                .orElseThrow(() -> new BusinessException(ErrorCode.DOCUMENT_NOT_FOUND));
    }

    private String generateDocumentNumber() {
        String prefix = "DOC-" + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")) + "-";
        int maxSeq = documentRepository.findMaxSequence(prefix);
        return prefix + String.format("%04d", maxSeq + 1);
    }
}
