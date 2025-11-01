package com.library.librarymanagement.service.borrow;

import com.library.librarymanagement.dto.response.BorrowRecordResponse;
import com.library.librarymanagement.entity.BorrowRecord;
import org.springframework.data.domain.Page;

import java.util.Date;

public interface BorrowService {
    void borrowBook(Long bookId, Date allowDate);
    Page<BorrowRecordResponse> searchBorrowRecords(String search, String status, Date fromDate, Date toDate, int page, int size);
}
