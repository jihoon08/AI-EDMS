package com.edms.search.service;

import com.edms.document.domain.Document;
import com.edms.document.repository.DocumentRepository;
import com.edms.search.dto.SearchDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class SearchService {

    private final DocumentRepository documentRepository;

    @Transactional(readOnly = true)
    public Page<SearchDto.SearchResult> search(SearchDto.SearchRequest request) {
        PageRequest pageable = PageRequest.of(
                request.getPage() > 0 ? request.getPage() : 0,
                request.getSize() > 0 ? request.getSize() : 20);

        // 키워드 기반 검색 (기존 DocumentRepository 재사용)
        Page<Document> documents;
        String keyword = request.getKeyword();
        String docType = request.getDocumentType();
        String status = request.getStatus();

        if (keyword != null && !keyword.isBlank()) {
            documents = documentRepository.searchWithKeyword(keyword, docType, status, pageable);
        } else {
            documents = documentRepository.searchWithoutKeyword(docType, status, pageable);
        }

        return documents.map(doc -> SearchDto.SearchResult.builder()
                .documentUuid(doc.getDocumentUuid())
                .documentNumber(doc.getDocumentNumber())
                .title(doc.getTitle())
                .description(doc.getDescription())
                .documentType(doc.getDocumentType())
                .status(doc.getStatus())
                .securityLevel(doc.getSecurityLevel())
                .fileName(doc.getFileName())
                .createdAt(doc.getCreatedAt() != null ? doc.getCreatedAt().toString() : null)
                .relevanceScore(1.0)
                .build());
    }
}
