package com.edms.search.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

public class SearchDto {

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SearchRequest {
        private String keyword;
        private String documentType;
        private String status;
        private String securityLevel;
        private String dateFrom;
        private String dateTo;
        private int page;
        private int size;
    }

    @Getter
    @Builder
    public static class SearchResult {
        private UUID documentUuid;
        private String documentNumber;
        private String title;
        private String description;
        private String documentType;
        private String status;
        private String securityLevel;
        private String fileName;
        private String createdAt;
        private Double relevanceScore;
    }
}
