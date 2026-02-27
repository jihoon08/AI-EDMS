package com.edms.dashboard.service;

import com.edms.dashboard.dto.DashboardDto;
import com.edms.document.domain.Document;
import com.edms.document.repository.DocumentRepository;
import com.edms.folder.repository.FolderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final DocumentRepository documentRepository;
    private final FolderRepository folderRepository;

    @Transactional(readOnly = true)
    public DashboardDto.Overview getOverview() {
        var allDocs = documentRepository.findAllActive(
                PageRequest.of(0, 1000, Sort.by(Sort.Direction.DESC, "createdAt")));

        List<Document> documents = allDocs.getContent();

        // 오늘 등록된 문서
        Instant todayStart = LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant();
        long todayCount = documents.stream()
                .filter(d -> d.getCreatedAt() != null && d.getCreatedAt().isAfter(todayStart))
                .count();

        // 상태별 집계
        Map<String, Long> byStatus = documents.stream()
                .collect(Collectors.groupingBy(Document::getStatus, LinkedHashMap::new, Collectors.counting()));

        // 유형별 집계
        Map<String, Long> byType = documents.stream()
                .collect(Collectors.groupingBy(Document::getDocumentType, LinkedHashMap::new, Collectors.counting()));

        // 최근 문서 5건
        List<DashboardDto.RecentDocument> recent = documents.stream()
                .limit(5)
                .map(d -> DashboardDto.RecentDocument.builder()
                        .documentUuid(d.getDocumentUuid().toString())
                        .documentNumber(d.getDocumentNumber())
                        .title(d.getTitle())
                        .documentType(d.getDocumentType())
                        .status(d.getStatus())
                        .fileName(d.getFileName())
                        .createdAt(d.getCreatedAt() != null ? d.getCreatedAt().toString() : null)
                        .build())
                .toList();

        long totalFolders = folderRepository.findAllActive().size();

        return DashboardDto.Overview.builder()
                .totalDocuments(allDocs.getTotalElements())
                .todayDocuments(todayCount)
                .totalFolders(totalFolders)
                .byStatus(byStatus)
                .byType(byType)
                .recentDocuments(recent)
                .build();
    }
}
