package com.library.librarymanagement.controller.user;

import com.library.librarymanagement.dto.response.ApiResponse;
import com.library.librarymanagement.service.borrow.BorrowService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/borrow")
public class BorrowController {

    private final BorrowService borrowService;

    @PostMapping()
    @PreAuthorize("hasRole('READER')")
    public ResponseEntity<ApiResponse> borrow(@RequestParam Long bookId, @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date allowDate) {
        borrowService.borrowBook(bookId, allowDate);
        return ResponseEntity.ok(new ApiResponse(true, "Borrow Successful"));
    }
}
