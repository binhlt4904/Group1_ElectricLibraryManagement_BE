package com.library.librarymanagement.controller.user;

import com.library.librarymanagement.service.book_report.BookReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
public class BookReportController {

    private final BookReportService bookReportService;

    @PostMapping
    @PreAuthorize("hasRole('READER')")
    public ResponseEntity<?> createReport(
            @RequestParam Long bookId,
            @RequestParam String reportType,
            @RequestParam String description) {
        return ResponseEntity.ok(bookReportService.createReport(bookId, reportType, description));
    }

    @PostMapping("/{id}/assign")
    @PreAuthorize("hasRole('LIBRARIAN')")
    public ResponseEntity<?> assignReport(
            @PathVariable Long id) {
        return ResponseEntity.ok(bookReportService.assignReport(id));
    }

    @PostMapping("/{id}/update")
    @PreAuthorize("hasRole('LIBRARIAN')")
    public ResponseEntity<?> updateStatus(
            @PathVariable Long id,
            @RequestParam(required = false) String note) {
        return ResponseEntity.ok(bookReportService.updateReportStatus(id, note));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('LIBRARIAN','ADMIN')")
    public ResponseEntity<?> getAllReports(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String reportType,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date fromDate,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date toDate,
            @RequestParam(defaultValue = "") String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "4") int size
    ) {
        return ResponseEntity.ok(bookReportService.searchBookReport(search, status,reportType,fromDate,toDate, page, size));
    }

    @GetMapping("/statistic")
    @PreAuthorize("hasAnyRole('LIBRARIAN','ADMIN')")
    public ResponseEntity<?> getAllReportsStatistic(
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date fromDate,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date toDate
    ) {
        return ResponseEntity.ok(bookReportService.searchBookReportStatistic(fromDate,toDate));
    }

    @GetMapping("/reader")
    @PreAuthorize("hasRole('READER')")
    public ResponseEntity<?> getAllReportsBySpecReader(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String reportType,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date fromDate,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date toDate,
            @RequestParam(defaultValue = "") String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "4") int size
    ) {
        return ResponseEntity.ok(bookReportService.searchBookReport(search, status,reportType,fromDate,toDate, page, size));
    }

    @GetMapping("/reader/statistic")
    @PreAuthorize("hasRole('READER')")
    public ResponseEntity<?> getAllReportsStatisticBySpecReader(
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date fromDate,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date toDate
    ) {
        return ResponseEntity.ok(bookReportService.searchBookReportStatistic(fromDate,toDate));
    }

}
