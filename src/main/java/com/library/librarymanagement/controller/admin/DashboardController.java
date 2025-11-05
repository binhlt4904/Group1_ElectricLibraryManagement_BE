package com.library.librarymanagement.controller.admin;


import com.library.librarymanagement.dto.response.admin_dashboard.*;
import com.library.librarymanagement.service.adminDashboard.AdminStatisticsService;
import com.library.librarymanagement.service.adminDashboard.DashboardExportService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Year;
import java.util.List;

@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
public class DashboardController {

    private final AdminStatisticsService adminStatisticsService;
    private final DashboardExportService dashboardExportService;

    // GET /api/admin/dashboard/total-revenue
    @GetMapping("dashboard/total-revenue")
    public ResponseEntity<TotalRevenueResponse> getTotalRevenue() {
        TotalRevenueResponse response = adminStatisticsService.getTotalRevenue();
        return ResponseEntity.ok(response);
    }

    // GET /api/v1/admin/dashboard/total-books
    @GetMapping("dashboard/total-books")
    public ResponseEntity<TotalBookResponse> getTotalBooks() {
        TotalBookResponse response = adminStatisticsService.getTotalBooks();
        return ResponseEntity.ok(response);
    }

    @GetMapping("dashboard/total-readers")
    public ResponseEntity<TotalActiveReaderResponse> getActiveReaders() {
        TotalActiveReaderResponse response = adminStatisticsService.getActiveReaders();
        return ResponseEntity.ok(response);
    }

    @GetMapping("dashboard/current-borrowals")
    public ResponseEntity<CurrentBorrowalsResponse> getCurrentBorrowals() {
        return ResponseEntity.ok(adminStatisticsService.getCurrentBorrowals());
    }

    @GetMapping("dashboard/overdue-items")
    public ResponseEntity<OverdueItemsResponse> getOverdueItems() {
        return ResponseEntity.ok(adminStatisticsService.getOverdueItems());
    }

    @GetMapping("dashboard/popular-books")
    public ResponseEntity<List<PopularBookResponse>> getPopularBooks() {
        return ResponseEntity.ok(adminStatisticsService.getPopularBooks());
    }

    @GetMapping("dashboard/recent-activities")
    public ResponseEntity<List<RecentActivityResponse>> getRecentActivities() {
        return ResponseEntity.ok(adminStatisticsService.getRecentActivities());
    }

    @GetMapping("dashboard/borrowing-trends")
    public ResponseEntity<List<BorrowingTrendResponse>> getBorrowingTrendsCurrentYear() {
        return ResponseEntity.ok(adminStatisticsService.getBorrowingTrendsCurrentYear());
    }

    @GetMapping(value = "dashboard/export-excel", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public ResponseEntity<ByteArrayResource> exportDashboardExcel(
            @RequestParam(value = "year", required = false) Integer year) {

        int y = (year == null ? Year.now().getValue() : year);
        byte[] bytes = dashboardExportService.exportDashboardExcel(y);

        String filename = "dashboard_report_" + y + ".xlsx";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(
                MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
        headers.set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"");

        return ResponseEntity.ok()
                .headers(headers)
                .contentLength(bytes.length)
                .body(new ByteArrayResource(bytes));
    }
}
