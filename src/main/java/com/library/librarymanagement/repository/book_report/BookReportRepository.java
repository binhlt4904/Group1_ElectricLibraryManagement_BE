package com.library.librarymanagement.repository.book_report;

import com.library.librarymanagement.entity.BookReport;
import com.library.librarymanagement.entity.BorrowRecord;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface BookReportRepository extends JpaRepository<BookReport, Long>, JpaSpecificationExecutor<BookReport> {
    Page<BookReport> findByReporter_Id(Long id, Pageable pageable);

    Page<BookReport> findAll(Specification<BookReport> spec, Pageable pageable);
}
