package com.edms.search.controller;

import com.edms.common.dto.CommonApiResponse;
import com.edms.search.dto.SearchDto;
import com.edms.search.service.SearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/search")
@RequiredArgsConstructor
public class SearchController {

    private final SearchService searchService;

    @GetMapping
    public ResponseEntity<CommonApiResponse<Page<SearchDto.SearchResult>>> search(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String documentType,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String securityLevel,
            @RequestParam(required = false) String dateFrom,
            @RequestParam(required = false) String dateTo,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        SearchDto.SearchRequest request = SearchDto.SearchRequest.builder()
                .keyword(keyword)
                .documentType(documentType)
                .status(status)
                .securityLevel(securityLevel)
                .dateFrom(dateFrom)
                .dateTo(dateTo)
                .page(page)
                .size(size)
                .build();

        return ResponseEntity.ok(CommonApiResponse.success(searchService.search(request)));
    }
}
