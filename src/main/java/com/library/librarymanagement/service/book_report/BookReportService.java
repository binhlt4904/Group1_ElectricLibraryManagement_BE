package com.library.librarymanagement.service.book_report;

import com.library.librarymanagement.dto.response.BookReportResponse;
import com.library.librarymanagement.dto.response.BorrowRecordResponse;
import com.library.librarymanagement.entity.BookReport;
import org.springframework.data.domain.Page;

import java.util.Date;
import java.util.List;

public interface BookReportService {
    public BookReportResponse createReport(Long bookId,String reportType, String description);
    public BookReportResponse assignReport(Long reportId);
    public BookReportResponse updateReportStatus(Long reportId, String note);
    public Page<BookReportResponse> searchBookReport(String search, String status, String type, Date fromDate, Date toDate, int page, int size);
    public List<BookReportResponse> searchBookReportStatistic(Date fromDate, Date toDate);

    public Page<BookReportResponse> searchBookReportBySpecReader(String search, String status, String type, Date fromDate, Date toDate, int page, int size);
    public List<BookReportResponse> searchBookReportStatisticBySpecReader(Date fromDate, Date toDate);
}
