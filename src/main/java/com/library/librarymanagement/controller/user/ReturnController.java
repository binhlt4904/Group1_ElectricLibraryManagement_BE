package com.library.librarymanagement.controller.user;

import com.library.librarymanagement.dto.response.ApiResponse;
import com.library.librarymanagement.dto.response.ApiReturnRecordResponse;
import com.library.librarymanagement.dto.response.ReturnRecordResponse;
import com.library.librarymanagement.entity.ReturnRecord;
import com.library.librarymanagement.service.return_record.ReturnRecordService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/return")
public class ReturnController {
    private final ReturnRecordService returnRecordService;

    @PostMapping("/{borrowId}")
    @PreAuthorize("hasRole('READER')")
    public ResponseEntity<ApiReturnRecordResponse<ReturnRecordResponse>> addReturnRecord(@PathVariable Long borrowId, @RequestParam(required = false) String note) {
        try {
            ReturnRecordResponse result = returnRecordService.returnRecord(borrowId, note);
            return ResponseEntity.ok(
                    ApiReturnRecordResponse.<ReturnRecordResponse>builder()
                            .success(true)
                            .message("Book returned successfully")
                            .data(result)
                            .build()
            );
        } catch (RuntimeException ex) {
            return ResponseEntity.badRequest().body(
                    ApiReturnRecordResponse.<ReturnRecordResponse>builder()
                            .success(false)
                            .message(ex.getMessage())
                            .data(null)
                            .build()
            );
        }
    }
}
