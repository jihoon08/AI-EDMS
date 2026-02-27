package com.edms.dashboard.controller;

import com.edms.common.dto.CommonApiResponse;
import com.edms.dashboard.dto.DashboardDto;
import com.edms.dashboard.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping
    public CommonApiResponse<DashboardDto.Overview> overview() {
        return CommonApiResponse.success(dashboardService.getOverview());
    }
}
