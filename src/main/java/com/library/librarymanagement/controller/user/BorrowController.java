package com.library.librarymanagement.controller.user;

import com.library.librarymanagement.dto.response.ApiResponse;
import com.library.librarymanagement.dto.response.BorrowRecordResponse;
import com.library.librarymanagement.entity.BorrowRecord;
import com.library.librarymanagement.service.borrow.BorrowService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/borrow")
public class BorrowController {

    private final BorrowService borrowService;

    //todo: can fix into pathVariable
    @PostMapping()
    @PreAuthorize("hasRole('READER')")
    public ResponseEntity<ApiResponse> borrow(@RequestParam Long bookId, @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date allowDate) {
        borrowService.borrowBook(bookId, allowDate);
        return ResponseEntity.ok(new ApiResponse(true, "Borrow Successful"));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('STAFF', 'ADMIN')")
    public ResponseEntity<Page<BorrowRecordResponse>> getAllBorrowRecords(
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "") String status,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date fromDate,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date toDate,
            @RequestParam(required = false, defaultValue = "0") int page,
            @RequestParam(defaultValue = "4") int size
    ) {
        System.out.print("Page : " + page + " Size : " + size);
        Page<BorrowRecordResponse> result = borrowService.searchBorrowRecords(search, status, fromDate, toDate, page, size);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/statistics")
    @PreAuthorize("hasAnyRole('STAFF', 'ADMIN')")
    public ResponseEntity<List<BorrowRecordResponse>> getAllBorrowStatics(
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date fromDate,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date toDate
    ) {
        List<BorrowRecordResponse> result = borrowService.searchBorrowRecordsStatistic(fromDate, toDate);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/history")
    @PreAuthorize("hasRole('READER')")
    public ResponseEntity<Page<BorrowRecordResponse>> getBorrowalsBySpecReader(
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "") String status,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date fromDate,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date toDate,
            @RequestParam(required = false, defaultValue = "0") int page,
            @RequestParam(defaultValue = "4") int size) {
        Page<BorrowRecordResponse> result = borrowService.searchBorrowRecordsBySpecReader(search, status, fromDate, toDate, page, size);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/history/statistics")
    @PreAuthorize("hasRole('READER')")
    public ResponseEntity<List<BorrowRecordResponse>> getBorrowalsStatisticBySpecReader(
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date fromDate,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date toDate) {
        List<BorrowRecordResponse> result = borrowService.searchBorrowRecordsStatisticBySpecReader(fromDate, toDate);
        return ResponseEntity.ok(result);
    }
}
