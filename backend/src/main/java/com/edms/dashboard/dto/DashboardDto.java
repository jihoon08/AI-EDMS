package com.edms.dashboard.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;
import java.util.Map;

public class DashboardDto {

    @Getter
    @Builder
    public static class Overview {
        private long totalDocuments;
        private long todayDocuments;
        private long totalFolders;
        private Map<String, Long> byStatus;
        private Map<String, Long> byType;
        private List<RecentDocument> recentDocuments;
    }

    @Getter
    @Builder
    public static class RecentDocument {
        private String documentUuid;
        private String documentNumber;
        private String title;
        private String documentType;
        private String status;
        private String fileName;
        private String createdAt;
    }
}
