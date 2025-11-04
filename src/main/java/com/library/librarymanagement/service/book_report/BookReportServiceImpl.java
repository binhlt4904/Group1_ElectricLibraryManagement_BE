package com.library.librarymanagement.service.book_report;

import com.library.librarymanagement.dto.response.BookReportResponse;
import com.library.librarymanagement.entity.Account;
import com.library.librarymanagement.entity.Book;
import com.library.librarymanagement.entity.BookReport;
import com.library.librarymanagement.entity.BorrowRecord;
import com.library.librarymanagement.repository.account.AccountRepository;
import com.library.librarymanagement.repository.book_report.BookReportRepository;
import com.library.librarymanagement.repository.user.BookRepository;
import com.library.librarymanagement.service.custom_user_details.CustomUserDetails;
import com.library.librarymanagement.util.Mapper;
import jakarta.persistence.criteria.JoinType;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookReportServiceImpl implements BookReportService {

    private final BookReportRepository bookReportRepository;
    private final BookRepository bookRepository;
    private final AccountRepository accountRepository;
    private final ReportEventPublisher reportEventPublisher;

    @Override
    @Transactional
    public BookReportResponse createReport(Long bookId, String reportType, String description) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new IllegalArgumentException("Book not found"));

        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Long accountId;
        if (principal instanceof CustomUserDetails userDetails) {
            accountId = userDetails.getAccountId();
        } else {
            throw new RuntimeException("Cannot extract accountId: invalid principal");
        }

        Account reporter = accountRepository.findById(accountId)
                .orElseThrow(() -> new IllegalArgumentException("Account not found"));

        BookReport report = BookReport.builder()
                .book(book)
                .reporter(reporter)
                .reportType(reportType)
                .description(description)
                .status("PENDING")
                .locked(false)
                .build();
        BookReport saved = bookReportRepository.save(report);

        //  Gửi thông báo realtime cho tất cả staff
        reportEventPublisher.publishNewReportEvent(Map.of(
                "id", saved.getId(),
                "bookTitle", book.getTitle(),
                "reporterName", reporter.getFullName(),
                "reportType", reportType,
                "description", description,
                "status", saved.getStatus(),
                "createdAt", saved.getCreatedAt()
        ));
        return Mapper.mapEntityToDTO(saved); //todo: notice
    }

    @Override
    @Transactional
    public BookReportResponse assignReport(Long reportId) { //TODO: STAFFid

        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Long accountId;
        if (principal instanceof CustomUserDetails userDetails) {
            accountId = userDetails.getAccountId();
        } else {
            throw new RuntimeException("Cannot extract accountId: invalid principal");
        }

        BookReport report = bookReportRepository.findById(reportId)
                .orElseThrow(() -> new IllegalArgumentException("Report not found"));

        if (report.getLocked() && report.getStaff() != null && !report.getStaff().getId().equals(accountId)) {
            throw new IllegalStateException("Report is currently handled by another staff");
        }


        Account staff = accountRepository.findById(accountId)
                .orElseThrow(() -> new IllegalArgumentException("Account not found"));

        report.setLocked(true);
        report.setStaff(staff);
        report.setStatus("PROCESSING");

        try {
            BookReport result = bookReportRepository.save(report);
            return Mapper.mapEntityToDTO(result);
        } catch (Exception e) {
            throw new IllegalStateException("Report was updated by another staff, please refresh");
        }
    }

    @Override
    @Transactional
    public BookReportResponse updateReportStatus(Long reportId,String note) {

        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Long accountId;
        if (principal instanceof CustomUserDetails userDetails) {
            accountId = userDetails.getAccountId();
        } else {
            throw new RuntimeException("Cannot extract accountId: invalid principal");
        }

        BookReport report = bookReportRepository.findById(reportId)
                .orElseThrow(() -> new IllegalArgumentException("Report not found"));

        //todo: consider !report.getLocked()
        if (!report.getLocked() || report.getStaff() == null || !report.getStaff().getId().equals(accountId)) {
            throw new IllegalStateException("You are not assigned to this report");
        }

        report.setStatus("RESOLVED");
        report.setNote(note);
        report.setLocked(false);
//        if (newStatus.equals("RESOLVED")) {
//            report.setLocked(false);
//        }

        try {
            BookReport result = bookReportRepository.save(report);
            return Mapper.mapEntityToDTO(result);
        } catch (Exception e) {
            throw new IllegalStateException("Report was updated by another staff, please refresh");
        }
    }

    @Override
    @Transactional
    public Page<BookReportResponse> searchBookReport(String search, String status, String reportType, Date fromDate, Date toDate, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Specification<BookReport> specification = Specification.allOf();

        if (search != null && !search.isBlank()) {
            specification = specification
                    .and((root, query, cb) -> cb.or(
                            cb.like(cb.lower(root.join("book").get("title")), "%" + search.toLowerCase() + "%"),
                            cb.like(cb.lower(root.join("book").join("author").get("fullName")), "%" + search.toLowerCase() + "%"),
                            cb.like(cb.lower(root.get("description")), "%" + search.toLowerCase() + "%"),
                            cb.like(cb.lower(root.join("reporter").get("fullName")),"%" + search.toLowerCase() + "%"),
                            cb.like(cb.lower(root.join("staff", JoinType.LEFT).get("fullName")),"%" + search.toLowerCase() + "%")
                    ));
        }

        if (status != null && !status.isBlank()) {
            specification = specification.and((root, query, cb) ->
                    cb.equal(root.get("status"), status)
            );
        }

        if (reportType != null && !reportType.isBlank()) {
            specification = specification.and((root, query, cb) ->
                    cb.equal(root.get("reportType"), reportType)
            );
        }

        if (fromDate != null){
            specification = specification.and((root, query, cb) ->
                    cb.greaterThanOrEqualTo(root.get("createdAt"), fromDate)
            );
        }

        if (toDate != null){
            specification = specification.and((root, query, cb) ->
                    cb.lessThanOrEqualTo(root.get("createdAt"), toDate)
            );
        }
        Page<BookReport> result = bookReportRepository.findAll(specification, pageable);
        return result.map((record) -> {
            return Mapper.mapEntityToDTO(record);
        });
    }

    @Override
    public List<BookReportResponse> searchBookReportStatistic(Date fromDate, Date toDate) {
        Specification<BookReport> specification = Specification.allOf();

        if (fromDate != null){
            specification = specification.and((root, query, cb) ->
                    cb.greaterThanOrEqualTo(root.get("createdAt"), fromDate)
            );
        }

        if (toDate != null){
            specification = specification.and((root, query, cb) ->
                    cb.lessThanOrEqualTo(root.get("createdAt"), toDate)
            );
        }

        List<BookReport> result = bookReportRepository.findAll(specification);
        return result.stream().map((record) -> {
            return Mapper.mapEntityToDTO(record);
        }).collect(Collectors.toList());
    }

    @Override
    public Page<BookReportResponse> searchBookReportBySpecReader(String search, String status, String reportType, Date fromDate, Date toDate, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Specification<BookReport> specification = Specification.allOf();

        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Long accountId;
        if (principal instanceof CustomUserDetails userDetails) {
            accountId = userDetails.getAccountId();
        } else {
            throw new RuntimeException("Cannot extract accountId: invalid principal");
        }

        specification = specification.and((root, query, cb) ->
                cb.equal(root.get("reporter").get("id"), accountId));

        if (search != null && !search.isBlank()) {
            specification = specification
                    .and((root, query, cb) -> cb.or(
                            cb.like(cb.lower(root.join("book").get("title")), "%" + search.toLowerCase() + "%"),
                            cb.like(cb.lower(root.join("book").join("author").get("fullName")), "%" + search.toLowerCase() + "%"),
                            cb.like(cb.lower(root.get("description")), "%" + search.toLowerCase() + "%"),
                            cb.like(cb.lower(root.join("reporter").get("fullName")),"%" + search.toLowerCase() + "%"),
                            cb.like(cb.lower(root.join("staff", JoinType.LEFT).get("fullName")),"%" + search.toLowerCase() + "%")
                    ));
        }

        if (status != null && !status.isBlank()) {
            specification = specification.and((root, query, cb) ->
                    cb.equal(root.get("status"), status)
            );
        }

        if (reportType != null && !reportType.isBlank()) {
            specification = specification.and((root, query, cb) ->
                    cb.equal(root.get("reportType"), reportType)
            );
        }

        if (fromDate != null){
            specification = specification.and((root, query, cb) ->
                    cb.greaterThanOrEqualTo(root.get("createdAt"), fromDate)
            );
        }

        if (toDate != null){
            specification = specification.and((root, query, cb) ->
                    cb.lessThanOrEqualTo(root.get("createdAt"), toDate)
            );
        }
        Page<BookReport> result = bookReportRepository.findAll(specification, pageable);
        return result.map((record) -> {
            return Mapper.mapEntityToDTO(record);
        });
    }

    @Override
    public List<BookReportResponse> searchBookReportStatisticBySpecReader(Date fromDate, Date toDate) {
        Specification<BookReport> specification = Specification.allOf();
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Long accountId;
        if (principal instanceof CustomUserDetails userDetails) {
            accountId = userDetails.getAccountId();
        } else {
            throw new RuntimeException("Cannot extract accountId: invalid principal");
        }

        specification = specification.and((root, query, cb) ->
                cb.equal(root.get("reporter").get("id"), accountId));

        if (fromDate != null){
            specification = specification.and((root, query, cb) ->
                    cb.greaterThanOrEqualTo(root.get("createdAt"), fromDate)
            );
        }

        if (toDate != null){
            specification = specification.and((root, query, cb) ->
                    cb.lessThanOrEqualTo(root.get("createdAt"), toDate)
            );
        }

        List<BookReport> result = bookReportRepository.findAll(specification);
        return result.stream().map((record) -> {
            return Mapper.mapEntityToDTO(record);
        }).collect(Collectors.toList());
    }
}
